package com.photo.editor.imagebrush.demoopenglesimage.openglutils

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.opengl.GLSurfaceView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import com.photo.editor.imagebrush.demoopenglesimage.R


class MainActivity : AppCompatActivity() {
//    private var renderer: TextureRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        var glview = GLView(this)
        setContentView(glview)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        renderer?.setCurrentEffect(item.getItemId())
        glsuface.requestRender()
        return true
    }
}

