package org.orangepalantir;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A small swing application for displaying
 */
public class SensorPlottingApp {
    List<double[]> temperatures = new ArrayList<>();

    //Expected 30fps, with 10sec displayed
    int points = 300;
    double period = 0.033;
    boolean running = true;

    public void monitor(){

        SensorMonitor monitor = new SensorMonitor();
        CyclicPlotWindow window = new CyclicPlotWindow("sensors", points);
        AtomicBoolean running = new AtomicBoolean(true);
        try{
            monitor.initialize();
            window.initialize(monitor.getIds());
            EventQueue.invokeLater(window::show);
            window.addCloseListener(w->{
                running.set(false);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem running sensors program.");
            return;
        }

        while(running.get()){
            double start = 0.001*System.currentTimeMillis();

            try {
                double[] temps = monitor.getTemperature();
                window.addData(temps);
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

    public static void main(String[] args) throws IOException, InterruptedException {
        SensorPlottingApp sap = new SensorPlottingApp();
        sap.monitor();
    }


}
