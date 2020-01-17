package org.orangepalantir;

import javax.swing.JFileChooser;
import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogFilePlottingApp {
    List<double[]> temperatures = new ArrayList<>();

    //Expected 30fps, with 10sec displayed
    int points = 5000;
    double period = 0.01;
    boolean running = true;
    FileMonitor fm;

    public void monitor(FileMonitor fileMonitor){

        CyclicPlotWindow window = new CyclicPlotWindow(points);
        CircleGuageWidget batchDial = new CircleGuageWidget();
        try{
            window.initialize(fileMonitor.getIds());
            batchDial.initialize("batch");
            EventQueue.invokeLater(window::show);
            EventQueue.invokeLater(batchDial::show);

            List<double[]> data = fileMonitor.getAllData();
            int n = data.size()>points? points : data.size();
            double latest_batch = 0;
            for(int i = 0; i<n; i++){
                window.addData(data.get(i));
                latest_batch = data.get(i)[0];
            }

            batchDial.setValue(latest_batch);
        }  catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem reading log file.");
            return;
        }
        window.setPlotRange(-2, 2);

        batchDial.setDomain(0, 5000);

        while(running){
            double start = 0.001*System.currentTimeMillis();

            try {
                double[] temps = fileMonitor.getLatest();
                window.addData(temps);
                batchDial.setValue(temps[0]);
                double elapsed = 0.001*System.currentTimeMillis() - start;
                if(elapsed < period){
                    Thread.sleep((long)((period - elapsed)*1000));
                } else{
                }

            } catch (IOException e) {
                System.out.println("could not read skipping.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        monitor.sync = true;
        lpa.monitor( monitor );
    }
}
