package com.datatree.activitys.visor;

import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.android.Utils;
import org.opencv.core.MatOfByte;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import com.datatree.databinding.ActivityVisorBinding;

public class VisorOpenCV extends AppCompatActivity {

    private static final String TAG = "VisorOpenCV";
    private ActivityVisorBinding binding;
    private PreviewView previewView;
    private Mat matInputFrame;
    private TextView etiqueta;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Iniciando el método onCreate");
        binding = ActivityVisorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        blockScreen(); // Evita que la pantalla se apague mientras usamos la app.

        previewView = binding.viewFinder;

        // Inicializar OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Error al inicializar OpenCV");
            Toast.makeText(this, "Error al inicializar OpenCV", Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "OpenCV correctamente inicializado");
            startCamera();  // Iniciar CameraX después de inicializar OpenCV
            Toast.makeText(this, "OpenCV correctamente inicializado", Toast.LENGTH_LONG);
        }
        etiqueta = new TextView(this);
        etiqueta.setText("Análisis del entorno con OpenCV");
        etiqueta.setTextColor(Color.WHITE);
        etiqueta.setX(100); // Posición X en la pantalla
        etiqueta.setY(200); // Posición Y en la pantalla
        binding.overlayContainer.addView(etiqueta); // Agregar el TextView al FrameLayout
        // Comienza a cambiar el texto dinámicamente
        startDynamicTextChange();
    }
    private void startDynamicTextChange() {
        // Crea un Runnable que cambia el texto de la etiqueta
        Runnable textChanger = new Runnable() {
            int counter = 0;

            @Override
            public void run() {
                // Cambia el texto de la etiqueta según el contador
                etiqueta.setText("Análisis del con OpenCV " + counter);
                counter++;

                // Reinicia el contador si llega a 10
                if (counter > 10) {
                    counter = 0;
                }

                // Vuelve a programar este Runnable después de 1000 ms
                handler.postDelayed(this, 1000);
            }
        };

        // Inicia el primer cambio de texto
        handler.post(textChanger);
    }


    private void blockScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Configurar CameraX Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Configurar Image Analysis para capturar frames
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::analyzeImage);

                // Seleccionar la cámara trasera
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unir todo: Preview + ImageAnalysis
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error al iniciar la cámara: ", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Este método procesa los fotogramas de la cámara con OpenCV
    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        // Convertir el frame a Mat para procesarlo en OpenCV
        if (imageProxy.getFormat() == ImageFormat.YUV_420_888) {
            ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            Mat yuvMat = new Mat(imageProxy.getHeight() + imageProxy.getHeight() / 2, imageProxy.getWidth(), CvType.CV_8UC1);
            yuvMat.put(0, 0, bytes);

            // Convertir de YUV a RGB (si es necesario)
            matInputFrame = new Mat(imageProxy.getHeight(), imageProxy.getWidth(), CvType.CV_8UC3);
            Imgproc.cvtColor(yuvMat, matInputFrame, Imgproc.COLOR_YUV2RGBA_I420);

            // Procesamiento en OpenCV (ejemplo: aplicar un filtro)
            Imgproc.cvtColor(matInputFrame, matInputFrame, Imgproc.COLOR_RGBA2GRAY);

            // Aquí puedes hacer más procesamiento con OpenCV y luego mostrarlo en la pantalla o realizar análisis.

            // Liberar recursos
            yuvMat.release();
        }

        // Importante: cerrar el frame después de procesarlo
        imageProxy.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Error al inicializar OpenCV");
            Toast.makeText(this, "Error al inicializar OpenCV", Toast.LENGTH_LONG).show();
        }
    }
}

