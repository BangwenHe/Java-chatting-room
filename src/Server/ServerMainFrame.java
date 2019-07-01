package Server;

import User.User;
import UI.UI;
import UI.CJButton;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerMainFrame extends JFrame {
    private JLabel userLabel;
    private JList userlist;
    private JButton kickButton;
    private JTextPane textPane;
    private JButton button;
    private JTextField textIP;
    private JTextField textPort;
    private JMenuBar bar;

    private ArrayList<User> list;
    private int port;

    public ServerMainFrame(int port, ArrayList<User> l) {
        this.port = port;
        this.list = l;
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JPanel headPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        // 初始化整个窗口
        UI.setUIStyle();
        UI.setUIFont();
        UI.setWindowsBoundsCenter(this, 500, 500);
        this.setTitle("聊天室服务器");
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bar = new JMenuBar();
        addMenuBar(bar);
        this.setJMenuBar(bar);

        // 设置顶部panel
        JLabel IPLabel = new JLabel("IP地址");
        JLabel portLabel = new JLabel("端口号");
        textIP = new JTextField("127.0.0.1");
        textPort = new JTextField(Integer.toString(port));
        textIP.setPreferredSize(new Dimension(100, 25));
        textIP.setEditable(false);
        textPort.setPreferredSize(new Dimension(100, 25));
        textPort.setEditable(false);

        headPanel.setLayout(new FlowLayout());
        headPanel.add(IPLabel);
        headPanel.add(textIP);
        headPanel.add(portLabel);
        headPanel.add(textPort);

        // 设置左部用户列表
        userLabel = new JLabel("当前聊天室在线人数: " + (list.size() - 1));
        userlist = new JList();
        kickButton = new CJButton("踢出", new Color(52,152,219));
        userlist.setListData(list.toArray());
        JScrollPane userScrollPane = new JScrollPane(userlist);
        userScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(userLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        leftPanel.add(userScrollPane, new GridBagConstraints(0, 1, 1, 1, 10, 10, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        leftPanel.add(kickButton, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

        kickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo 为按钮添加踢人功能
                int index = userlist.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(ServerMainFrame.this, "对不起, 您未选中任何用户");
                } else if (index == 0) {
                    JOptionPane.showMessageDialog(ServerMainFrame.this, "对不起, \"所有人\"只是一个标识, 不是用户, 删除失败");
                } else {
                    int isKicked = JOptionPane.showConfirmDialog(ServerMainFrame.this, "确定踢出用户" + list.get(index).getName() + "?");
                    if (isKicked == 0) {
                        try {
                            String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());

                            for (User temp: list) {
                                DataOutputStream out = temp.getOut();
                                if (!temp.getName().equals("所有人"))
                                    out.writeUTF("<code>kick</code>" + "<sender>服务器</sender>" + "<message>" + list.get(index).getName() + "</message>" + "<receiver></receiver>" + "<date>" + date + "</date>");
                            }

                            System.out.println(list.get(index).getName() + "已被强制下线!");
                            updateTextArea(list.get(index).getName() + "于 " + date + " 被强制下线");
                            list.get(index).getSocket().close();
                            removeUser(list.get(index));
                        } catch (IOException ioe) {
                            System.out.println("强制下线失败");
                        }
                    }
                }
            }
        });

        // 设置右部用户聊天室消息
        JLabel msgLabel = new JLabel("聊天室消息列表");
        textPane = new JTextPane();
        textPane.setEditable(false);
        JScrollPane msgScrollPane = new JScrollPane();
        msgScrollPane.setViewportView(textPane);
        msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        button = new CJButton("群发消息", new Color(52,152,219));

        rightPanel.setLayout(new GridBagLayout());
        rightPanel.add(msgLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        rightPanel.add(msgScrollPane, new GridBagConstraints(0, 1, 1, 1, 10, 10, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        rightPanel.add(button, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 如何在监听器内获取外部的this指针
                new MassDialog(ServerMainFrame.this);
            }
        });

        // 设置所有panel布局
        panel.setLayout(new BorderLayout());
        panel.add(headPanel, BorderLayout.NORTH);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void addMenuBar(JMenuBar menuBar) {
        JMenu helpMenu = new JMenu("帮助");
        menuBar.add(helpMenu);
        Icon icon = new ImageIcon("C:\\Users\\asus\\eclipse-workspace\\CsuTeachingmanagerSpider-master\\Java课程设计\\src\\Server\\info.png");

        JMenuItem item1 = new JMenuItem("踢出", icon);
        JMenuItem item2 = new JMenuItem("群发消息", icon);
        helpMenu.add(item1);
        helpMenu.add(item2);

        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ServerMainFrame.this, "从列表中选择一项, 然后点击\"踢出\"按钮即可.", "踢出", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ServerMainFrame.this, "点击\"群发消息\"按钮, 会弹出一个对话框输入需要群发的消息", "群发消息", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void sendMsgToAll(String msg) {
        // todo 实现群发消息
        System.out.println("群发消息: " + msg);

        String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        updateTextArea("[服务器]于 " + date + " 群发消息: \n" + msg + '\n');
        for (User temp: list) {
            if (!temp.getName().equals("所有人")) {
                try {
                    DataOutputStream out = temp.getOut();
                    out.writeUTF("<code>broadcast</code>" + "<sender>服务器</sender>" + "<message>" + msg + "</message>" + "<receiver></receiver>" + "<date>" + date + "</date>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeUser(User u) {
        list.remove(u);
        userlist.setListData(list.toArray());
        userLabel.setText("当前聊天室在线人数: " + (list.size() - 1));
    }

    public void addUser() {
        userlist.setListData(list.toArray());
        userLabel.setText("当前聊天室在线人数: " + (list.size() - 1));
    }

    public void updateTextArea(String msg) {
        textPane.setText(textPane.getText() + msg);
    }
}
