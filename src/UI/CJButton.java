package UI;

import javax.swing.*;
import java.awt.*;

public class CJButton extends JButton{
    public CJButton(String text)
    {
        setFocusPainted(false);
        setBorderPainted(false);

        setPreferredSize(new Dimension(60, 30));
        setMargin(new Insets(0, 0, 0, 0));
        setText(text);
    }

    public CJButton(String text, Dimension d)
    {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        setPreferredSize(d);
        setMargin(new Insets(0, 0, 0, 0));
        setText(text);
    }

    public CJButton(String text, Color color)
    {
        setFocusPainted(false);
        setBorderPainted(false);

        setPreferredSize(new Dimension(60, 30));
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(color);
        setText(text);
    }
}
