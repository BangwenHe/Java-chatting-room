package Client;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
/**
 *
 * @author lannooo
 *
 */
public class IconDialog implements ActionListener {

    private JDialog dialog;
    private ClientMainFrame client;
    /**
     * 通过frame和客户端对象来构造
     * @param frame
     * @param client
     */
    public IconDialog(JFrame frame, ClientMainFrame client) {
        this.client = client;
        dialog = new JDialog(frame, "请选择表情", true);

        // 16个表情
        JButton[] icon_button = new JButton[16];
        ImageIcon[] icons = new ImageIcon[16];
        // 获得弹出窗口的容器，设置布局
        Container dialogPane = dialog.getContentPane();
        dialogPane.setLayout(new GridLayout(0, 4));
        // 加入表情
        for(int i=1; i<=15; i++) {
            icons[i] = new ImageIcon("C:\\Users\\asus\\eclipse-workspace\\CsuTeachingmanagerSpider-master\\Java课程设计\\src\\Client\\icon\\"+i+".png");
            icons[i].setImage(icons[i].getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
            icon_button[i] = new JButton("" + i, icons[i]);
            icon_button[i].addActionListener(this);

            icon_button[i].setContentAreaFilled(false);
            icon_button[i].setFocusPainted(false);
            icon_button[i].setPressedIcon(new ImageIcon("C:\\Users\\asus\\eclipse-workspace\\CsuTeachingmanagerSpider-master\\Java课程设计\\src\\Client\\icon\\"+i+".png"));

            dialogPane.add(icon_button[i]);
        }
        dialog.setBounds(200,266,266,280);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 构造emoji结构的消息发送, 并关闭窗口
        String cmd = e.getActionCommand();
        dialog.dispose();
        //client.sendMsg("<emoji>"+cmd+"</emoji>", "message");
    }

}