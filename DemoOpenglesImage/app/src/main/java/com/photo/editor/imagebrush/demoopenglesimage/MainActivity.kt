package com.photo.editor.imagebrush.demoopenglesimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.view.Menu
import android.view.View
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory


class MainActivity : AppCompatActivity() {
//    private var renderer: TextureRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        renderer = TextureRenderer()
//        renderer?.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.gliter1))
//        renderer?.setCurrentEffect(R.id.none)
        glsuface.setEGLContextClientVersion(2)
        glsuface.setRenderer(GlRenderer(baseContext))
        glsuface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

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

