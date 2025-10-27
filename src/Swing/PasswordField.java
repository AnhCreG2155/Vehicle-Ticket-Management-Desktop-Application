package Swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;

public class PasswordField extends JPasswordField {
    private Color background = Color.WHITE;
    private  final Image eye;
    private  final Image eye_hide;
    private  boolean IsHide = true;
    private boolean mouseOver = false;
  
    public PasswordField() {
        setBackground(new Color(255, 255, 255, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 50));
        setFont(new java.awt.Font("sanserif", 0, 14));
        setSelectionColor(new Color(80, 199, 255));
        eye = new ImageIcon(getClass().getResource("/Image/eye.png")).getImage();
        eye_hide = new ImageIcon(getClass().getResource("/Image/eye_hide.png")).getImage();
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                
                    int x = getWidth() - 30;
                if (new Rectangle(x, 0, 30, 30).contains(me.getPoint())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                mouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                int x = getWidth() - 30;
                    if (new Rectangle(x, 0, 30, 30).contains(me.getPoint())) {
                        IsHide = !IsHide;
                        if (IsHide) {
                            setEchoChar('*');
                        } else {
                            setEchoChar((char) 0);
                        }
                        repaint();
                }
            }
        });
    }
    
    private void createShowHide(Graphics2D g2) {
        int x = getWidth() - 30 + 5;
        int y = (getHeight() - 20) / 2;
        g2.drawImage(IsHide ? eye_hide : eye, x, y, null);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, width, height, 15, 15);
        super.paintComponent(g); 
            createShowHide(g2);
    }
}