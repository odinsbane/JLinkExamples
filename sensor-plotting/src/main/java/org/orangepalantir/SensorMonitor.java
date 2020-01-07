package org.orangepalantir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for calling the program sensors and getting the temperature from all of the cpus.
 *
 */
public class SensorMonitor {
    final String command;
    //lines that start with core, and are followed by a terperature.
    Pattern pat = Pattern.compile("^Core (\\d+):\\s+[+-]([0-9.]+)°C");
    private List<String> ids= new ArrayList<>();
    public SensorMonitor(){
        this("sensors");
    }

    /**
     * In case the command is not on the path, or has a different name.
     * @param cmd name of the command to run.
     */
    public SensorMonitor(String cmd){
        command = cmd;
    }

    /**
     * Attempts to make a measurement and setup the ids so that the number of cores is known.
     *
     * @throws IOException issue running the sensors program. does it exist? is it on the path?
     * @throws InterruptedException
     */
    public void initialize() throws IOException, InterruptedException {
        Process proc = new ProcessBuilder(command).start();
        proc.waitFor();
        BufferedReader br =  new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String s;

        int cores = 0;
        while((s = br.readLine()) != null){
            Matcher m = pat.matcher(s);
            if(m.find()){

                String id = m.group(1);
                ids.add(id);

            }

        }
    }

    /**
     * Runs the program, but returns an array of temperature measurements.
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    double[] getTemperature() throws IOException, InterruptedException {

        Process proc = new ProcessBuilder("sensors").start();
        proc.waitFor();
        BufferedReader br =  new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String s;
        int i = 0;
        double[] temps = new double[ids.size()];
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

    /**
     * Collected ids during initialization.
     * @return
     */
    List<String> getIds(){
        return new ArrayList<>(ids);
    }

}
