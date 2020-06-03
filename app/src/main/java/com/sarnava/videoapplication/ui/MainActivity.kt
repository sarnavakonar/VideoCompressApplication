package com.sarnava.videoapplication.ui

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.sarnava.videoapplication.R
import com.sarnava.videoapplication.viewmodel.VideoViewmodel

class MainActivity : AppCompatActivity() {

    private var viewModel: VideoViewmodel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(VideoViewmodel::class.java)

        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE),
            1);
    }

}
