package sample;


import com.ha.facecamera.configserver.ConfigServer;
import com.ha.facecamera.configserver.ConfigServerConfig;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private ImageView imageView2;


    private  FileOutputStream fileOutputStream = null;

    private ExecutorService service = Executors.newCachedThreadPool();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String sn = "012330-9F7B55-4FCEEE";
        ConfigServer configServer = new ConfigServer();
        configServer.start(10001, new ConfigServerConfig());
        configServer.onCameraConnected(val -> {
            System.out.println("上线" + val);
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        configServer.getIsStreamStart(sn);
        configServer.startStream(sn);
        File file = new File("D:/1.mp4");

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LimitQueue<InputStream> inputStreamLimitQueue = new LimitQueue<>(30);
        configServer.onStreamDataReceived((sng, h264) -> {

         try {

              fileOutputStream.write(h264, 0, h264.length);
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }


      });

            service.execute(()->{
                try {
                    InputStream inputStream1 = new FileInputStream(file);
                    FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream1);
                    frameGrabber.setFrameRate(30);
                    frameGrabber.setFormat("h264");
                    frameGrabber.setVideoBitrate(15);
                    frameGrabber.setVideoOption("preset", "ultrafast");
                    frameGrabber.setNumBuffers(25000000);
                    avutil.av_log_set_level(avutil.AV_LOG_ERROR);
                    frameGrabber.start();

                    Frame frame1=  frameGrabber.grab();


                    Java2DFrameConverter converter = new Java2DFrameConverter();

                    while (true) {
                        try {
                            frame1 = frameGrabber.grab();
                            if (frame1 == null)
                                continue;
                            BufferedImage bufferedImage = converter.convert(frame1);
                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                            Platform.runLater(() -> {
                                imageView.setImage(image);
                            });
                        } catch (FrameGrabber.Exception e) {
                            e.printStackTrace();
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });



        /* configServer.stopStream(sn);*/


        //rstp


        service.execute(()->{
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber("rtsp://192.168.3.87");
            fFmpegFrameGrabber.setOption("rtsp_transport", "tcp");
            fFmpegFrameGrabber.setFrameRate(30);
            fFmpegFrameGrabber.setVideoBitrate(15);
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);


            Frame frame = null;
            try {
                fFmpegFrameGrabber.start();
                frame = fFmpegFrameGrabber.grabFrame();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
            Java2DFrameConverter converter = new Java2DFrameConverter();
            while (frame != null) {
                try {
                    frame = fFmpegFrameGrabber.grabImage();
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
                BufferedImage bufferedImage = converter.convert(frame);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                Platform.runLater(() -> {
                    imageView2.setImage(image);
                });
            }

        });




    }


}
