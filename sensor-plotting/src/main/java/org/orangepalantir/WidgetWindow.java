package org.orangepalantir;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Random;

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

    /**
     * Creates a plot widget and ad guage widget for each
     *
     * @param path
     */
    public void addFileMonitoringPanel(Path path){
        // LogFilePlottingEngine
        // gets a FileMonitor and a CyclicPlotWindow that get updated.
        CyclicPlotWindow window = new CyclicPlotWindow(5000);
        FileMonitor monitor = new FileMonitor(path);
        monitor.setSync(true); //positlby switch to have a listener?
        panel.add(window.panel);
    }

    public static void main(String[] args){
        WidgetWindow ww = new WidgetWindow();
        JFrame frame = new JFrame();
        frame.setContentPane(ww.panel);
        CircleGuageWidget cgw = new CircleGuageWidget();
        cgw.initialize("test");
        cgw.panel.setSize(280, 280);

        CircleGuageWidget cgw2 = new CircleGuageWidget();
        cgw2.initialize("test");
        cgw2.panel.setSize(280, 280);
        cgw.setDomain(0, 5000);
        cgw2.setDomain(0, 5000);

        ww.addWidget(cgw);
        ww.addWidget(cgw2);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 640);
        frame.setVisible(true);

        new Thread(()->{
            Random ng = new Random();
            int i = 0;
            while(true){
                i = i + 1;
                int delta = (int)(100 * ng.nextGaussian());
                cgw.setValue(i + delta);
                cgw2.setValue(delta*delta);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

interface Widget{
    JPanel getPanel();
    void addCloseListener(CloseListener cl);
}

interface CloseListener{
    public void close(Widget w);
}