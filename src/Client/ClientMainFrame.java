package Client;

import User.User;
import UI.UI;
import UI.CJButton;
import UI.CJTabbedPane;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientMainFrame extends JFrame {
    private JMenuBar menubar;
    private User user;
    private JTextField hostTextField;
    private JTextField portTextField;
    private JTextField nameTextField;
    private JTextArea textArea;
    private JLabel userLabel;
    private JList userList;
    private JPanel msgPanel;
    private DefaultListModel<String> userModel;
    private JTabbedPane tab;
    private ArrayList<String> chatingList;

    /**
     * 初始化主窗口类
     * @param user
     */
    public ClientMainFrame(User user) {
        this.user = user;

        chatingList = new ArrayList<String>();
        this.chatingList.add("所有人");
        initialize();

        sendMsg("join", this.user.getName(), "", "所有人", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        ClientThread thread = new ClientThread(this.user.getSocket(), this);
        thread.start();
    }

    /**
     * 发送特定格式的信息到服务器
     * @param code
     * @param sender
     * @param message
     * @param receiver
     * @param date
     */
    public void sendMsg(String code, String sender, String message, String receiver, String date) {
        try {
            DataOutputStream out = new DataOutputStream(this.user.getSocket().getOutputStream());
            out.writeUTF("<code>" + code + "</code>" + "<sender>" + sender + "</sender>" + "<message>" + message + "</message>" + "<receiver>" + receiver + "</receiver>" + "<date>" + date + "</date>");
        } catch (SocketException se) {
            System.out.println("用户已下线");
            insertTabMsg(tab.getSelectedIndex(), "发送失败\n-------------你已下线-------------\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置窗口的UI, 初始化
     */
    private void initialize() {
        // 设置窗口的UI样式和字体
        UI.setUIStyle();
        UI.setUIFont();

        menubar = new JMenuBar();
        addMenuBar(menubar);
        this.setJMenuBar(menubar);

        // 主要的panel, 下层是消息区, 左边是用户列表, 中间是消息面板
        JPanel panel = new JPanel();
        JPanel headpanel = new JPanel();        //上层panel
        JPanel footpanel = new JPanel();        //下层panel, 用于放置发送信息的组件
        JPanel leftpanel = new JPanel();        //左边panel, 用于防止用户列表
        msgPanel = new JPanel();

        BorderLayout layout = new BorderLayout();           //东南西北中
        GridBagLayout gridBagLayout = new GridBagLayout();  //格子布局, 用于南部
        FlowLayout flowLayout = new FlowLayout();           //流式布局

        // 设置窗口大小
        UI.setWindowsBoundsCenter(this, 538, 700);
        this.setTitle(this.user.getName() + "的聊天室");
        this.setContentPane(panel);
        this.setLayout(layout);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置各个部分的panel的布局和大小
        headpanel.setLayout(flowLayout);
        footpanel.setLayout(gridBagLayout);
        leftpanel.setLayout(gridBagLayout);
        msgPanel.setLayout(new BorderLayout());
        leftpanel.setPreferredSize(new Dimension(130, 0));

        // 放置headpanel组件
        hostTextField = new JTextField(this.user.getSocket().getInetAddress().toString().substring(1));
        portTextField = new JTextField(Integer.toString(this.user.getSocket().getPort()));
        nameTextField = new JTextField(this.user.getName());
        hostTextField.setPreferredSize(new Dimension(100, 25));
        hostTextField.setEditable(false);
        portTextField.setPreferredSize(new Dimension(70, 25));
        portTextField.setEditable(false);
        nameTextField.setPreferredSize(new Dimension(120, 25));
        nameTextField.setEditable(false);

        JLabel hostLabel = new JLabel("服务器IP");
        JLabel portLabel = new JLabel("端口");
        JLabel nameLabel = new JLabel("昵称");

        headpanel.add(hostLabel);
        headpanel.add(hostTextField);
        headpanel.add(portLabel);
        headpanel.add(portTextField);
        headpanel.add(nameLabel);
        headpanel.add(nameTextField);

        // 放置footpanel组件
        JButton footSend = new CJButton("发送", new Color(52,152,219));

        textArea = new JTextArea();
        textArea.setSelectionColor(Color.RED);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BorderLayout());
        btnPanel.add(footSend, BorderLayout.SOUTH);

        footpanel.setPreferredSize(new Dimension(-1, 80));
        footpanel.add(textScrollPane, new GridBagConstraints(0, 0, 2, 2, 100, 100, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 10));
        footpanel.add(btnPanel, new GridBagConstraints(2, 0, 1, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        // 为发送按钮footsend添加监听
        footSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                textArea.setText("");
                Date day=new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // 判断用户是否在线
                if (userModel.contains(tab.getTitleAt(tab.getSelectedIndex()))) {
                    insertTabMsg(tab.getSelectedIndex(), "[你] " + df.format(day) + ":\n" + text + '\n');
                    sendMsg("message", user.getName(), text, tab.getTitleAt(tab.getSelectedIndex()), df.format(day));
                } else {
                    insertTabMsg(tab.getSelectedIndex(), "[你] " + df.format(day) + ":\n" + text + '\n' +
                            "发送失败\n" +
                            "-------------" + tab.getTitleAt(tab.getSelectedIndex()) + "已下线-------------\n");
                }
            }
        });
        // 为textArea添加回车监听, 回车直接发送
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int code = e.getKeyChar();

                if(code == '\n' && e.isControlDown())
                    textArea.setText(textArea.getText() + '\n');
                else if (code == '\n') {
                    try {
                        textArea.setText(textArea.getText(0, textArea.getText().length() - 1));
                        footSend.doClick();
                    } catch (BadLocationException ble) {
                        System.out.println("bad location in textarea");
                    }
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // 放置leftpanel组件
        userLabel = new JLabel("聊天室内人数: 0");

        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        userList.setBackground(new Color(52,152,219));

        JScrollPane userListPane = new JScrollPane(userList);

        leftpanel.add(userLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(userListPane,new GridBagConstraints(0, 1, 1, 1, 100, 100, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String name = (String)userList.getSelectedValue();
                // 点击用户列表, 打开一个新的tab聊天界面, 但是不能是自己
                if (userList.getValueIsAdjusting() && !name.equals(ClientMainFrame.this.getName()))
                {
                    System.out.println(name);

                    if (isExist(chatingList, name)) {
                        for (int i = 0; i < tab.getTabCount(); i++) {
                            if (tab.getTitleAt(i).equals(name)) {
                                tab.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    else {
                        chatingList.add(name);

                        JPanel userPanel = new JPanel();
                        addScrollPane(userPanel);
                        tab.add(userPanel, name);
                        addCloseBtn(tab, userPanel, name);
                    }
                }
            }
        });

        // 放置msgPanel组件
        tab = new JTabbedPane(JTabbedPane.TOP);
        tab.setUI(new CJTabbedPane());
        JLabel msgLabel = new JLabel("消息记录");

        JPanel all = new JPanel();
        addScrollPane(all);
        tab.add(all, "所有人");

        msgPanel.add(msgLabel, BorderLayout.NORTH);
        msgPanel.add(tab, BorderLayout.CENTER);

        // 设置顶层布局
        panel.add(headpanel, "North");
        panel.add(footpanel, "South");
        panel.add(leftpanel, "West");
        panel.add(msgPanel, "Center");

        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                sendMsg("esc", user.getName(), "", "", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
                System.exit(0);
            }
        });
    }

    /**
     * 添加菜单
     * @param menuBar
     */
    private void addMenuBar(JMenuBar menuBar) {
        JMenu optionMenu = new JMenu("选项");
        JMenu helpMenu = new JMenu("帮助");
        menuBar.add(optionMenu);
        menuBar.add(helpMenu);

        JMenuItem optionItem1 = new JMenuItem("选项");
        optionMenu.add(optionItem1);

        JMenuItem helpItem1 = new JMenuItem("帮助");
        helpMenu.add(helpItem1);
    }

    /**
     * 添加消息面板
     * @param panel
     */
    private void addScrollPane(JPanel panel) {
        JTextPane msgArea = new JTextPane();
        msgArea.setBackground(Color.CYAN);
        msgArea.setEditable(false);
        JScrollPane msgScrollPane = new JScrollPane();
        msgScrollPane.setViewportView(msgArea);
        msgScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // 添加至panel
        panel.setLayout(new BorderLayout());
        panel.add(msgScrollPane, BorderLayout.CENTER);
    }

    /**
     * 为JTabbedPane中的JPanel添加关闭按钮, 组件将呈现标题, 并且 JtabbedPane 将不会呈现标题或图标
     * @param tab
     * @param jPanel
     * @param text
     */
    private void addCloseBtn(JTabbedPane tab, JPanel jPanel, String text) {
        JPanel pane = new JPanel();
        pane.setOpaque(false);// 设置jpanel面板背景透明

        JLabel title = new JLabel(text);
        pane.add(title);

        JButton close = new CJButton("X", new Dimension(20, 20));
        close.setForeground(Color.LIGHT_GRAY);
        pane.add(close);

        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                close.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                close.setForeground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                chatingList.remove(tab.getTitleAt(tab.indexOfComponent(jPanel)));
                tab.remove(tab.indexOfComponent(jPanel));

                // 所有tab都关闭了，整个tabpane也要关闭
                if (tab.getTabCount() <= 0) {
                    System.exit(0);
                }
            }
        });

        // jdk1.6之后的新特性，也是实现这个功能的核心所在，在相应下表的tab出放置任何组件
        tab.setTabComponentAt(tab.indexOfComponent(jPanel), pane);
    }

    /**
     * 判断数组中是否存在o元素
     * @param list 是否存在的数组
     * @param o 判断的对象
     * @return o是否在list中存在
     */
    public boolean isExist(ArrayList list, Object o) {
        for (Object temp: list) {
            if (temp.equals(o)) {
                return true;
            }
        }

        return false;
    }

    private void insertTabMsg(int index, String msg) {
        JComponent com = (JComponent) tab.getComponentAt(index);
        JComponent comInner = (JComponent) com.getComponent(0);
        JViewport view = (JViewport) comInner.getComponent(0);
        JTextPane area = (JTextPane) view.getComponent(0);

        area.setText(area.getText() + msg);
    }

    /**
     * 更新消息面板, 对于特定的发送者
     * @param name 发送者
     * @param msg 信息
     */
    public void updateTextArea(String name, String msg) {
        if (isExist(chatingList, name)) {
            int index = tab.indexOfTab(name);
            tab.setSelectedIndex(index);

            insertTabMsg(index, msg);
        } else {
            JPanel panel = new JPanel();
            addScrollPane(panel);
            chatingList.add(name);
            tab.add(panel, name);
            addCloseBtn(tab, panel, name);

            int index = tab.indexOfTab(name);
            tab.setSelectedIndex(index);

            insertTabMsg(index, msg);
        }
    }

    /**
     * 添加一个姓名到在线列表中
     * @param name 添加的姓名
     */
    public void addToUserList(String name) {
        userModel.addElement(name);
        userList.setModel(userModel);
    }

    /**
     * 从在线列表中删除一个姓名
     * @param name 删除的姓名
     */
    public void removeFromUserList(String name) {
        userModel.removeElement(name);
        userList.setModel(userModel);
    }

    /**
     * 修改聊天室内人数
     */
    public void setOnlineNumber() {
        userLabel.setText("聊天室内人数: " + (userModel.getSize() - 1));
    }

    /**
     * 得到该用户的昵称
     * @return 昵称
     */
    public String getName() {
        return this.user.getName();
    }

    /**
     * 得到正在聊天的用户列表, 即显示在tab中的列表
     * @return chat list
     */
    public ArrayList getChatingList() {
        return this.chatingList;
    }
}

/*
 * 踢人功能
 */