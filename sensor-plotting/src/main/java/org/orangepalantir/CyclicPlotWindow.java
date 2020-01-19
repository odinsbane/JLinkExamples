package org.orangepalantir;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A small window/frame that keeps track of n points in a cycle. ie it has a cursor that represents the first point, and
 * each time data is added that cursor is moved forward one.
 */
public class CyclicPlotWindow implements Widget{
    List<double[]> data;
    List<String> names;
    int points;
    int cursor = 0;
    PlotPanel panel;

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void addCloseListener(CloseListener cl) {
        //do stuff.
    }

    class PlotPanel extends JPanel {
        int padding = 15;
        int delta = 3; //inset padding.

        double highT = 120;
        double lowT = 0;
        Color bg = new Color(50, 0, 0);

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
            int lc = cursor;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);

            int dw = w - 2*padding;
            int dh = h - 2*padding;

            g.setColor(bg);
            g.fillRoundRect(padding, padding, dw, dh, 5, 5);

            g.setColor(Color.WHITE);
            g.drawRoundRect(padding, padding, dw, dh, 5, 5);


            int cx0 = w + delta - padding;
            int cx1 = w - delta;
            int cy0 = delta;
            int cy1 = padding - delta;

            g.drawRect(cx0, cy0, padding - 2*delta, padding - 2*delta);
            g.drawLine(cx0, cy0, cx1, cy1);
            g.drawLine(cx0, cy1, cx1, cy0);



            double xf = dw*1.0/(points-1);
            double yf = dh/(highT - lowT);

            g.setColor(Color.RED);
            int y100 = h - padding - (int)(yf*100);
            g.drawLine( padding+1, y100, w-padding -1, y100 );

            for(int j = 0; j<data.size(); j++){
                if(names.get(j).contains("loss")){
                    g.setColor(Color.BLUE);
                } else{
                    g.setColor(Color.GREEN);
                }
                double[] line = data.get(j);
                for(int i = 0; i<points-1; i++){

                    int i0 = (i+lc)%points;
                    int i1 = (i+lc + 1)%points;

                    int x0 = (int)(i*xf + padding);
                    int y0 = (int)(h - padding - (line[i0] - lowT)*yf);

                    int x1 = (int)((i+1)*xf + padding);
                    int y1 = (int)(h - padding - (line[i1] - lowT)*yf);

                    g.drawLine(x0, y0, x1, y1);
                }
            }


        }

    }

    public CyclicPlotWindow(int n){
        points = n;
    }

    public void initialize(List<String> names){
        this.names = new ArrayList<>(names);
        data = new ArrayList<>();
        panel = new PlotPanel();

        for(String s: names){
            data.add(new double[points]);
        }

    }

    public void setPlotRange(double low, double high){
        panel.highT = high;
        panel.lowT = low;
    }

    public void show(){

        JFrame frame = new JFrame("sensors");
        frame.setUndecorated(true);
        frame.setContentPane(panel);
        frame.setSize(320, 240);
        frame.setVisible(true);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int w = panel.getWidth();
                int h = panel.getHeight();

                int lb = w - panel.padding + panel.delta;
                int ub = w - panel.delta;

                if(x>=lb && x<= ub && y>=panel.delta && y<=panel.delta + panel.padding){
                    frame.setVisible(false);
                    System.exit(0);
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

    public void addData(double[] newData){
        for(int i = 0; i<newData.length; i++){
            data.get(i)[cursor] = newData[i];
        }
        cursor = (cursor+1)%points;
        panel.repaint();
    }

}
