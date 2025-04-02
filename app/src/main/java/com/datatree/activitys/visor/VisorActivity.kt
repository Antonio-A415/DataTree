package com.datatree.activitys.visor

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datatree.databinding.ActivityVisorBinding

class VisorActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    private lateinit var views: ActivityVisorBinding
    private var etiqueta: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityVisorBinding.inflate(layoutInflater)
        setContentView(views.root)
        FullScreen() // Pone la pantalla en modo pantalla completa
        if (allPermissionsGranted()) {
            startCamera() // Iniciar la cámara si los permisos ya están concedidos
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        etiqueta = TextView(this)

        etiqueta!!.setText("Análisis del entorno con OpenCV")
        etiqueta!!.setTextColor(Color.WHITE)
        etiqueta!!.setX(100f) // Posición X en la pantalla

        etiqueta!!.setY(200f) // Posición Y en la pantalla

        views.overlayContainer.addView(etiqueta) // Agregar el TextView al FrameLayout

        // Comienza a cambiar el texto dinámicamente
        // Comienza a cambiar el texto dinámicamente
        //startDynamicTextChange()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9) // Ajusta esto según tus necesidades
                    .build()
                    .also {
                        it.setSurfaceProvider(views.viewFinder.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            } catch (exc: Exception) {
                Log.e("CameraX", "Error al abrir la cámara: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permisos de cámara no concedidos. La aplicación se cerrará.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun FullScreen() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //or View.SYSTEM_UI_FLAG_IMMERSIVE
                )
    }

    private fun cicloFor() {
        for (i in 1..5) {
            print("EL numero es: ${i}")
        }
    }

}