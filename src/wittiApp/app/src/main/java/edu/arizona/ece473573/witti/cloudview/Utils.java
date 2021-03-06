//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Author: Alex Warren
//Original Author: Sravan Kumar Reddy  

package edu.arizona.ece473573.witti.cloudview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Utils - Code for common openGL work
 * 
 * This code has been taken from 
 * http://opengles2learning.blogspot.com/2011/05/particle-system-with-point-spritespart.html
 * This has been licensed under GPL, so either we change it or
 * Also use GPL.
 * @author Sravan Kumar Reddy
 * @author Alex Warren
 */

public class Utils {
    private static final String CAT_TAG = "WITTI_Utils";
    
    public static int LoadTexture(GLSurfaceView view, int imgResID){
        Log.d("Utils", "Loadtexture");
        Bitmap img = null;
        int textures[] = new int[1];
        try {
            img = BitmapFactory.decodeResource(view.getResources(), imgResID);
            GLES20.glGenTextures(1, textures, 0);
            
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);
            Log.d("LoadTexture", "Loaded texture"+":H:"+img.getHeight()+":W:"+img.getWidth());
        } catch (Exception e){
            Log.d("LoadTexture", e.toString()+ ":" + e.getMessage()+":"+e.getLocalizedMessage());
        }
        img.recycle();
        return textures[0];     
    }
    
    public static int LoadShader(String strSource, int iType)
    {
        Log.d(CAT_TAG, "LoadShader");
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {         
            Log.d("Load Shader Failed", "Compilation\n"+GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }
    
    public static int LoadProgram(String strVSource, String strFSource)
    {
        Log.d(CAT_TAG, "LoadProgram");
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = LoadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0)
        {
            Log.d(CAT_TAG, "Load Vertex Shader Failed");
            return 0;
        }
        iFShader = LoadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if(iFShader == 0)
        {
            Log.d(CAT_TAG, "Load Fragment Shader Failed");
            return 0;
        }
        
        iProgId = GLES20.glCreateProgram();
        
        checkGlError(CAT_TAG, "glCreateProgram");

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);
        
        GLES20.glLinkProgram(iProgId);
        
        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] == GLES20.GL_FALSE) {
            Log.d(CAT_TAG, "Linking Failed");
            return 0;
        }
        GLES20.glGetProgramiv(iProgId, GLES20.GL_VALIDATE_STATUS, link, 0);
        if (link[0] == GLES20.GL_FALSE) {
            Log.d(CAT_TAG, "Validating Failed");
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }
    
    //http://stackoverflow.com/questions/16599489/error-when-creating-program-and-trying-to-get-attribute-location-opengl-es-2-0
    public static void checkGlError(String cat_tag, String op) {
        int error;
        String errorString = "";
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(cat_tag, op + ": glError " + error);
            errorString += "err: " + String.valueOf(error);
        }
        if (errorString.length()>0)
            throw new RuntimeException(op + ": GL " + errorString);
    }
}
