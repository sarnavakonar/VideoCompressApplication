package com.sarnava.videoapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel

class VideoViewmodel : ViewModel(){

    var uri: Uri? = null
    var path: String? = null
    var compressedUri: Uri? = null
}