package org.orangepalantir;

import javax.swing.JTextField;

public class GuageConfigWidget extends Widget {
    CircleGuageWidget widget;
    JTextField max;
    JTextField min;

    GuageConfigWidget( CircleGuageWidget widget){
        super("config: " + widget.name);
        this.widget = widget;
        max = new JTextField("" + widget.highValue);
        min = new JTextField("" + widget.lowValue);

        max.setSize(100, 20);
        min.setSize(100, 20);
        max.setLocation(padding + 10, padding + 10);
        min.setLocation(padding + 10, padding + 40);
        panel.setLayout(null);
        panel.add(max);
        panel.add(min);
        min.addActionListener(evt->close());
        max.addActionListener(evt->close());

        panel.setOpaque(true);
        panel.setSize(widget.panel.getSize());
    }

    @Override
    public void close(){
        super.close();
        widget.setDomain(
                Double.parseDouble(min.getText()),
                Double.parseDouble(max.getText())
        );
    }

}
