package Client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientThread extends Thread{
    private Socket socket;
    private ClientMainFrame client;
    private DataInputStream in;

    /**
     * 从过主线程传入的socket和client对象来构造
     *
     * @param socket
     * @param client
     */
    public ClientThread(Socket socket, ClientMainFrame client) {
        this.client = client;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("cannot get inputstream from socket.");
        }
    }

    /**
     * 读数据, 并调用处理方法
     */
    public void run() {
        try {
            in = new DataInputStream(this.socket.getInputStream());
            while (true) {
                try {
                    String msg = in.readUTF();
                    parseMessage(msg);
                } catch (EOFException eof) {
                    System.out.println("用户被强制下线.");
                    this.socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseMessage(String message) {
        String code = null;
        String msg = null;
        String sender = null;
        String receiver = null;
        String date = null;
        /*
         * 先用正则表达式匹配code码和msg内容
         */
        if (message.length() > 0) {
            Pattern pattern = Pattern.compile("<code>(.*)</code>");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                code = matcher.group(1);
            }

            pattern = Pattern.compile("<message>(.*)</message>", Pattern.DOTALL);
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                msg = matcher.group(1);
            }

            pattern = Pattern.compile("<sender>(.*)</sender>");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                sender = matcher.group(1);
            }

            pattern = Pattern.compile("<receiver>(.*)</receiver>");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                receiver = matcher.group(1);
            }

            pattern = Pattern.compile("<date>(.*)</date>");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                date = matcher.group(1);
            }

            System.out.println("{code: " + code + ", sender: " + sender + ", message: " + msg + ", receiver: " + receiver + ", date: " + date + "}");

            switch (code) {
                case "join":
                    System.out.println(sender + " log in.");
                    client.updateTextArea("所有人", "[" + sender + "] 于" + date + "上线\n");
                    client.addToUserList(sender);
                    client.setOnlineNumber();

                    break;
                case "esc":
                    // 发送到聊天室内, 聊天室的昵称就是所有人
                    System.out.println(sender + " log out.");
                    client.updateTextArea("所有人", "[" + sender + "] 于" + date + "下线\n");
                    if (client.isExist(client.getChatingList(), sender)) {
                        client.updateTextArea(sender, "[" + sender + "] 于" + date + "下线\n");
                    }

                    client.removeFromUserList(sender);
                    client.setOnlineNumber();

                    break;
                case "message":
                    if (receiver.equals(this.client.getName()))
                        client.updateTextArea(sender, "[" + sender + "] " + date + ": \n" + msg + "\n");
                    else
                        client.updateTextArea("所有人", "[" + sender + "] " + date + ": \n" + msg + "\n");
                    break;
                case "list":
                    String[] nickname = msg.split("@@");
                    for (String name: nickname) {
                        client.addToUserList(name);
                    }
                    client.setOnlineNumber();
                    break;
                case "broadcast":
                    // todo 输出服务器群发消息
                    client.updateTextArea("所有人", "[服务器]于 " + date + " 群发消息: \n" + msg + "\n");
                    break;
                case "kick":
                    // 踢人消息
                    if (msg.equals(this.client.getName()))
                        JOptionPane.showMessageDialog(client, "你已被服务器强制下线!");
                    else {
                        client.updateTextArea("所有人", "[服务器] " + date + ": \n" + msg + "被强制下线\n");
                        client.removeFromUserList(msg);
                        client.setOnlineNumber();
                    }
                    break;
                default:
                    System.out.println("wrong message from " + sender);
                    break;
            }
        }
    }
}
