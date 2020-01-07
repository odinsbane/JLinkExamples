package org.orangepalantir;


import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.GraphPoints;
import lightgraph.LGFont;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensorPlottingApp {
    List<double[]> temperatures = new ArrayList<>();
    int points = 100;
    double period = 0.1;

    double[] times = new double[points];
    double[] time_delta = new double[points];
    Graph graph;
    Pattern pat = Pattern.compile("^Core (\\d+):\\s+[+-]([0-9.]+)°C");
    int cores = 0;
    boolean running = true;



    double[] getTemperature() throws IOException, InterruptedException {

        Process proc = new ProcessBuilder("sensors").start();
        proc.waitFor();
        BufferedReader br =  new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String s;
        int i = 0;
        double[] temps = new double[cores];
        while((s = br.readLine()) != null){
            Matcher m = pat.matcher(s);
            if(m.find()){
                double f = Double.parseDouble(m.group(2));
                temps[i] = f;
                i++;
            }

        }

        return temps;
    }
    void first() throws InterruptedException, IOException {
        Process proc = new ProcessBuilder("sensors").start();
        proc.waitFor();
        BufferedReader br =  new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String s;
        int i = 0;
        double t = System.currentTimeMillis()*0.001;
        for(int j = 0; j<points; j++){
            times[j] = t - (points-j)*period;
            time_delta[j] = period*j;
        }

        graph = new Graph();
        while((s = br.readLine()) != null){
            Matcher m = pat.matcher(s);
            if(m.find()){
                cores++;
                double[] values = new double[points];
                temperatures.add(values);

                double f = Double.parseDouble(m.group(2));
                String id = m.group(1);
                values[points-1] = f;

                DataSet set = graph.addData(time_delta, values);
                set.setLabel("Core: " + id);
                set.setPoints(GraphPoints.filledCircles());
            }

        }
        graph.setXRange(0, (points) * period);
        graph.setYRange(0, 110);
        graph.setXTicCount(6);
        graph.setYTicCount(12);
        graph.setBackground(Color.BLACK);
        graph.setAxisColor(Color.WHITE);
        graph.show(true, "CPU Temperature");


    }

    public void update() throws IOException, InterruptedException {

        final double[] temps = getTemperature();
        final double t = System.currentTimeMillis()*0.001;
        final double ot = times[1];
        for(int i = 0; i<points-1; i++){
            times[i] = times[i+1];
            time_delta[i] = times[i] - ot;
        }
        time_delta[points-1] = t - ot;
        times[points-1] = t;

        for(int i = 0; i<temps.length; i++){
            double[] values = temperatures.get(i);
            System.arraycopy(values, 1, values, 0, points-1);
            values[points-1] = temps[i];
            graph.getDataSet(i).setData(time_delta, values);
        }
        graph.refresh(false);

    }

    public void monitor(){
        try {
            first();
        } catch (InterruptedException e) {
            System.out.println("interrupted before command completed, exiting");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("could not read from process, exiting");
            e.printStackTrace();
            System.exit(-1);
        }
        while(running){
            double start = 0.001*System.currentTimeMillis();

            try {
                update();
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
