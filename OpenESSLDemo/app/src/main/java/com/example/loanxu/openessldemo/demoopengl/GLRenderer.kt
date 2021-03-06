package com.example.loanxu.openessldemo.demoopengl


import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30
import android.opengl.Matrix
import android.opengl.Matrix.frustumM
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setRotateM
import android.opengl.Matrix.setLookAtM
import android.util.Log
import com.moviemaker.image.video.slideimage.demo.demoopengl.Square


class GLRenderer(context: Context) : GLSurfaceView.Renderer {
    private var mTriangle: Triangle? = null
    private var mSquare: Square? = null
    private var context: Context
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

    private var mAngle: Float = 0.toFloat()

    init {
        this.context = context
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mTriangle = Triangle()
        mSquare = Square()
    }

    override fun onDrawFrame(unused: GL10) {
        val scratch = FloatArray(16)

        // Draw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Draw square
        mSquare!!.draw(mMVPMatrix)

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0f, 0f, 1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // Draw triangle
        mTriangle!!.draw(scratch)

    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES30.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

    }


    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    fun getAngle(): Float {
        return mAngle
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    fun setAngle(angle: Float) {
        mAngle = angle
    }

    companion object {
        /**
         * Utility method for compiling a OpenGL shader.
         *
         *
         * **Note:** When developing shaders, use the checkGlError()
         * method to debug shader coding errors.
         *
         * @param type - Vertex or fragment shader type.
         * @param shaderCode - String containing the shader code.
         * @return - Returns an id for the shader.
         */
        fun loadShader(type: Int, shaderCode: String): Int {
            // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
            val shader = GLES30.glCreateShader(type)
            // add the source code to the shader and compile it
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            return shader
        }

        /**
         * Utility method for debugging OpenGL calls. Provide the name of the call
         * just after making it:
         *
         * <pre>
         * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
         * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
         *
         * If the operation is not successful, the check throws an error.
         *
         * @param glOperation - Name of the OpenGL call to check.
         */
        fun checkGlError(glOperation: String) {
            var error = GLES30.glGetError()
            while ((error) != GLES30.GL_NO_ERROR) {
                error = GLES30.glGetError()
                Log.e("DEBUG", "$glOperation: glError $error")
                throw RuntimeException("$glOperation: glError $error")
            }
        }
    }
}