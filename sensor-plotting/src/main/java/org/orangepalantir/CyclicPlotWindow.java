package org.orangepalantir;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A small window/frame that keeps track of n points in a cycle. ie it has a cursor that represents the first point, and
 * each time data is added that cursor is moved forward one.
 */
public class CyclicPlotWindow extends Widget{
    List<double[]> data;
    List<String> names;
    int points;
    int cursor = 0;

    public CyclicPlotWindow(String name, int n){
        super(name);
        panel.setSize(360, 240);
        points = n;
    }

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
    public void paintWidget(Graphics2D g){
            int w = panel.getWidth();
            int h = panel.getHeight();
            int lc = cursor;

            int dw = w - 2*padding;
            int dh = h - 2*padding;

            g.setColor(bg);
            g.fillRoundRect(padding, padding, dw, dh, 5, 5);

            g.setColor(Color.WHITE);
            g.drawRoundRect(padding, padding, dw, dh, 5, 5);


            double xf = dw*1.0/(points-1);
            double yf = dh/(highT - lowT);

            g.setColor(Color.RED);
            int y100 = h - padding - (int)(yf*100);
            g.drawLine( padding+1, y100, w-padding -1, y100 );
            int ymin = padding+1;
            int ymax = dh + padding-1;
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

                    y0 = y0<ymin?ymin:y0;
                    y0 = y0>ymax?ymax:y0;
                    y1 = y1<ymin?ymin:y1;
                    y1 = y1>ymax?ymax:y1;

                    g.drawLine(x0, y0, x1, y1);
                }
            }
    }

    @Override
    public Widget getConfig(){
        return new CyclicPlotConfigWidget(this);
    }
    public void initialize(List<String> names){
        this.names = new ArrayList<>(names);
        data = new ArrayList<>();

        for(String s: names){
            data.add(new double[points]);
        }

    }

    public void setPlotRange(double low, double high){
        highT = high;
        lowT = low;
    }

    public void setNPoints(int n){
        if(data.get(0).length < n){
            for(int i = 0; i<data.size(); i++){
                data.set(i, new double[n]);
            }
        }
        points = n;
    }
    public void addData(double[] newData){
        for(int i = 0; i<newData.length; i++){
            data.get(i)[cursor] = newData[i];
        }
        cursor = (cursor+1)%points;
        panel.repaint();
    }

}
