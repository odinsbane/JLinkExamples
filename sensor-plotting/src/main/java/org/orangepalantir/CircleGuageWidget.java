package org.orangepalantir;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class CircleGuageWidget implements Widget{
    String name;
    double minTheta = Math.PI/4;
    double maxTheta = 3*Math.PI/4;
    double highValue = 1;
    double lowValue = 0;
    double value;


    CircleGuageWidget.PlotPanel panel;

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void addCloseListener(CloseListener cl) {
        cl.close(this);
    }

    class PlotPanel extends JPanel {
        int padding = 15;
        int delta = 3; //inset padding.

        Color bg = new Color(50, 0, 0);
        PlotPanel(){
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
            g2d.setComposite(AlphaComposite.Clear);
            g.setColor(new Color(0,0,0,0));
            g.fillRect(0, 0, w, h);
            g2d.setComposite(AlphaComposite.SrcOver);
            int drawLength = w<h? w: h - 2*padding;

            int padx = (w - drawLength)/2;
            int pady = (h - drawLength)/2;



            g.setColor(bg);
            g.fillOval(padx, pady, drawLength, drawLength);

            g.setColor(Color.WHITE);
            g.drawOval(padding + delta, padding + delta, drawLength-2*delta, drawLength - 2*delta);

            g.drawString("" + value, w/2,  h/2);
            g.setColor(Color.WHITE);

            int cx0 = w + delta - padding;
            int cx1 = w - delta;
            int cy0 = delta;
            int cy1 = padding - delta;

            g.drawRect(cx0, cy0, padding - 2*delta, padding - 2*delta);
            g.drawLine(cx0, cy0, cx1, cy1);
            g.drawLine(cx0, cy1, cx1, cy0);

            double theta = (maxTheta - minTheta)*(value - lowValue)/(highValue - lowValue) + minTheta;

            theta = theta<minTheta?minTheta:theta;
            theta = theta>maxTheta?maxTheta:theta;

            int ox = w/2;
            int oy = h - pady - 2*delta;
            double radius = drawLength*0.75;
            double x = Math.cos(theta)*radius;
            double y = Math.sin(theta)*radius;
            g.setColor(Color.YELLOW);
            g.drawLine(ox, oy, ox - (int)x, oy - (int)y);

            double r2 = radius*0.8;
            g.setColor(Color.RED);
            Ellipse2D e1 = new Ellipse2D.Double(ox - r2 * Math.cos(minTheta)-delta, oy - r2*Math.sin(minTheta)-delta, 2*delta, 2*delta);
            Ellipse2D e2 = new Ellipse2D.Double(ox - r2 * Math.cos(maxTheta)-delta, oy - r2*Math.sin(maxTheta)-delta, 2*delta, 2*delta);
            g2d.fill(e1);
            g2d.fill(e2);
        }

    }

    public CircleGuageWidget(){

    }

    public void initialize(String name){
        this.name = name;
        panel = new CircleGuageWidget.PlotPanel();

    }


    public void show(){

        JFrame frame = new JFrame(name + " :: guage");

        frame.setUndecorated(true);
        frame.setContentPane(panel);
        frame.setSize(180, 180);
        frame.setVisible(true);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int w = panel.getWidth();
                int lb = w - panel.padding + panel.delta;
                int ub = w - panel.delta;
                System.out.println(x + ", " + y + "\t" + lb + ", " + ub);
                panel.repaint();
                if(x>=lb && x<= ub && y>=panel.delta && y<=panel.delta + panel.padding){
                    frame.setVisible(false);
                }
            }
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

    public void setDomain(double min, double max){
        lowValue = min;
        highValue = max;
    }
    public void setValue(double newData){
        value = newData;
        panel.repaint();
    }

}
