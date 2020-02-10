package org.orangepalantir;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WidgetWindow {
    JLayeredPane panel = new JLayeredPane();
    String title;
    List<Widget> widgets = new ArrayList<>();
    public WidgetWindow(String title){
        this.title = title;
        panel.setSize(new Dimension(640, 640));
        panel.setBackground(Color.BLACK);
        panel.setOpaque(true);
    }

    public void addWidget(Widget widget){
        JPanel widgetPanel = widget.getPanel();
        panel.add(widgetPanel, JLayeredPane.DEFAULT_LAYER);
        widgets.add(widget);


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


        widgetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    Widget cfg = widget.getConfig();


                    panel.add(cfg.panel, JLayeredPane.MODAL_LAYER);
                    cfg.panel.setLocation(widgetPanel.getLocation());

                    cfg.addCloseListener(w->{
                        cfg.panel.setVisible(false);
                        panel.remove(cfg.panel);
                    });

                }
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
        CyclicPlotWindow window = new CyclicPlotWindow("testing", 5000);
        FileMonitor monitor = new FileMonitor(path);
        monitor.setSync(true); //possibly switch to have a listener?
        panel.add(window.panel);
    }
    public JFrame show(){
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.setSize(640, 640);
        frame.setVisible(true);
        return frame;
    }
    public static void main(String[] args){
        WidgetWindow ww = new WidgetWindow("");
        ww.show();
    }

    public void pack(){
        int w = panel.getWidth();
        int h = panel.getHeight();
        int rowHMax = 0;
        int rowX = 0;
        int rowY = 0;
        for(Widget widget: widgets){
            JPanel p = widget.getPanel();
            int wh = p.getHeight();
            int ww = p.getWidth();
            int nx = rowX + ww;
            if(nx > w + 0.2*ww){
                //new row.
                rowY += rowHMax;
                rowX = 0;
                rowHMax = wh;
            }

            if(wh>rowHMax){
                rowHMax = wh;
            }
            p.setLocation(rowX, rowY);
            rowX += ww;
        }
    }
}

interface CloseListener{
    public void close(Widget w);
}