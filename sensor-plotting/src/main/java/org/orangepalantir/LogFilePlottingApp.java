package org.orangepalantir;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LogFilePlottingApp {
    List<double[]> temperatures = new ArrayList<>();

    //Expected 30fps, with 10sec displayed
    int points = 500;
    double period = 0.01;
    FileMonitor fm;

    public void monitor(FileMonitor fileMonitor){
        AtomicInteger started = new AtomicInteger(0);
        WidgetWindow window = new WidgetWindow("log file plotting");

        CyclicPlotWindow cyclicPlot = new CyclicPlotWindow("log file plot", points);


        List<CircleGuageWidget> guages = new ArrayList<>();

        try{
            List<String> ids = fileMonitor.getIds();
            cyclicPlot.initialize(ids);

            EventQueue.invokeLater(()->{
                JFrame f = window.show();
                f.setTitle(fileMonitor.path.getFileName().toString());
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            });

            window.addWidget(cyclicPlot);
            started.incrementAndGet();
            guages.addAll(ids.stream().map(CircleGuageWidget::new).collect(Collectors.toList()));
            for(CircleGuageWidget guage: guages){
                window.addWidget(guage);
                started.incrementAndGet();
            }
            window.pack();

            CloseListener listener = new CloseListener() {

                Thread main = Thread.currentThread();
                @Override
                public void close(Widget w) {
                    int i = started.decrementAndGet();
                    if(i==0){
                        main.interrupt();
                    }
                }
            };
            cyclicPlot.addCloseListener(listener);
            guages.forEach(g->g.addCloseListener(listener));

            List<double[]> data = fileMonitor.getAllData();
            int n = data.size()>points? points : data.size();
            double latest_batch = 0;
            for(int i = 0; i<n; i++){
                double[] row = data.get(i);
                cyclicPlot.addData(data.get(i));

                for(int j = 0; j<guages.size(); j++){
                    guages.get(j).setValue(row[j]);
                }
            }

        }  catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem reading log file.");
            return;
        }
        cyclicPlot.setPlotRange(-2, 2);


        while(started.get()>0){
            double start = 0.001*System.currentTimeMillis();

            try {
                double[] temps = fileMonitor.getLatest();
                cyclicPlot.addData(temps);
                for(int j = 0; j<temps.length; j++){
                    guages.get(j).setValue(temps[j]);
                }
                double elapsed = 0.001*System.currentTimeMillis() - start;
                if(elapsed < period){
                    Thread.sleep((long)((period - elapsed)*1000));
                } else{
                }

            } catch (IOException e) {
                System.out.println("could not read skipping.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                //interrupting the file monitor if necessary
                break;
            }
        }
    }

    public static void main(String[] args){
        Path p;
        if(args.length==0) {
            JFileChooser choose = new JFileChooser();
            choose.showDialog(null, "log file");
            p = Paths.get(choose.getSelectedFile().getAbsolutePath());
        } else{
            p = Paths.get(args[0]);
        }

        LogFilePlottingApp lpa = new LogFilePlottingApp();
        FileMonitor monitor = new FileMonitor(p);
        monitor.setSync( true );
        lpa.monitor( monitor );
        System.out.println("finished monitoring");
    }
}
