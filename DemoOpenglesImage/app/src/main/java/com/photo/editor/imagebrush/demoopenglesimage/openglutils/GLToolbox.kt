package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import android.opengl.GLES30


class GLToolbox {
    companion object {
        fun loadShader(shaderType: Int, source: String): Int {
            var shader = GLES30.glCreateShader(shaderType)
            if (shader != 0) {
                GLES30.glShaderSource(shader, source)
                GLES30.glCompileShader(shader)
                val compiled = IntArray(1)
                GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
                if (compiled[0] == 0) {
                    val info = GLES30.glGetShaderInfoLog(shader)
                    GLES30.glDeleteShader(shader)
                    shader = 0
                    throw RuntimeException("Could not compile shader " +
                            shaderType + ":" + info)
                }
            }
            return shader
        }

        fun createProgram(vertexSource: String,
                          fragmentSource: String): Int {
            val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
            if (vertexShader == 0) {
                return 0
            }
            val pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
            if (pixelShader == 0) {
                return 0
            }

            var program = GLES30.glCreateProgram()
            if (program != 0) {
                GLES30.glAttachShader(program, vertexShader)
                checkGlError("glAttachShader")
                GLES30.glAttachShader(program, pixelShader)
                checkGlError("glAttachShader")
                GLES30.glLinkProgram(program)
                val linkStatus = IntArray(1)
                GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus,
                        0)
                if (linkStatus[0] != GLES30.GL_TRUE) {
                    val info = GLES30.glGetProgramInfoLog(program)
                    GLES30.glDeleteProgram(program)
                    program = 0
                    throw RuntimeException("Could not link program: $info")
                }
            }
            return program
        }

        fun checkGlError(op: String) {
            val error: Int = GLES30.glGetError()
            while (error != GLES30.GL_NO_ERROR) {
                throw RuntimeException("$op: glError $error")
            }
        }

        fun initTexParams() {
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        }
    }
}