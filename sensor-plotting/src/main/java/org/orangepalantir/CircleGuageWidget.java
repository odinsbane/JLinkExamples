package org.orangepalantir;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CircleGuageWidget extends Widget{
    double minTheta = Math.PI/4;
    double maxTheta = 3*Math.PI/4;
    double highValue = 1;
    double lowValue = 0;
    double value;

    public CircleGuageWidget(String name){
        super(name);
        panel.setSize(120, 120);
    }
    Color bg = new Color(50, 0, 0);
    @Override
    public void paintWidget(Graphics2D g){
        int w = panel.getWidth();
        int h = panel.getHeight();
        g.setComposite(AlphaComposite.SrcOver);
        int drawLength = w<h? w: h - 2*padding;

        int padx = (w - drawLength)/2;
        int pady = (h - drawLength)/2;

        g.setColor(bg);
        g.fillOval(padx, pady, drawLength, drawLength);

        g.setColor(Color.WHITE);
        g.drawOval(padding + delta, padding + delta, drawLength-2*delta, drawLength - 2*delta);

        g.drawString("" + value, 2*padding,  h/2+padding);

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
        g.fill(e1);
        g.fill(e2);

    }

    @Override
    public Widget getConfig(){
        return new GuageConfigWidget(this);
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
