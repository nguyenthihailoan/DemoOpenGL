package com.photo.editor.imagebrush.demoopenglesimage

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.media.effect.EffectContext
import android.opengl.GLUtils
import android.graphics.Bitmap
import android.media.effect.EffectFactory
import android.graphics.Color
import android.media.effect.Effect
import android.util.Log
import com.photo.editor.imagebrush.demoopenglesimage.openglutils.GLToolbox
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*


class TextureRenderer() : GLSurfaceView.Renderer {
    private var mProgram: Int = 0
    private var mTexSamplerHandle: Int = 0
    private var mTexCoordHandle: Int = 0
    private var mPosCoordHandle: Int = 0

    private var mTexVertices: FloatBuffer? = null
    private var mPosVertices: FloatBuffer? = null

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0

    private var mTexWidth: Int = 0
    private var mTexHeight: Int = 0

    private val mRunOnDraw: Queue<Runnable>
    private val mTextures = IntArray(2)
    var mCurrentEffect: Int = 0
    private var mEffectContext: EffectContext? = null
    private var mEffect: Effect? = null
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var initialized = false

    private val VERTEX_SHADER = "attribute vec4 a_position;\n" +
            "attribute vec2 a_texcoord;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  gl_Position = a_position;\n" +
            "  v_texcoord = a_texcoord;\n" +
            "}\n"

    private val FRAGMENT_SHADER = (
            "precision mediump float;\n" +
                    "uniform sampler2D tex_sampler;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                    "}\n")

    private val TEX_VERTICES = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)

    private val POS_VERTICES = floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)

    private val FLOAT_SIZE_BYTES = 4

    init {
        // TODO Auto-generated constructor stub
        mRunOnDraw = LinkedList()

    }

    fun init() {
        // Create program
        mProgram = GLToolbox.createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        // Bind attributes and uniforms
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram,
                "tex_sampler")
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord")
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position")

        // Setup coordinate buffers
        mTexVertices = ByteBuffer.allocateDirect(
                TEX_VERTICES.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTexVertices!!.put(TEX_VERTICES).position(0)
        mPosVertices = ByteBuffer.allocateDirect(
                POS_VERTICES.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mPosVertices!!.put(POS_VERTICES).position(0)
    }

    fun tearDown() {
        GLES20.glDeleteProgram(mProgram)
    }

    fun updateTextureSize(texWidth: Int, texHeight: Int) {
        mTexWidth = texWidth
        mTexHeight = texHeight
        computeOutputVertices()
    }

    fun updateViewSize(viewWidth: Int, viewHeight: Int) {
        mViewWidth = viewWidth
        mViewHeight = viewHeight
        computeOutputVertices()
    }

    fun renderTexture(texId: Int) {
        GLES20.glUseProgram(mProgram)
        GLToolbox.checkGlError("glUseProgram")

        GLES20.glViewport(0, 0, mViewWidth, mViewHeight)
        GLToolbox.checkGlError("glViewport")

        GLES20.glDisable(GLES20.GL_BLEND)

        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mTexVertices)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mPosVertices)
        GLES20.glEnableVertexAttribArray(mPosCoordHandle)
        GLToolbox.checkGlError("vertex attribute setup")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLToolbox.checkGlError("glActiveTexture")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId)//把已经处理好的Texture传到GL上面
        GLToolbox.checkGlError("glBindTexture")
        GLES20.glUniform1i(mTexSamplerHandle, 0)

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun computeOutputVertices() { //调整AspectRatio 保证landscape和portrait的时候显示比例相同，图片不会被拉伸
        if (mPosVertices != null) {
            val imgAspectRatio = mTexWidth / mTexHeight.toFloat()
            val viewAspectRatio = mViewWidth / mViewHeight.toFloat()
            val relativeAspectRatio = viewAspectRatio / imgAspectRatio
            val x0: Float
            val y0: Float
            val x1: Float
            val y1: Float
            if (relativeAspectRatio > 1.0f) {
                x0 = -1.0f / relativeAspectRatio
                y0 = -1.0f
                x1 = 1.0f / relativeAspectRatio
                y1 = 1.0f
            } else {
                x0 = -1.0f
                y0 = -relativeAspectRatio
                x1 = 1.0f
                y1 = relativeAspectRatio
            }
            val coords = floatArrayOf(x0, y0, x1, y0, x0, y1, x1, y1)
            mPosVertices!!.put(coords).position(0)
        }
    }

    private fun initEffect() {
        val effectFactory = mEffectContext!!.factory
        if (mEffect != null) {
            mEffect!!.release()
        }
        /**
         * Initialize the correct effect based on the selected menu/action item
         */
        when (mCurrentEffect) {

            R.id.none -> {
            }

            R.id.autofix -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX)
                mEffect!!.setParameter("scale", 0.5f)
            }

            R.id.bw -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BLACKWHITE)
                mEffect!!.setParameter("black", .1f)
                mEffect!!.setParameter("white", .7f)
            }

            R.id.brightness -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS)
                mEffect!!.setParameter("brightness", 2.0f)
            }

            R.id.contrast -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST)
                mEffect!!.setParameter("contrast", 1.4f)
            }

            R.id.crossprocess -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS)

            R.id.documentary -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY)

            R.id.duotone -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE)
                mEffect!!.setParameter("first_color", Color.YELLOW)
                mEffect!!.setParameter("second_color", Color.DKGRAY)
            }

            R.id.filllight -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT)
                mEffect!!.setParameter("strength", .8f)
            }

            R.id.fisheye -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE)
                mEffect!!.setParameter("scale", .5f)
            }

            R.id.flipvert -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP)
                mEffect!!.setParameter("vertical", true)
            }

            R.id.fliphor -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP)
                mEffect!!.setParameter("horizontal", true)
            }

            R.id.grain -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN)
                mEffect!!.setParameter("strength", 1.0f)
            }

            R.id.grayscale -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE)

            R.id.lomoish -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH)

            R.id.negative -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE)

            R.id.posterize -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE)

            R.id.rotate -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE)
                mEffect!!.setParameter("angle", 180)
            }

            R.id.saturate -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE)
                mEffect!!.setParameter("scale", .5f)
            }

            R.id.sepia -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA)

            R.id.sharpen -> mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN)

            R.id.temperature -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE)
                mEffect!!.setParameter("scale", .9f)
            }

            R.id.tint -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TINT)
                mEffect!!.setParameter("tint", Color.MAGENTA)
            }

            R.id.vignette -> {
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE)
                mEffect!!.setParameter("scale", .5f)
            }

            else -> {
            }
        }
    }

    fun setCurrentEffect(effect: Int) {
        mCurrentEffect = effect
    }


    fun setImageBitmap(bmp: Bitmap) {
        runOnDraw(Runnable {
            // TODO Auto-generated method stub
            loadTexture(bmp)
        })
    }

    private fun loadTexture(bmp: Bitmap) {
        GLES20.glGenTextures(2, mTextures, 0)

        updateTextureSize(bmp.width, bmp.height)

        mImageWidth = bmp.width
        mImageHeight = bmp.height

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0])
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
        bmp.recycle()
        GLToolbox.initTexParams()
    }

    private fun applyEffect() {
        if (mEffect == null) {
            Log.i("info", "apply Effect null mEffect")
        }

        mEffect!!.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1])
    }

    private fun renderResult() {
        if (mCurrentEffect != R.id.none) {
            renderTexture(mTextures[1])
        } else {
            renderTexture(mTextures[0])
        }
    }

    override fun onDrawFrame(gl: GL10) {
        // TODO Auto-generated method stub
        if (!initialized) {
            init()
            mEffectContext = EffectContext.createWithCurrentGlContext()
            initialized = true
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        synchronized(mRunOnDraw) {
            while (!mRunOnDraw.isEmpty()) {
                mRunOnDraw.poll().run()
            }
        }

        if (mCurrentEffect != R.id.none) {
            initEffect()
            applyEffect()
        }
        renderResult()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // TODO Auto-generated method stub
        updateViewSize(width, height)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // TODO Auto-generated method stub

    }

    protected fun runOnDraw(runnable: Runnable) {
        synchronized(mRunOnDraw) {
            mRunOnDraw.add(runnable)
        }
    }
}