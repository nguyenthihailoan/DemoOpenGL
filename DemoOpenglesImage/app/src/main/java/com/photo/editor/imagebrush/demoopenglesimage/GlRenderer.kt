package com.photo.editor.imagebrush.demoopenglesimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GlRenderer(context: Context):GLSurfaceView.Renderer {
    private var bitmap:Bitmap
    private var context:Context
    init {
        this.context=context
        bitmap=BitmapFactory.decodeResource(context.resources,R.drawable.gliter1)
    }
    override fun onDrawFrame(gl: GL10?) {
        square?.draw(textures[0]);
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width, height);
        GLES20.glClearColor(0f,0f,0f,1f);
        generateSquare();
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    private val textures = IntArray(2)
    private var square: Square? = null

    private fun generateSquare() {
        GLES20.glGenTextures(2, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        square = Square()
    }

    fun rotateImage(angle:Float,x:Float,y:Float,z:Float){

    }
}