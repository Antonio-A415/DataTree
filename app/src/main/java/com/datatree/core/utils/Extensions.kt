package com.datatree.core.utils

import com.permissionx.guolindev.PermissionX

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getCameraAndMicPermission(success:()->Unit){
    PermissionX.init(this)
        .permissions(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO)
        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                success()
            } else {
                Toast.makeText(this, "Es necesario el permiso de la cámara y el microfono", Toast.LENGTH_SHORT)
                    .show()
            }
        }

}

fun Int.convertToHumanTime() : String{
    val seconds = this%60
    val minutes = this/60
    val secondsString = if (seconds<10) "0$seconds" else "$seconds"
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
    return "$minutesString:$secondsString"
}