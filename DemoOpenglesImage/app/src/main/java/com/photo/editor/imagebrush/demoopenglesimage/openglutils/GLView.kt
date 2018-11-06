package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.view.MotionEvent


class GLView(context: Context) : GLSurfaceView(context) {
    private var mRenderer: GlRenderer

    init {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(3)
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = GlRenderer(context)
        setRenderer(mRenderer)

        // Render the view only when there is a change in the drawing data
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//        previewTransitionImage()
    }

    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private var mPreviousX: Float = 0.toFloat()
    private var mPreviousY: Float = 0.toFloat()

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x = e.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx = x - mPreviousX
                var dy = y - mPreviousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx = dx * -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy = dy * -1
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() + (dx + dy) * TOUCH_SCALE_FACTOR)  // = 180.0f / 320
                requestRender()
            }
        }

        mPreviousX = x
        mPreviousY = y
        return true
    }

    var currentAngle = 1.0f
    fun previewTransitionImage() {
        var handle = Handler()
        handle.postDelayed(object : Runnable {
            override fun run() {
                currentAngle+=0.01f
                mRenderer.setScale(currentAngle)
                requestRender()
                if (currentAngle >= 150f) return
                handle.postDelayed(this, 40)
            }
        }, 0)
    }

}