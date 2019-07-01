package User;

import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import message.Message;

public class User {
    private String name;
    private int id;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * 初始化User类, 并设置socket连接
     * @param name
     * @param id
     * @param socket
     * @throws IOException
     */
    public User(String name, int id, Socket socket) throws IOException {
        this.name = name;
        this.id = id;
        this.socket = socket;
        in = new DataInputStream(this.socket.getInputStream());
        out = new DataOutputStream(this.socket.getOutputStream());
    }

    /**
     * 无信息初始化
     */
    public User() {
        this.name = "";
        this.id = 0;
    }

    /**
     * 设置用户昵称
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回用户昵称
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置用户id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 返回用户id, 每次聊天开始, id从1开始计数
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * 设置输入数据流
     * @param in
     */
    public void setIn(DataInputStream in) {
        this.in = in;
    }

    /**
     * 返回数据输入流
     * @return
     */
    public DataInputStream getIn() {
        return in;
    }

    /**
     * 设置数据输出流
     * @param out
     */
    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    /**
     * 返回数据输出流
     * @return
     */
    public DataOutputStream getOut() {
        return out;
    }

    /**
     * 设置socket
     * @param socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * 返回socket
     * @return
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * 返回用户字符串
     * @Override
     * @return
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
