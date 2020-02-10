package org.orangepalantir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Widget implements MouseListener {
    protected int padding = 15;
    protected int delta = 3; //inset padding.
    List<CloseListener> listeners = new ArrayList<>();
    Rectangle2D closeBox = new Rectangle2D.Double(0, 0, 0, 0);
    WidgetPanel panel;
    final String name;

    public Widget(String name){
        this.name = name;
        panel = new WidgetPanel();
        panel.addMouseListener(this);

    }
    public Widget(){
        this("");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(closeBox.contains(e.getPoint())){
            close();
            e.consume();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    class WidgetPanel extends JPanel {

        WidgetPanel(){
            setOpaque(false);

        }
        
        /**
         * fills the space with a black rectangle and a white x-box in the top right corner. The center is then filled
         * with a dark red, and data points are plotted.
         *
         * @param g
         */
        @Override
        public void paintComponent(Graphics g){
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2d = (Graphics2D)g;

            //g2d.setComposite(AlphaComposite.Clear);
            g.setColor(new Color(0,0,0,0));
            g.fillRect(0, 0, w, h);
            g2d.setComposite(AlphaComposite.SrcOver);

            g.setColor(Color.WHITE);

            int cx0 = w + delta - padding;
            int cx1 = w - delta;
            int cy0 = delta;
            int cy1 = padding - delta;
            closeBox.setRect(cx0, cy0, padding - 2*delta, padding - 2*delta);
            g.drawRect(cx0, cy0, padding - 2*delta, padding - 2*delta);

            g.drawLine(cx0, cy0, cx1, cy1);
            g.drawLine(cx0, cy1, cx1, cy0);

            Font font = new Font(Font.MONOSPACED, Font.PLAIN, padding - 2*delta);
            FontMetrics fm = g.getFontMetrics(font);
            int titleWidth = fm.charsWidth(name.toCharArray(), 0, name.length());
            int center = w/2;
            ((Graphics2D) g).drawString(name, center - titleWidth/2, h - delta);
            g.setFont(font);
            paintWidget((Graphics2D)g);


        }

    }
    public Widget getConfig(){
        Widget config = new Widget("not configurable");
        config.panel.setOpaque(true);
        config.panel.setSize(panel.getSize());
        return config;
    }

    public JPanel getPanel(){
        return panel;
    }


    public void addCloseListener(CloseListener cl){
        listeners.add(cl);
    }

    public void close(){
        for(CloseListener l: listeners){
            l.close(this);
        }
        panel.setVisible(false);
    }

    /**
     * Creates an independent JFrame to show the widget.
     */
    public void show(){

        JFrame frame = new JFrame(name );

        frame.setUndecorated(true);
        frame.setContentPane(panel);
        frame.setSize(panel.getSize());
        frame.setVisible(true);
        frame.setBackground(Color.BLACK);
        addCloseListener(w->{
            frame.setVisible(false);
            frame.dispose();
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                int w = frame.getWidth();
                int h = frame.getHeight();
                frame.setLocation(p.x - w/2, p.y - h/2);
            }
        });

    }

    public void paintWidget(Graphics2D g2d){

    }
}
