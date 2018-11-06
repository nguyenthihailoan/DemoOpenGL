package com.example.loanxu.openessldemo

import com.example.loanxu.openessldemo.demoopengl.MyGLSurfaceView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
class DemoGenerateActivity : AppCompatActivity() {
    var bitmaps = ArrayList<Bitmap>()
    lateinit var glView: MyGLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_1)
        var bmp = Bitmap.createScaledBitmap(bitmap,480,480,true)
        glView= MyGLSurfaceView(this)
        glView.setBitmap(bmp)
        setContentView(glView)
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }
}
