package org.orangepalantir;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CyclicPlotConfigWidget extends Widget{
    CyclicPlotWindow widget;
    JTextField max;
    JTextField min;
    private JTextField nPoints;

    public CyclicPlotConfigWidget(CyclicPlotWindow widget){
        super("config: " + widget.name);
        this.widget = widget;
        max = new JTextField("" + widget.highT);
        min = new JTextField("" + widget.lowT);
        nPoints = new JTextField("" + widget.points);

        max.setSize(100, 20);
        min.setSize(100, 20);
        nPoints.setSize(100, 20);
        max.setLocation(padding + 10, padding + 10);
        min.setLocation(padding + 10, padding + 40);
        nPoints.setLocation(padding + 10, padding + 60);
        panel.setLayout(null);
        panel.add(max);
        panel.add(min);
        panel.add(nPoints);
        min.addActionListener(evt->close());
        max.addActionListener(evt->close());
        nPoints.addActionListener(evt->close());

        panel.setOpaque(true);
        panel.setSize(widget.panel.getSize());
    }

    @Override
    public void close(){
        super.close();
        widget.setPlotRange(Double.parseDouble( min.getText()), Double.parseDouble(max.getText()));
        widget.setNPoints(Integer.parseInt(nPoints.getText()));
    }


}
