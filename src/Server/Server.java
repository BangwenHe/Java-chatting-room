package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

import User.User;

public class Server extends JFrame {
    private ServerSocket ss;
    private ArrayList<User> allUsers;
    private int port;
    private int userCount;
    private final int MAX_USERS = 20;
    private ServerMainFrame frame;

    /**
     * 通过端口号新建一个服务器对象
     * @param port
     * @throws IOException
     */
    public Server(int port) throws IOException {
        // todo 链接server和serverMainframe
        this.allUsers = new ArrayList<User>();

        User all = new User();
        all.setName("所有人");
        this.allUsers.add(all);

        this.port = port;
        this.userCount = 0;
        this.ss = new ServerSocket(this.port);

        System.out.println("服务器已开启");
        frame = new ServerMainFrame(this.port, this.allUsers);
    }

    /**
     * 返回下一个可用的用户id
     * @return
     */
    public int getNextId() {
        if (allUsers.size() < MAX_USERS) {
            return ++this.userCount;
        }
        else {
            return -1;
        }
    }

    /**
     * 监听端口, 如果收到新的用户连接, 则创建一个新的用户线程用于处理用户的请求
     * @throws Exception
     */
    public void startListen() throws Exception {
        while (true) {
            // 有新用户发起连接请求
            Socket socket = ss.accept();

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String name = in.readUTF();
            //判断用户名是否唯一
            if (isUnique(name)) {   //唯一
                int id = getNextId();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("true");

                if (id != -1) {     //登录
                    User user = new User(name, id, socket);
                    allUsers.add(user);
                    frame.addUser();
                    String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                    frame.updateTextArea("[" + user.getName() + "] 于" + date + "上线\n");

                    ServerThread thread = new ServerThread(user, allUsers, frame);
                    thread.start();
                }
                else {
                    System.out.println("服务器已满");
                    socket.close();
                }
            }
            else {                  //不唯一
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("false");
            }
        }
    }

    /**
     * 从所有用户列表中查找是否有重复的名称, 若有, 则返回false(不唯一), 若无, 则返回true(唯一)
     * @param name
     * @return
     */
    private boolean isUnique(String name) {
        for (User temp: allUsers) {
            if (temp.getName().equals(name)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 测试函数
     * @param args
     */
    public static void main(String[] args) {
        try {
            Server server = new Server(12345);
            server.startListen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
