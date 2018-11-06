package com.example.loanxu.openessldemo.demoopengl


import android.content.Context
import android.graphics.Bitmap
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import android.view.MotionEvent
import android.opengl.GLSurfaceView


class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private var renderer: BitmapRenderer

    init {
        // Create an OpenGL ES 3.0 context.  CHANGED to 3.0  JW.
        setEGLContextClientVersion(3)
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        // Set the Renderer for drawing on the GLSurfaceView
        renderer = BitmapRenderer()
        setRenderer(renderer)

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
    }

    fun setBitmap(bitmap: Bitmap) {
        renderer.setImageBitmap(bitmap)
    }
//
//    private val TOUCH_SCALE_FACTOR = 180.0f / 320
//    private var mPreviousX: Float = 0.toFloat()
//    private var mPreviousY: Float = 0.toFloat()
//
//    override fun onTouchEvent(e: MotionEvent): Boolean {
//        // MotionEvent reports input details from the touch screen
//        // and other input controls. In this case, you are only
//        // interested in events where the touch position changed.
//
//        val x = e.x
//        val y = e.y
//
//        when (e.action) {
//            MotionEvent.ACTION_MOVE -> {
//
//                var dx = x - mPreviousX
//                var dy = y - mPreviousY
//
//                // reverse direction of rotation above the mid-line
//                if (y > getHeight() / 2) {
//                    dx = dx * -1
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < getWidth() / 2) {
//                    dy = dy * -1
//                }
//
////                mRenderer.setAngle(
////                        mRenderer.getAngle() + (dx + dy) * TOUCH_SCALE_FACTOR)  // = 180.0f / 320
//                requestRender()
//            }
//        }
//
//        mPreviousX = x
//        mPreviousY = y
//        return true
//    }
}