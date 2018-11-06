package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.photo.editor.imagebrush.demoopenglesimage.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.util.Log


class GlRenderer(context: Context) : GLSurfaceView.Renderer {
    private var bitmap: Bitmap
    private var context: Context
    private val textures = IntArray(3)
    private var square: Square? = null
    private var scale = 1f

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mVPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private val translateMatrix = FloatArray(16)

    private var angle: Float = 0f
    private var x: Float = 0f
    private var y: Float = 0f

    init {
        this.context = context
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.gliter1)

    }

    override fun onDrawFrame(gl: GL10?) {
//
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setIdentityM(mVPMatrix, 0);
        scaleImage(scale)
        square?.draw(textures[0], mVPMatrix)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        generateSquare()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }


    private fun generateSquare() {
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width - 100, bitmap.height - 100, true)
        square = Square()
        GLES30.glGenTextures(2, textures, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLToolbox.initTexParams()
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
    }

    fun rotateImage(angle: Float) {
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, 1f)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, rotationMatrix, 0)
    }

    fun scaleImage(scale: Float) {
        Matrix.scaleM(viewMatrix, 0, 0.5f, 0.5f, 0.5f)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, viewMatrix, 0)
    }

    fun translateImage(x: Float, y: Float) {
        Matrix.translateM(translateMatrix, 0, x, y, 1f)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, translateMatrix, 0)
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    fun getAngle(): Float {
        return angle
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    fun setAngle(angle: Float) {
        this.angle = angle
        Log.d("DEBUGSCALE", angle.toString())
    }

    fun setX(x: Float) {
        this.x = x
        this.y = 0f
        Log.d("DEBUGSCALE", angle.toString())
    }

    fun setY(Y: Float) {
        this.y = y
        this.x = 0f
        Log.d("DEBUGSCALE", angle.toString())
    }

    fun setScale(scale: Float) {
        this.scale = scale
        Log.d("DEBUGSCALE", angle.toString())
    }
}