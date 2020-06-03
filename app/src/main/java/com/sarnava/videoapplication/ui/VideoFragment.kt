package com.sarnava.videoapplication.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.sarnava.videoapplication.R
import com.sarnava.videoapplication.viewmodel.VideoViewmodel
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.File


class VideoFragment : Fragment() {

    private var ffmpeg: FFmpeg? = null
    val TAG = "boom"

    private var viewModel: VideoViewmodel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(VideoViewmodel::class.java)

        videoView.setVideoURI(viewModel!!.uri)
        videoView.start()
        videoView.setOnPreparedListener { mp -> mp.isLooping = true }

        ffmpeg = FFmpeg.getInstance(activity)
        loadFFMpegBinary()

        button.setOnClickListener {

            if (activity!!.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

                compress()
            }
        }
    }

    private fun loadFFMpegBinary() {
        try {
            ffmpeg?.loadBinary(object :
                LoadBinaryResponseHandler() {

                override fun onFailure() {
                    Toast.makeText(activity, "Unsupported", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            Toast.makeText(activity, "Unsupported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun execFFmpegBinary(command: Array<String>) {
        try {
            ffmpeg!!.execute(
                command,
                object : ExecuteBinaryResponseHandler() {

                    override fun onFailure(s: String) {
                        Log.e("boom FAILED : ", s)
                        pb?.visibility = GONE
                    }

                    override fun onSuccess(s: String) {
                        Log.e("boom SUCCESS : ", s)
                        pb?.visibility = GONE
                    }

                    override fun onProgress(s: String) {
                        Log.e(TAG, "Progress command : ffmpeg $command")
                        Log.e("progress : ", s)
                    }

                    override fun onStart() {
                        Log.e(TAG, "Started command : ffmpeg $command")
                        pb.visibility = VISIBLE
                        button.isEnabled = false
                        Toast.makeText(activity, "Compressing...", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFinish() {
                        Log.e(TAG, "Finished command : ffmpeg $command")

                        Navigation
                            .findNavController(activity!!,
                                R.id.nav_host_fragment
                            )
                            .navigate(R.id.action_videoFragment_to_compressedVideoFragment)
                    }
                })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // do nothing for now

        }
    }

    private fun getOutputMediaFile(): File? {

        var parentDir = File(activity!!.filesDir, "CompressedVideos")
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                return null
            }
        }

        val filename = "video_compress"
        return File(parentDir.path + File.separator + filename + ".mp4")
    }

    private fun compress(){

        val bitrate = et.text.toString()

        if (bitrate.isEmpty()){
            return
        }

        val outputPath = Uri.fromFile(getOutputMediaFile())
        viewModel!!.compressedUri = outputPath

        var commandArray: Array<String>
        commandArray = arrayOf(
            "-y",
            "-i",
            viewModel!!.path!!,
            "-b:v",
            bitrate,
            outputPath.path!!
        )
        execFFmpegBinary(commandArray)
    }

    override fun onStop() {
        super.onStop()
        ffmpeg!!.killRunningProcesses()
    }
}
