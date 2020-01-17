package org.orangepalantir;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WidgetWindow {
    JLayeredPane panel = new JLayeredPane();


    public WidgetWindow(){
        panel.setSize(new Dimension(640, 640));
        panel.setBackground(Color.DARK_GRAY);
        panel.setOpaque(true);
    }

    public void addWidget(Widget widget){
        JPanel widgetPanel = widget.getPanel();
        panel.add(widgetPanel, JLayeredPane.DEFAULT_LAYER);


        widgetPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                Point origin = panel.getLocationOnScreen();

                int w = widgetPanel.getWidth();
                int h = widgetPanel.getHeight();
                widgetPanel.setLocation(p.x - w/2 - origin.x, p.y - h/2 - origin.y);
            }
        });

    }

    public static void main(String[] args){
        WidgetWindow ww = new WidgetWindow();
        JFrame frame = new JFrame();
        frame.setContentPane(ww.panel);
        CircleGuageWidget cgw = new CircleGuageWidget();
        cgw.initialize("test");
        cgw.panel.setSize(140, 140);
        ww.addWidget(cgw);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 640);
        frame.setVisible(true);



    }
}

interface Widget{
    JPanel getPanel();
    void addCloseListener(CloseListener cl);
}

interface CloseListener{
    public void close(Widget w);
}