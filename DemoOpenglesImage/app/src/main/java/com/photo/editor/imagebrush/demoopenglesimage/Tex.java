package com.photo.editor.imagebrush.demoopenglesimage;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Tex {
//    private FloatBuffer mVertexBuffer;
//    private ShortBuffer mDrawListBuffter;
//    protected FloatBuffer mUvBuffer;
//    protected static float mUvs[];
//    private final float[] mMtrxView = new float[16];
//    public static final String vs_Image
//            = "uniform mat4 uMVPMatrix;"
//            + "attribute vec4 vPosition;"
//            + "attribute vec2 a_texCoord;"
//            + "varying vec2 v_texCoord;"
//            + "void main() {"
//            + "     gl_Position = uMVPMatrix * vPosition;"
//            + "     v_texCoord = a_texCoord;"
//            + "}";
//    public static final String fs_Image
//            = "precision mediump float;"
//            + "varying vec2 v_texCoord;"
//            + "uniform sampler2D s_texture;"
//            + "void main() {"
//            + "     vec4 tex = texture2D(s_texture, v_texCoord);"
//            + "     float tintR = 0.6;"
//            + "     float tintG = 0.3;"
//            + "     float tintB = 0.0;"
//            + "     float tr = clamp(tex.r * (1.0 - tintR) + tintR, 0.0, 1.0);"
//            + "     float tg = clamp(tex.g * (1.0 - tintG) + tintG, 0.0, 1.0);"
//            + "     float tb = clamp(tex.b * (1.0 - tintB) + tintB, 0.0, 1.0);"
//            + "     gl_FragColor = vec4(tr, tg, tb, tex.a);"
//            + "}";
//    float mSquareCoords[] = {
//            -1.0f, 1.0f, 0.0f,
//            -1.0f, -1.0f, 0.0f,
//            1.0f, -1.0f, 0.0f,
//            1.0f, 1.0f, 0.0f
//    };
//    private short mDrawOrder[] = {0, 1, 2, 0, 2, 3};
//    private final int mProgram;
//    int[] mTextureNames;
//    MainGLRenderer mMainGLRenderer;
//    int mWidth, mHeight;
//    Bitmap mBitmap;
//
//    public Tex(MainGLRenderer mainGLRenderer, Bitmap bitmap) {
//        mMainGLRenderer = mainGLRenderer;
//
//        ByteBuffer bb = ByteBuffer.allocateDirect(mSquareCoords.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        mVertexBuffer = bb.asFloatBuffer();
//        mVertexBuffer.put(mSquareCoords);
//        mVertexBuffer.position(0);
//
//        ByteBuffer dlb = ByteBuffer.allocateDirect(mDrawOrder.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        mDrawListBuffter = dlb.asShortBuffer();
//        mDrawListBuffter.put(mDrawOrder);
//        mDrawListBuffter.position(0);
//
//        mUvs = new float[] {
//                0.0f, 0.0f,
//                0.0f, 1.0f,
//                1.0f, 1.0f,
//                1.0f, 0.0f,
//        };
//        ByteBuffer bbUvs = ByteBuffer.allocateDirect(mUvs.length * 4);
//        bbUvs.order(ByteOrder.nativeOrder());
//        mUvBuffer = bbUvs.asFloatBuffer();
//        mUvBuffer.put(mUvs);
//        mUvBuffer.position(0);
//
//        int vertexShader = mMainGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vs_Image);
//        int fragmentShader = mMainGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image);
//
//        mProgram = GLES20.glCreateProgram();
//        GLES20.glAttachShader(mProgram, vertexShader);
//        GLES20.glAttachShader(mProgram, fragmentShader);
//        GLES20.glLinkProgram(mProgram);
//
//        initTexture(bitmap);
//    }
//
//    private void initTexture(Bitmap bitmap) {
//        mWidth = bitmap.getWidth();
//        mHeight = bitmap.getHeight();
//        GLES20.glViewport(0, 0, mWidth, mHeight);
//
//        mTextureNames = new int[1];
//        GLES20.glGenTextures(1, mTextureNames, 0);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureNames[0]);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//
//        draw();
//    }
//
//    public void draw() {
//        GLES20.glUseProgram(mProgram);
//        Matrix.setIdentityM(mMtrxView, 0);
//
//        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//        GLES20.glEnableVertexAttribArray(positionHandle);
//        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
//
//        int texCoordLoc = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
//        GLES20.glEnableVertexAttribArray(texCoordLoc);
//        GLES20.glVertexAttribPointer(texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mUvBuffer);
//
//        int mtrxHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//        GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, mMtrxView, 0);
//
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureNames[0]);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffter);
//
//        GLES20.glDisableVertexAttribArray(positionHandle);
//        GLES20.glDisableVertexAttribArray(texCoordLoc);
//
//        mBitmap = getBitmap();
//    }
//
//    public Bitmap getBitmap() {
//        IntBuffer intBuffer = IntBuffer.allocate(mWidth * mHeight);
//        GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
//
//        int[] intArrayO = intBuffer.array();
//        int[] intArrayR = new int[mWidth * mHeight];
//        for (int i = 0; i < mHeight; i++) {
//            for (int j = 0; j < mWidth; j++) {
//                intArrayR[(mHeight - i - 1) * mWidth + j] = intArrayO[i * mWidth + j];
//            }
//        }
//
//        Bitmap postBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//        postBitmap.copyPixelsFromBuffer(intBuffer.wrap(intArrayR));
//
//        return postBitmap;
//    }
//
//    public Bitmap getmBitmap() {
//        return mBitmap;
//    }
}
