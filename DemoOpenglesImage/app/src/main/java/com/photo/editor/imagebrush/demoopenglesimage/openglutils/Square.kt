package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import java.nio.FloatBuffer
import android.opengl.GLES30
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.opengl.GLES20


class Square {
    private val vertices = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
    private val textureVertices = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
    // creat buffer object
    private var verticesBuffer: FloatBuffer? = null
    private var textureBuffer: FloatBuffer? = null

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var textureHandle: Int = 0
    private var texturePositionHandle: Int = 0
    private var vPMatrixHandle: Int = 0
//    private val mMtrxView = FloatArray(16)
//    /**
//     * creat fragment
//     */
//    private val vertexShaderCode =
//            "uniform mat4 u_MVPMatrix;" +
//            "attribute vec4 aPosition;" +
//            "attribute vec2 aTexPosition;" +
//            "varying vec2 vTexPosition;" +
//            "void main() {" +
//            "  gl_Position = u_MVPMatrix * aPosition;" +
//            "  vTexPosition = aTexPosition;" +
//            "}"
//
//    private val fragmentShaderCode = (
//            "precision mediump float;" +
//                    "uniform sampler2D uTexture;" +
//                    "varying vec2 vTexPosition;" +
//                    "void main() {" +
//                    "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
//                    "}")

    val vs_Image = (
            "uniform mat4 uMVPMatrix;"
                    + "attribute vec4 vPosition;"
                    + "attribute vec2 a_texCoord;"
                    + "varying vec2 v_texCoord;"
                    + "void main() {"
                    + "     gl_Position = uMVPMatrix * vPosition;"
                    + "     v_texCoord = a_texCoord;"
                    + "}")
    //    val fs_Image = ("precision mediump float;"
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
//            + "}")
    val fs_Image = ("precision mediump float;"
            + "varying vec2 v_texCoord;"
            + "uniform sampler2D s_texture;"
            + "void main() {"
            + "     gl_FragColor = texture2D(s_texture, v_texCoord);"
            + "}")

    init {
        initializeBuffers()
        creatProgram()
    }

    /**
     * creat program
     */
    fun creatProgram() {
        program = GLToolbox.createProgram(vs_Image, fs_Image)
    }

    private fun initializeBuffers() {
        var buff = ByteBuffer.allocateDirect(vertices.size * 4)
        buff.order(ByteOrder.nativeOrder())
        verticesBuffer = buff.asFloatBuffer()
        verticesBuffer?.put(vertices)
        verticesBuffer?.position(0)

        buff = ByteBuffer.allocateDirect(textureVertices.size * 4)
        buff.order(ByteOrder.nativeOrder())
        textureBuffer = buff.asFloatBuffer()
        textureBuffer?.put(textureVertices)
        textureBuffer?.position(0)
    }

    /**
     * draw
     */
    fun draw(texture: Int, mvpMatrix: FloatArray) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glUseProgram(program)
        GLES30.glDisable(GLES30.GL_BLEND)
        positionHandle = GLES30.glGetAttribLocation(program, "vPosition")
        textureHandle = GLES30.glGetUniformLocation(program, "s_texture")
        texturePositionHandle = GLES30.glGetAttribLocation(program, "a_texCoord")
        vPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
        GLES30.glVertexAttribPointer(texturePositionHandle, 2, GLES30.GL_FLOAT, false, 0, textureBuffer)
        GLES30.glEnableVertexAttribArray(texturePositionHandle)
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture)
        GLES30.glUniform1i(textureHandle, 0)

        GLES30.glVertexAttribPointer(positionHandle, 2, GLES30.GL_FLOAT, false, 0, verticesBuffer)
        GLES30.glEnableVertexAttribArray(positionHandle)


        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
    }
}