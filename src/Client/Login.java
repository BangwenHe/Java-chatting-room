package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import Client.ClientMainFrame;
import User.User;
import UI.UI;
import UI.CJButton;

public class Login {
    private JFrame frame;
    private JTextField userText;
    private User user;
    private String addr;
    private int port;

    public Login() {
        addr = "127.0.0.1";
        port = 12345;

        initUI();
    }

    /**
     * 实现登录框, 用户可输入特定的IP地址以及端口号
     */
    public void initUI() {
        UI.setUIStyle();
        UI.setUIFont();

        // 设置整个登录框的大小
        frame = new JFrame();
        frame.setBounds(558, 304, 350, 216);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);

        // 创建菜单栏, 用户设置IP地址和端口号
        JMenuBar menuBar = new JMenuBar();
        addMenu(menuBar);
        frame.setJMenuBar(menuBar);

        // 创建昵称提示框
        JLabel userLabel = new JLabel("昵称");
        userLabel.setBounds(10,20,30,25);
        panel.add(userLabel);

        // 创建文本域用于用户输入
        userText = new JTextField(20);
        userText.setBounds(50,20,165,25);
        panel.add(userText);

        // 创建登录按钮
        JButton loginButton = new CJButton("登录", new Color(52,152,219));
        loginButton.setBounds(10, 65, 80, 25);
        loginButton.addActionListener(new ActionListener() {
            /**
             * 登录按钮的监听事件, 设置默认的IP地址与端口号, 设置用户的昵称
             * 与服务器通信后, 打开新的界面
             * @param e 监听事件
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addr.equals(""))
                    addr = "127.0.0.1";

                if (port < 1024)
                    port = 12345;

                System.out.println("{IP: " + addr + ", port: " + port + "}");

                // TO-DO list
                // 实现与服务器端的连接, 服务器端判断此昵称是否存在
                try {
                    user = new User();
                    user.setName(userText.getText());

                    // 注意try与catch的控制流, 如果先使用setSocket, 报错后会跳转至catch部分, 使得无法获得name
                    user.setSocket(new Socket(addr, port));

                    DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                    out.writeUTF(user.getName());

                    DataInputStream in = new DataInputStream(user.getSocket().getInputStream());
                    // 使用getBoolean方法是判断字符串是否为系统属性的字符串, 要想把字符串变成true/false, 使用parse/valueOf
                    boolean isUnique = Boolean.parseBoolean(in.readUTF());

                    // 判断用户名是否唯一
                    if (isUnique) {
                        new ClientMainFrame(user);
                        frame.setVisible(false);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "重复的昵称, 请重新选择");
                    }

                }
                catch (ConnectException exp) {
                    JOptionPane.showMessageDialog(null, "服务器未开启");
                }
                catch (IOException io) {
                    io.printStackTrace();
                }
            }
        });
        panel.add(loginButton);

        // 创建取消按钮, 直接退出
        JButton exitButton = new CJButton("取消", new Color(52,152,219));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(exitButton);

        frame.getRootPane().setDefaultButton(loginButton);
        panel.setBackground(Color.GREEN);
        frame.setVisible(true);
    }

    /**
     * 实现菜单栏, 添加菜单组件
     * @param menuBar 菜单栏
     */
    private void addMenu(JMenuBar menuBar) {
        //添加选项菜单以及帮助菜单
        JMenu opMenu = new JMenu("选项");
        JMenu hMenu = new JMenu("帮助");
        menuBar.add(opMenu);
        menuBar.add(hMenu);

        //添加IP地址菜单控件
        JMenuItem ipItem = new JMenuItem("IP地址");
        opMenu.add(ipItem);
        ipItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addr = JOptionPane.showInputDialog(null, "请输入IP地址：\n", "127.0.0.1");
                try {
                    addr.length();
                } catch (NullPointerException n) {
                    addr = "127.0.0.1";
                }
            }
        });

        //添加端口菜单控件
        JMenuItem portItem = new JMenuItem("端口");
        opMenu.add(portItem);
        portItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String portStr = JOptionPane.showInputDialog(null, "请输入端口号：\n", "12345");
                try{
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException exp) {
                    port = 12345;
                }

            }
        });

        //添加帮助菜单控件
        JMenuItem hItem = new JMenuItem("帮助");
        hMenu.add(hItem);
        hItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "1. 通过输入唯一的昵称, 您可以登录进入聊天室\n" +
                        "2. 通过菜单栏的选项按钮, 您可以指定IP地址以及端口号\n" +
                        "3. 默认IP为127.0.0.1, 默认端口为12345",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }
}
