package com.sarnava.videoapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.sarnava.videoapplication.R
import com.sarnava.videoapplication.viewmodel.VideoViewmodel
import kotlinx.android.synthetic.main.fragment_compressed_video.*
import kotlinx.android.synthetic.main.fragment_compressed_video.videoView
import kotlinx.android.synthetic.main.fragment_video.*

class CompressedVideoFragment : Fragment() {

    private var viewModel: VideoViewmodel? = null
    private var position:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compressed_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(VideoViewmodel::class.java)

        videoView.setVideoURI(viewModel!!.compressedUri)
        videoView.start()
        videoView.setOnPreparedListener { mp -> mp.isLooping = true }

        play.setOnClickListener {

            if(position != 0){
                //videoView.seekTo(position)
                videoView.start()
            }
        }

        pause.setOnClickListener {
            position = videoView.currentPosition
            videoView.pause()
        }
    }
}
