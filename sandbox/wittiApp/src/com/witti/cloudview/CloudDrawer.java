package com.witti.cloudview;

public class CloudDrawer {
    private static final String CAT_TAG = "WITTI_Cloud_Drawer";

    private int mProgId;
    private int mTexId;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mColorHandle;
    private int mTimesHandle;
    private int mMaxZHandle;
    private int mMVPHandle;

    private float mMaxZ;

    final static int MAX_PARTICLES = 7364;
    final static int PARTICLE_SIZE = 3;

    float[] mVertices;
    FloatBuffer mVertexBuffer;

    public CloudDrawer(){
        initializeShaders();
    }

    public draw(Matrix modelView){
        GLES20.glUseProgram(mProgId);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);
        
        GLES20.glUniform1i(mTextureHandle, 0);
        
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        

        GLES20.glUniform1f(mTimesHandle, 0f);
        GLES20.glUniform1f(mMaxZHandle, mMaxZ);
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, modelView, 0);
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, MAX_PARTICLES);
    }

    private initializeShaders(){
        Log.v(CAT_TAG, "initializeShaders");
        String strVShader = 
            "precision mediump float;" +
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "uniform float a_time;" +
            "uniform float a_max_z;" +
            "varying float v_color;" +
            "float time;" +
            "void main(){" +
                "gl_PointSize = 1.0;" +
                "v_color = vec4(.6,.6,.6+.4*(a_Position.z/a_max_z),.5);" +
                "gl_Position = u_MVPMatrix * a_Position;" +
            "}"

        String strFShader = 
            "precision mediump float;" +
            "uniform sampler2D u_texture;" +
            "varying vec4 v_color;" +
            "void main(){" +
                "vec4 tex = texture2D(u_texture, gl_PointCoord);" +
                "gl_FragColor = v_color * tex;" +
            "}"

        mProgId = Utils.LoadProgram(strVShader, strFShader);
        mTexId = Utils.LoadTexture(mCloudSurfaceView, R.drawable.particle);
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgId, "a_Position");
        mTextureHandle = GLES20.glGetUniformLocation(mProgId, "u_texture");
        mMVPHandle = GLES20.glGetUniformLocation(mProgId, "u_MVPMatrix");
        mTimeHandle = GLES20.glGetAttribLocation(mProgId, "a_time");
        mMaxZHandle = GLES20.glGetUniformLocation(mProgId, "a_max_z");
    }

    private loadDemo(){
        Log.v(CAT_TAG, "loadDemo");
        loadDemoFile();
        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertices).position(0);
    }

    private loadDemoFile(){
        Log.v(CAT_TAG, "loadDemoFile");
        mVertices = new float[MAX_PARTICLES * PARTICLE_SIZE];
        InputStream is = getApplicationContext().getResources()
                            .openRawResource(R.raw.yxz_points_less);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        int count = 0;
        Float[] parsed_line;
        mMaxZ = 0;
        while ((line = in.readLine()) != null && count<MAX_PARTICLES){
            parsed_line = parseLine(line);
            mVertices[count*PARTICLE_SIZE]   = parsed_line[0];
            mVertices[count*PARTICLE_SIZE+1] = parsed_line[1];
            mVertices[count*PARTICLE_SIZE+2] = parsed_line[2];
            if (parsed_line[2]>mMaxZ) mMaxZ = parsed_line[2];
            count++;
        }
        while (count<MAX_PARTICLES){
            mVertices[count*PARTICLE_SIZE]   = 0f;
            mVertices[count*PARTICLE_SIZE+1] = 0f;
            mVertices[count*PARTICLE_SIZE+2] = 0f;
            count++;
        }
    }

    private Float[] parseLine(String line) {
        Float[] result = {
                0f, 0f, 0f
        };
        String[] tokens = line.split("\\s");
        if (tokens.length < 3)
            return result;
        int count = 0;
        for (int ii = 0; ii < tokens.length && count < 3; ii++) {
            try {
                result[count] = Float.parseFloat(tokens[ii]);
            } catch (NumberFormatException nfe) {
                continue;
            }
            count++;
        }
        return result;
    }
}