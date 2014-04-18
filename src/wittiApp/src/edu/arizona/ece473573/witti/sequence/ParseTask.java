//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Alex Warren

package edu.arizona.ece473573.witti.sequence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.arizona.ece473573.witti.cloudview.PointCloud;

import android.os.AsyncTask;
import android.util.Log;

/**
 * ParseTask Class for parsing an input stream of binary floats.
 *
 * This is the base class for LoadFileTask and DownloadDataTask.
 * Its main purpose is to parse input stream.
 *
 * Creates a float buffer from binary data. The floats are loaded
 * byte by byte. Buffers incoming data as it copies from stream
 * into a directly (think malloc) allocated buffer for openGL.
 *
 * The results are wrapped into a PointCloud and returned.
 *
 * The current implementation is dependant
 * on the native byte order to be little endian. Java is big endian,
 * and this makes it more complicated to convert from little endian.
 * Most (all?) android devices are little endian.
 * 
 * @author Alex Warren
 */
public abstract class ParseTask extends AsyncTask<Void, Void, Integer> {
    private static final String CAT_TAG = "WITTI_ParseTask";
    private static final int PARSE_BUFFER_SIZE = 1024;

    protected String mErrorString;


    // takes a reference to the activity for starting new activity
    public ParseTask() {
        mErrorString = "";
    }

    /**
     * Parses an InputStream as binary data to create point cloud.
     * 
     * @param is          InputStream to be parsed
     * @param size        Size in bytes of the stream for preallocating buffer.
     * @return PointCloud Point cloud with nio float buffer loaded
     * @throws IOException 
     */
    public PointCloud parseBinary(InputStream is, int size) throws IOException {
        //TODO
        //Error Check size%12
        Log.d(CAT_TAG, "parseBinary Byte Size: " + Integer.toString(size));
        //int num_elements = size/(3*4);

        //This is basically a contiguous memory location for openGL to access,
        //It will be 'converted' to a float buffer
        //Changing the byte order only effects how Java reads and writes(?) but not the underlying data
        ByteBuffer nio_byte_buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        
        byte[] buffer = new byte[PARSE_BUFFER_SIZE]; //This is a buffer for the stream
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
                nio_byte_buffer.put(buffer, 0, buffer_num_read);
                total_offset += buffer_num_read;
            }
        }else{ 
            Log.e(CAT_TAG, "Big Endian, there is no hope");
            //NOOOOOOOOOO!!!!!!!!!!!!
            //TODO Fail miserably
            //Or just switch all the incoming bytes
        }

        if (isCancelled()) {
            Log.d(CAT_TAG, "Task cancelled");
            mErrorString = "Data parse task cancelled.\n";
            return null;
        }

        //rewind the buffer or asFloatBuffer will only give remaining
        //part of buffer, which should be empty
        nio_byte_buffer.rewind();
        //logBuffer(nio_byte_buffer.asFloatBuffer());
        return new PointCloud(nio_byte_buffer.asFloatBuffer());
    }

    /**
     * Print the buffer to log.
     * 
     * @param fb          Buffer to be printed
     */
    public void logBuffer(FloatBuffer fb) {
        fb.rewind();
        Log.d(CAT_TAG, "bufer: " + fb.toString());   
        while(fb.remaining() > 0){
            Log.d(CAT_TAG, Float.toString(fb.get()));
        }
    }
}