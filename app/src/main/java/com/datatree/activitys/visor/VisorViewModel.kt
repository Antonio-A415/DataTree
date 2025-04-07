package com.datatree.activitys.visor

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class VisorViewModel @Inject constructor() : ViewModel() {


    private var isFlashOn = false



    public fun showAppInfo(context: Context) {
        // Mostrar información sobre la aplicación
        Toast.makeText(context, "DataTree v1.0", Toast.LENGTH_SHORT).show()
    }
}