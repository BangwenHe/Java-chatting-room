package Server;

import java.util.ArrayList;
import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import User.User;

public class ServerThread extends Thread {
    private User user;
    private ArrayList<User> userList;
    private ServerMainFrame frame;

    /**
     * 通过用户对象以及用户列表实例化用户进程
     * @param user
     * @param allUser
     */
    public ServerThread(User user, ArrayList<User> allUser, ServerMainFrame frame) {
        this.user = user;
        this.userList = allUser;

        for (User temp: userList) {
            System.out.println(temp.getId() + ": " + temp.getName());
        }
        this.frame = frame;
    }

    @Override
    public void run() {
        try{
            while (true) {
                String msg = this.user.getIn().readUTF();
                parseMsg(msg);                  // 解析用户的数据格式
            }
        }
        catch (SocketException se) {           // 处理用户断开的异常
            System.out.println("user "+user.getName()+" logout.");
        }
        catch (Exception e) {                  // 处理其他异常
            e.printStackTrace();
        }
        finally {                               // 用户断开或者退出，需要把该用户移除并关闭socket
            try {
                remove(user);
                this.user.getIn().close();
                this.user.getSocket().close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 用正则表达式匹配数据的格式，根据不同的指令类型，来调用相应的方法处理
     * @param msg
     */
    private void parseMsg(String msg) {
        // 匹配指令部分
        Pattern pattern = Pattern.compile("<code>(.*)</code>");
        Matcher matcher = pattern.matcher(msg);
        String code = null;
        if (matcher.find()) {
            code = matcher.group(1);
        }

        // 匹配信息发送者
        pattern = Pattern.compile("<sender>(.*)</sender>");
        matcher = pattern.matcher(msg);
        String sender = null;
        if (matcher.find()) {
            sender = matcher.group(1);
        }

        // 匹配信息部分
        pattern = Pattern.compile("<message>(.*)</message>", Pattern.DOTALL);
        matcher = pattern.matcher(msg);
        String message = null;
        if(matcher.find()){
            message = matcher.group(1);
        }

        pattern = Pattern.compile("<receiver>(.*)</receiver>");
        matcher = pattern.matcher(msg);
        String receiver = null;
        if(matcher.find()){
            receiver = matcher.group(1);
        }

        pattern = Pattern.compile("<date>(.*)</date>");
        matcher = pattern.matcher(msg);
        String date = null;
        if(matcher.find()){
            date = matcher.group(1);
        }

        System.out.println("{code: " + code + ", sender: " + sender + ", message: " + message + ", receiver: " + receiver + ", date: " + date + "}");

        switch (code) {
            case "join":
                sendMsgExceptSelf(message, "所有人", "join", date);
                returnOnlineList();
                break;
            case "esc":
                sendMsgExceptSelf("", "所有人", "esc", date);
                frame.updateTextArea("[" + user.getName() + "] 于" + date + "下线\n");
                break;
            case "message":
                if (receiver.equals("所有人")) {
                    sendMsgExceptSelf(message, "所有人", "message", date);
                    frame.updateTextArea("[" + sender + "] " + date + ": \n" + message + "\n");
                } else {
                    sendPrivateMsg(message, receiver, "message", date);
                }
                break;
            default:
                System.out.println("not valid message from user " + this.user.getName());
                break;
        }
    }

    private void remove(User user) {
        frame.removeUser(user);
    }

    /**
     * 按照指定的格式发送信息
     * @param msg
     * @param receiver
     * @param code
     * @param date
     */
    private void sendMsgExceptSelf(String msg, String receiver, String code, String date) {
        for (User temp: userList) {
            if (!temp.getName().equals("所有人") && temp.getId() != this.user.getId()) {
                try {
                    DataOutputStream out = temp.getOut();
                    out.writeUTF("<code>" + code + "</code>" + "<sender>" + this.user.getName() + "</sender>" + "<message>" + msg + "</message>" + "<receiver>" + receiver + "</receiver>" + "<date>" + date + "</date>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向某个特定的用户发送信息
     * @param msg
     * @param receiver
     * @param code
     * @param date
     */
    private void sendPrivateMsg(String msg, String receiver, String code, String date) {
         for (User temp: userList) {
             if (temp.getName().equals(receiver)) {
                 try {
                     DataOutputStream out = temp.getOut();
                     out.writeUTF("<code>" + code + "</code>" + "<sender>" + this.user.getName() + "</sender>" + "<message>" + msg + "</message>" + "<receiver>" + receiver + "</receiver>" + "<date>" + date + "</date>");
                 } catch (SocketException se) {
                     userList.remove(temp.getName());
                     System.out.println(temp.getName() + "log out.");
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
    }

    /**
     * 返回在线用户列表
     */
    private void returnOnlineList() {
        try {
            String uStr = "";
            for (User temp: this.userList) {
                uStr = uStr + temp.getName() + "@@";
            }
            this.user.getOut().writeUTF("<code>list</code><sender></sender><message>" + uStr + "</message><receiver>所有人</receiver><date></date>");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
