package com.example.loanxu.openessldemo.demoopengl

import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class BitmapRenderer:GLSurfaceView.Renderer {


    private var program: Int = 0
    private var texSamplerHandle: Int = 0
    private var texCoordHandle: Int = 0
    private var posCoordHandle: Int = 0
    // Geometric variables
    var POS_VERTICES : FloatArray = floatArrayOf(
            // Mapping coordinates for the vertices
            -1f, -1f, 0f        // top left     (V2)
            - 1f, 1f, 0f,       // bottom left  (V1)
            1f, -1f, 0f,        // top right    (V4)
            1f, 1f, 0f)         // bottom right (V3)
    var texVertices: FloatBuffer? = null   // buffer holding the texture coordinates
    var posVertices: FloatBuffer? = null   // buffer holding the texture coordinates
    var TEX_VERTICES : FloatArray =floatArrayOf(
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f)
    private val textures = IntArray(1)
    var bitmap: Bitmap? = null
    val VERTEX_SHADER = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}"
    val FRAGMENT_SHADER = (
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                    "}")
    private val FLOAT_SIZE_BYTES = 4
    private var initializer=false


    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var texWidth: Int = 0
    private var texHeight: Int = 0

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private val runOnDraw: Queue<Runnable>

    init {
        runOnDraw=LinkedList<Runnable>()
    }

    override fun onDrawFrame(gl: GL10?) {
        if (!initializer){
            initializer=true
            init()
        }
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                runOnDraw.poll().run()
            }
        }
        renderTexture(textures[0])
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }



    fun init(){
        // Create program
        program = GLToolbox.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        // Bind attributes and uniforms
        texSamplerHandle = GLES30.glGetUniformLocation(program,
                "tex_sampler")
        texCoordHandle = GLES30.glGetAttribLocation(program, "a_texcoord")
        posCoordHandle = GLES30.glGetAttribLocation(program, "a_position")

        // Setup coordinate buffers
        texVertices = ByteBuffer.allocateDirect(
                TEX_VERTICES.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        texVertices!!.put(TEX_VERTICES).position(0)
        posVertices = ByteBuffer.allocateDirect(
                POS_VERTICES.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        posVertices!!.put(POS_VERTICES).position(0)
    }

    fun tearDown() {
        GLES30.glDeleteProgram(program)
    }

    fun updateTextureSize(texWidth: Int, texHeight: Int) {
        this.texWidth = texWidth
        this.texHeight = texHeight
        computeOutputVertices()
    }

    fun updateViewSize(viewWidth: Int, viewHeight: Int) {
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
        computeOutputVertices()
    }

    private fun computeOutputVertices() {
        if (posVertices != null) {
            val imgAspectRatio = texWidth / texHeight as Float
            val viewAspectRatio = viewWidth / viewHeight as Float
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
            posVertices!!.put(coords).position(0)
        }
    }

    fun renderTexture(texId: Int) {
        GLES30.glUseProgram(program)
        GLToolbox.checkGlError("glUseProgram")

        GLES30.glViewport(0, 0, viewWidth,viewHeight)
        GLToolbox.checkGlError("glViewport")

        GLES30.glDisable(GLES30.GL_BLEND)

        GLES30.glVertexAttribPointer(texCoordHandle, 2, GLES30.GL_FLOAT, false,
                0, texVertices)
        GLES30.glEnableVertexAttribArray(texCoordHandle)
        GLES30.glVertexAttribPointer(posCoordHandle, 2, GLES30.GL_FLOAT, false,
                0, posVertices)
        GLES30.glEnableVertexAttribArray(posCoordHandle)
        GLToolbox.checkGlError("vertex attribute setup")

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLToolbox.checkGlError("glActiveTexture")
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId)//把已经处理好的Texture传到GL上面
        GLToolbox.checkGlError("glBindTexture")
        GLES30.glUniform1i(texSamplerHandle, 0)

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
    }

    fun setImageBitmap(bmp: Bitmap) {
        runOnDraw(Runnable {
            // TODO Auto-generated method stub
            loadTexture(bmp)
        })
    }

    private fun loadTexture(bmp: Bitmap) {
        GLES30.glGenTextures(2, textures, 0)
        updateTextureSize(bmp.width, bmp.height)
        imageWidth = bmp.width
        imageHeight = bmp.height
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bmp, 0)
        GLToolbox.initTexParams()
    }

    protected fun runOnDraw(runnable: Runnable) {
        synchronized(runOnDraw) {
            runOnDraw.add(runnable)
        }
    }

}