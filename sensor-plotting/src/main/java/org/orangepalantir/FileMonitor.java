package org.orangepalantir;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;

public class FileMonitor {
    BlockingQueue<double[]> measurements = new SynchronousQueue<>();
    final Path path;
    FileTime last;
    double[] lastMeasurement;
    boolean sync = false;
    FileMonitor(Path path){
        this.path = path;
    }
    String tilps = "\\s+";

    double[] getLatestSyncronized() throws InterruptedException, IOException {
        double[] r = measurements.take();
        if(r==null){
            throw new IOException("could not read measurement");
        }
        lastMeasurement = r;
        return r;
    }

    double[] getLatestAsync(){
        return lastMeasurement;
    }

    double[] getLatest() throws IOException, InterruptedException {
        if(sync){
            return getLatestSyncronized();
        } else{
            return getLatestAsync();
        }
    }

    List<double[]> getAllData() throws IOException {
        List<String> lines = Files.readAllLines(path).stream().filter(l->!l.startsWith("#")).collect(Collectors.toList());
        return lines.stream().map(
                l->l.split(tilps)
        ).map(
                s-> Arrays.stream(s).mapToDouble(Double::valueOf).toArray()
        ).collect(Collectors.toList());
    }

    double[] readLast() throws IOException {
        List<String> lines = Files.readAllLines(path).stream().filter(l->!l.startsWith("#")).collect(Collectors.toList());
        double[] row = Arrays.stream(
                lines.get( lines.size() - 1 ).split(tilps)
        ).mapToDouble( Double::valueOf ).toArray();

        last = Files.getLastModifiedTime(path);

        return row;
    }

    List<String> getIds() throws IOException {
        List<String> lines = Files.readAllLines(path).stream().filter(l->l.startsWith("#")).collect(Collectors.toList());
        last = Files.getLastModifiedTime(path);
        lastMeasurement = readLast();
        new Thread(this::monitorLoop).start();
        List<String> ids = Arrays.asList(lines.get(lines.size()-1).split( tilps ));
        System.out.println(ids.size());
        return ids;
    }

    void monitorLoop(){
        while(true){
            try {
                FileTime mod = Files.getLastModifiedTime(path);

                if(mod.compareTo(last)>0){
                    lastMeasurement = readLast( );
                    measurements.add(lastMeasurement);
                } else{
                    Thread.sleep(500);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }

}