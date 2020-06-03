package com.sarnava.videoapplication.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.sarnava.videoapplication.R
import com.sarnava.videoapplication.viewmodel.VideoViewmodel
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private var viewModel: VideoViewmodel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(VideoViewmodel::class.java)

        button.setOnClickListener {

            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            viewModel?.uri = data?.data!!
            viewModel?.path = getPath(data.data)

            Navigation
                .findNavController(activity!!, R.id.nav_host_fragment)
                .navigate(R.id.action_mainFragment_to_videoFragment)
        }
    }


    fun getPath(uri: Uri?): String? {

        val projection =
            arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor = activity!!.getContentResolver().query(uri!!, projection, null, null, null)!!
        return if (cursor != null) {

            val column_index: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }
}
