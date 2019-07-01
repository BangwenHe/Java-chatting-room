package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import UI.UI;
import UI.CJButton;

public class MassDialog {
    private JDialog dialog;

    public MassDialog(ServerMainFrame server) {
        dialog = new JDialog(server, "输入群发消息", true);

        // 准备绘制Dialog
        UI.setUIFont();
        UI.setUIStyle();
        JPanel panel = new JPanel();
        JLabel label = new JLabel("请输入群发消息:");
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JButton confirmBtn = new CJButton("确定", new Color(52,152,219));
        JButton cancelBtn = new CJButton("取消", new Color(52,152,219));

        panel.setLayout(new GridBagLayout());
        panel.add(label, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
        panel.add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 5, 5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        panel.add(confirmBtn, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
        panel.add(cancelBtn, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("cancel button pressed!");
                dialog.dispose();
            }
        });
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo 为确定按钮添加群发事件
                server.sendMsgToAll(textArea.getText());
                textArea.setText("");
                dialog.dispose();
            }
        });

        dialog.setContentPane(panel);
        dialog.setBounds(server.getX() + 100,server.getY() + 100, server.getWidth(),server.getHeight());
        dialog.setVisible(true);
    }
}
