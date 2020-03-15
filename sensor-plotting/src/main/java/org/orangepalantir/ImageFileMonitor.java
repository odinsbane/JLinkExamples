package org.orangepalantir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class ImageFileMonitor extends Widget {
    BufferedImage img;
    Path path;
    FileTime last;
    public ImageFileMonitor(Path monitoring) throws IOException {
        super(monitoring.getFileName().toString());
        img = ImageIO.read(monitoring.toFile());
        last = Files.getLastModifiedTime(monitoring);
        int w = 480;
        int h = 360;
        double sx = (w*1.0 - 2*padding)/img.getWidth();
        double sy = (h*1.0 - 2*padding)/img.getHeight();

        if(sx > sy){
            w = (int)(sy*img.getWidth()) + 2*padding;
        } else{
            h = (int)(sx*img.getHeight()) + 2*padding;
        }


        panel.setSize(w, h);
        path = monitoring;
    }
    public void paintWidget(Graphics2D g2d){
        if(img != null){
            int x2 = panel.getWidth() - padding;
            int y2 = panel.getHeight() - padding;
            g2d.drawImage(img, padding, padding, x2, y2, 0, 0, img.getWidth(), img.getHeight(), panel);
        }
    }

    public void monitor(){
        Thread t = new Thread(()->{
            while(true){
                try {
                    FileTime mod = Files.getLastModifiedTime(path);

                    if(mod.compareTo(last)>0){
                        img = ImageIO.read(path.toFile());
                        last = mod;
                    } else{
                        Thread.sleep(500);
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void setImage(BufferedImage img){
        this.img = img;
        panel.repaint();
    }

    public static void main(String[] args) throws Exception{
        ImageFileMonitor ifm = new ImageFileMonitor(Paths.get(args[0]));
        ifm.show();
        ifm.addCloseListener((w)->{
            System.exit(0);
        });
        ifm.monitor();
    }
}
