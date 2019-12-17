package sample;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @auther JianLinWei
 * @date 2019-12-17 11:33
 */
public class H264StreamHander   {
   private InputStream inputStream =null ;


    public void onStreamDataReceived(String deviceID, byte[] h264) {


     setInput(new ByteArrayInputStream(ByteBuffer.wrap(h264).array()));
    }

    public synchronized void setInput(InputStream input){
        inputStream = input;
    }

    public  synchronized InputStream getInput(){
        return inputStream;
    }
}
