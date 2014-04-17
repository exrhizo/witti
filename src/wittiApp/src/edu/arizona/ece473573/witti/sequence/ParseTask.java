package edu.arizona.ece473573.witti.sequence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.arizona.ece473573.witti.cloudview.PointCloud;

import android.os.AsyncTask;
import android.util.Log;

public abstract class ParseTask extends AsyncTask<Void, Void, Integer> {
    private static final String CAT_TAG = "WITTI_ParseTask";
    private static final int PARSE_BUFFER_SIZE = 1024;

    protected int mPosition;
    protected String mErrorString;


    // takes a reference to the activity for starting new activity
    public ParseTask(int position) {
        mPosition = position;
        mErrorString = "";
    }

    /**
     * Parses an InputStream for binary floats.
     * 
     * @param is an InputStream to be parsed
     * @param size Size in bytes of the stream
     * @return PointCloud Point cloud with nio float buffer loaded
     * @throws IOException 
     */
    public PointCloud parseBinary(InputStream is, int size) throws IOException {
        //TODO
        //Check size%12

        //int num_elements = size/(3*4);
        //DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        ByteBuffer nio_byte_buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        
        byte[] buffer = new byte[PARSE_BUFFER_SIZE];
        int buffer_num_read = 0;
        int total_offset = 0;

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN){
            while (total_offset < size && !isCancelled()) {
                buffer_num_read = is.read(buffer, 0, PARSE_BUFFER_SIZE);
                if (buffer_num_read < 0) {
                    //TODO
                    //Expected more data, error
                    break;
                }else if (buffer_num_read == 0){
                    continue;
                }
                nio_byte_buffer.put(buffer, total_offset, buffer_num_read);
                total_offset += buffer_num_read;
            }
        }else{ 
            //NOOOOOOOOOO!!!!!!!!!!!!
            //TODO Fail miserably
        }
        if (isCancelled()) {
            Log.d(CAT_TAG, "Task cancelled");
            mErrorString = "Data parse task cancelled.\n";
            return null;
        }
        return new PointCloud(nio_byte_buffer.asFloatBuffer());
    }

}