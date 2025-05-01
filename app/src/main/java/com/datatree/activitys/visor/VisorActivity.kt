package com.datatree.activitys.visor

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.internal.annotation.CameraExecutor
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datatree.R
import com.datatree.core.interfaces.OpenAISearchService
import com.datatree.core.interfaces.OpenAIService
import com.datatree.databinding.ActivityVisorBinding
import com.datatree.infraestructure.dataclass.visordataclasses.ContentItem
import com.datatree.infraestructure.dataclass.visordataclasses.ImageUrl
import com.datatree.infraestructure.dataclass.visordataclasses.Message
import com.datatree.infraestructure.dataclass.visordataclasses.OpenAIRequest
import com.datatree.infraestructure.dataclass.visordataclasses.SearchRequest
import com.datatree.infraestructure.dataclass.visordataclasses.Tool
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class VisorActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    private lateinit var views: ActivityVisorBinding


    private lateinit var cameraExecutor: ExecutorService

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // OpenAI API Key - Deberías almacenar esto de forma segura (BuildConfig, etc.)
    private val openAIApiKey = "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"
    private lateinit var openAIService: OpenAIService
    private lateinit var openAISearchService: OpenAISearchService
    //private val client

    //inyectar al dependencia
    private val viewModelVisor: VisorViewModel by viewModels()

    private lateinit var imageCapture: ImageCapture



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityVisorBinding.inflate(layoutInflater)
        setContentView(views.root)

        // Inicializar CameraManager

        FullScreen() // Pone la pantalla en modo pantalla completa
        if (allPermissionsGranted()) {
            startCamera() // Iniciar la cámara si los permisos ya están concedidos
            cameraExecutor = Executors.newSingleThreadExecutor()

            // Inicializar Retrofit para OpenAI API
            setupRetrofit()

            // Iniciar la cámara
            startCamera()

            // Configurar listeners de botones
            findViewById<Button>(R.id.captureButton).setOnClickListener {
                captureAndAnalyzeImage()
                //captureAndSendToOpenAI()
            }
            views.flashButton.setOnClickListener{
                Toast.makeText(this, "En proceso de desarrollo", Toast.LENGTH_SHORT).show()
            }


            findViewById<ImageButton>(R.id.infoButton).setOnClickListener {
                viewModelVisor.showAppInfo(this)
            }



            /*
            findViewById<Button>(R.id.searchInfoButton).setOnClickListener {
                val plantName = findViewById<TextView>(R.id.plantNameText).text.toString()
                if (plantName != "Planta desconocida" && plantName.isNotEmpty()) {
                    searchPlantInfo(plantName)
                } else {
                    Toast.makeText(this, "Primero identifica una planta", Toast.LENGTH_SHORT).show()
                }
            }

            */

            // Encuentra el View o CardView que deseas ocultar o mostrar
            val infoPanel = findViewById<CardView>(R.id.plantInfoPanel)

// Agrega un listener al panel de información (o un botón)
            infoPanel.setOnClickListener {
                // Alterna la visibilidad del panel
                if (infoPanel.visibility == View.VISIBLE) {
                    infoPanel.visibility = View.GONE // Oculta el panel
                } else {
                    infoPanel.visibility = View.VISIBLE // Muestra el panel
                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }


    }

/*
    private fun toggleFlash() {
        try {
            cameraId?.let { id ->
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(id, isFlashOn)
                val message = if (isFlashOn) "Flash activado" else "Flash desactivado"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Actualizar icono del botón
                //val flashIcon = if (isFlashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                //views.flashButton.setImageResource(flashIcon)
            } ?: run {
                Toast.makeText(this, "Cámara no disponible", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CameraAccessException) {
            Log.e("CameraFlash", "Error al acceder al flash", e)
            Toast.makeText(this, "Error al acceder al flash", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("CameraFlash", "Error inesperado", e)
            Toast.makeText(this, "Error con el flash", Toast.LENGTH_SHORT).show()
        }
    }

    */

    private fun captureAndAnalyzeImage() {
        views.loadingIndicator.visibility = View.VISIBLE
        views.analyzeStatusText.apply {
            text = "Capturando imagen..."
            visibility = View.VISIBLE
        }

        // Crear archivo temporal para la captura (opcional)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            File(cacheDir, "temp_plant_${System.currentTimeMillis()}.jpg")
        ).build()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)

                    try {
                        // Convertir ImageProxy a Bitmap
                        val bitmap = imageProxyToBitmap(imageProxy)
                        imageProxy.close()

                        // Procesar la imagen
                        processCapturedImage(bitmap)
                    } catch (e: Exception) {
                        views.loadingIndicator.visibility = View.GONE
                        views.analyzeStatusText.visibility = View.GONE
                        Toast.makeText(this@VisorActivity,
                            "Error al procesar imagen: ${e.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    views.loadingIndicator.visibility = View.GONE
                    views.analyzeStatusText.visibility = View.GONE
                    Toast.makeText(this@VisorActivity,
                        "Error en captura: ${exception.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Función auxiliar para convertir ImageProxy a Bitmap
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Rotar la imagen según la orientación
        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }

        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }


    private fun optimizeBitmapForAnalysis(bitmap: Bitmap): Bitmap {
        // Reducir tamaño si es muy grande (manteniendo aspect ratio)
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height

        val scale = when {
            width > height && width > maxDimension -> maxDimension.toFloat() / width
            height > width && height > maxDimension -> maxDimension.toFloat() / height
            else -> 1f
        }

        return if (scale < 1f) {
            Bitmap.createScaledBitmap(bitmap,
                (width * scale).toInt(),
                (height * scale).toInt(),
                true)
        } else {
            bitmap
        }
    }

    private fun processCapturedImage(bitmap: Bitmap) {
        views.analyzeStatusText.text = "Analizando imagen..."

        // Forzar detección como planta
        views.plantDetectionBox.visibility = View.VISIBLE
        // Mostrar temporizador de progreso
        val progressTimer = Timer()
        val timerTask = object : TimerTask() {
            var seconds = 0
            override fun run() {
                runOnUiThread {
                    views.analyzeStatusText.text = "Analizando... ${seconds}s"
                    seconds++
                }
            }
        }
        progressTimer.scheduleAtFixedRate(timerTask, 0, 1000)

        coroutineScope.launch {
            try {
                // Usar withTimeout para limitar el tiempo de espera
                val plantInfo = withTimeout(30_000) { // 30 segundos de timeout
                    getPlantInfoFromOpenAI("planta", bitmap)
                }
                progressTimer.cancel()
                displayPlantInfo(plantInfo)
            } catch (e: TimeoutCancellationException) {
                progressTimer.cancel()
                views.loadingIndicator.visibility = View.GONE
                views.analyzeStatusText.text = "Tiempo agotado. Intenta nuevamente."
                Toast.makeText(this@VisorActivity,
                    "El análisis tomó demasiado tiempo",
                    Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                progressTimer.cancel()
                views.loadingIndicator.visibility = View.GONE
                views.analyzeStatusText.text = "Intentalo de nuevo"
                Toast.makeText(this@VisorActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

/*
    private fun captureAndAnalyzeImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val previewView = findViewById<PreviewView>(R.id.viewFinder)

            val bitmap = Bitmap.createBitmap(previewView.width, previewView.height, Bitmap.Config.ARGB_8888)

            views.loadingIndicator.visibility = View.VISIBLE

            views.analyzeStatusText.apply {
                text = "Analizando imagen..."
                visibility = View.VISIBLE
            }


            val location = IntArray(2)
            previewView.getLocationInWindow(location)
            val x = location[0]
            val y = location[1]

            val handler = Handler(Looper.getMainLooper())

            PixelCopy.request(
                this.window,
                Rect(x, y, x + previewView.width, y + previewView.height),
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        detectPlantInImage(bitmap)
                        Toast.makeText(this, "Se capturo la imagen", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
                    }

                    views.loadingIndicator.visibility = View.GONE
                    views.analyzeStatusText.visibility = View.GONE
                },
                handler
            )
        } else {
            Toast.makeText(this, "La captura no es compatible con esta versión de Android", Toast.LENGTH_SHORT).show()
        }
    }
*/


    private fun captureAndSendToOpenAI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val previewView = findViewById<PreviewView>(R.id.viewFinder)
            val bitmap = Bitmap.createBitmap(previewView.width, previewView.height, Bitmap.Config.ARGB_8888)

            views.loadingIndicator.visibility = View.VISIBLE

            views.analyzeStatusText.apply {
                text = "Analizando imagen..."
                visibility = View.VISIBLE
            }

            val location = IntArray(2)
            previewView.getLocationInWindow(location)
            val x = location[0]
            val y = location[1]

            val handler = Handler(Looper.getMainLooper())

            PixelCopy.request(
                this.window,
                Rect(x, y, x + previewView.width, y + previewView.height),
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        // Mostrar recuadro de detección
                        findViewById<View>(R.id.plantDetectionBox).visibility = View.VISIBLE

                        // Enviar directamente a OpenAI
                        coroutineScope.launch {
                            try {
                                val plantInfo = getPlantInfoFromOpenAI("planta", bitmap)
                                displayPlantInfo(plantInfo)
                            } catch (e: Exception) {
                                Toast.makeText(this@VisorActivity,
                                    "Error al obtener información: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
                    }
                    views.loadingIndicator.visibility = View.GONE
                    views.analyzeStatusText.visibility = View.GONE

                },
                handler
            )
        } else {
            Toast.makeText(this, "La captura no es compatible con esta versión de Android", Toast.LENGTH_SHORT).show()
        }
    }
    private fun detectPlantInImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.4f) // Ajustar umbral de confianza
            .build())

        labeler.process(image)
            .addOnSuccessListener { labels ->
                var maxConfidence = 0f
                var plantLabel = ""

                // Buscar etiquetas relacionadas con plantas con mejor confianza
                for (label in labels) {
                    val text = label.text.lowercase()
                    val confidence = label.confidence

                    // Lista más amplia de palabras clave
                    val plantKeywords = listOf("plant", "flower", "tree", "leaf", "foliage",
                        "vegetation", "herb", "shrub", "planta", "flor", "árbol", "hoja",
                        "vegetal", "hierba", "arbusto")

                    if (plantKeywords.any { text.contains(it) } && confidence > maxConfidence) {
                        maxConfidence = confidence
                        plantLabel = label.text
                    }
                }

                if (plantLabel.isNotEmpty()) {
                    // Mostrar recuadro de detección
                    findViewById<View>(R.id.plantDetectionBox).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.instructionText).text = "Detectado: $plantLabel (${(maxConfidence * 100).toInt()}%)"

                    // Consultar a OpenAI
                    coroutineScope.launch {
                        try {
                            val plantInfo = getPlantInfoFromOpenAI(plantLabel, bitmap)
                            displayPlantInfo(plantInfo)
                        } catch (e: Exception) {
                            // Manejo de errores
                        }
                    }
                } else {
                    // Mostrar mensaje más informativo
                    val topLabels = labels.take(3).joinToString {
                        "${it.text} (${(it.confidence * 100).toInt()}%)"
                    }
                    Toast.makeText(this,"No se detectó planta claramente. Top etiquetas: $topLabels", Toast.LENGTH_LONG).show()
                    Log.e("etiquetas: ",topLabels.toString())
                }

                // Ocultar indicadores
                findViewById<ProgressBar>(R.id.loadingIndicator).visibility = View.GONE
                findViewById<TextView>(R.id.analyzeStatusText).visibility = View.GONE
            }
            .addOnFailureListener { e ->
                // Manejo de errores
            }
    }

    /*
    private fun detectPlantInImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                var isPlant = false
                var plantLabel = ""

                // Buscar etiquetas relacionadas con plantas
                for (label in labels) {
                    val text = label.text.lowercase()
                    if (text.contains("plant") || text.contains("flower") ||
                        text.contains("tree") || text.contains("leaf") ||
                        text.contains("planta") || text.contains("flor") ||
                        text.contains("árbol") || text.contains("hoja")) {
                        isPlant = true
                        plantLabel = label.text
                        break
                    }
                }

                if (isPlant) {
                    // Mostrar recuadro de detección
                    findViewById<View>(R.id.plantDetectionBox).visibility = View.VISIBLE

                    // Consultar a OpenAI para obtener información sobre la planta
                    coroutineScope.launch {
                        try {
                            val plantInfo = getPlantInfoFromOpenAI(plantLabel, bitmap)
                            displayPlantInfo(plantInfo)
                        } catch (e: Exception) {
                            Toast.makeText(this@VisorActivity,
                                "Error al obtener información: ${e.message}",
                                Toast.LENGTH_SHORT).show()
                            findViewById<ProgressBar>(R.id.loadingIndicator).visibility = View.GONE
                            findViewById<TextView>(R.id.analyzeStatusText).visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(this, "No se detectó ninguna planta en la imagen",
                        Toast.LENGTH_SHORT).show()
                    findViewById<ProgressBar>(R.id.loadingIndicator).visibility = View.GONE
                    findViewById<TextView>(R.id.analyzeStatusText).visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error en la detección: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                findViewById<ProgressBar>(R.id.loadingIndicator).visibility = View.GONE
                findViewById<TextView>(R.id.analyzeStatusText).visibility = View.GONE
            }
    }

    */

    private fun displayPlantInfo(plantInfo: JSONObject) {
        runOnUiThread {
            findViewById<ProgressBar>(R.id.loadingIndicator).visibility = View.GONE
            findViewById<TextView>(R.id.analyzeStatusText).visibility = View.GONE

            // Mostrar panel de información
            val infoPanel = findViewById<CardView>(R.id.plantInfoPanel)
            infoPanel.visibility = View.VISIBLE

            // Actualizar información
            findViewById<TextView>(R.id.plantNameText).text = plantInfo.optString("nombre", "Planta desconocida")
            findViewById<TextView>(R.id.plantSuggestionText).text = plantInfo.optString("sugerencias", "No hay sugerencias disponibles")

            // Establecer calificación de salud
            val healthRating = plantInfo.optDouble("salud", 0.0).toFloat()
            findViewById<RatingBar>(R.id.healthRatingBar).rating = healthRating

            // Mostrar botón de búsqueda web
            //findViewById<Button>(R.id.searchInfoButton).visibility = View.VISIBLE
        }
    }

    private suspend fun getPlantInfoFromOpenAI(plantType: String, bitmap: Bitmap): JSONObject {
        val optimizedBitmap = optimizeBitmapForAnalysis(bitmap)

        // Convertir bitmap a base64 para enviar a OpenAI
        val byteArrayOutputStream = ByteArrayOutputStream()
        optimizedBitmap.compress(Bitmap.CompressFormat.JPEG,75,byteArrayOutputStream)
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        val base64Image = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)

        // Preparar la solicitud para OpenAI
        val content = mutableListOf<ContentItem>()


        // Agregar el texto
        content.add(ContentItem(
            type = "text",
            text = """
        Esta es una imagen de lo que parece ser una $plantType.
        Por favor, identifica exactamente qué tipo de planta es,
        proporciona sugerencias para su cuidado y evalúa su estado de salud
        en una escala del 1 al 5.
        Devuelve SOLO la respuesta en formato JSON sin texto adicional,
        con los campos 'nombre', 'sugerencias' y 'salud'.
        
        Ejemplo de formato esperado:
        {
            "nombre": "Nombre de la planta (Nombre cienfico)",
            "sugerencias": "Sugerencias para el cuidado",
            "salud": 4
        }
    """.trimIndent()))


        // Agregar la imagen
        content.add(
            ContentItem(
            type = "image_url",
            image_url = ImageUrl("data:image/jpeg;base64,$base64Image")
        )
        )

        val message = Message(role = "user", content = content)

        val request = OpenAIRequest(
            model = "gpt-4-turbo",
            messages = listOf(message),
            max_tokens = 300
        )

        try {
            val gson = Gson()
            Log.d("OpenAI", "Request: ${gson.toJson(request)}")  // <-- Revisa en logcat
            // Realizar la solicitud con Retrofit
            val response = withContext(Dispatchers.IO) {


                openAIService.getCompletion("Bearer $openAIApiKey", request)
            }

            val response_gson = Gson()
            Log.d("OpenAI respuesta", "Response: ${response_gson.toJson(response)}")  // <-- Revisa en logcat

            // Procesar la respuesta
            val responseContent = response.choices[0].message.content
            return JSONObject(responseContent)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("OpenAI", "Error 400: $errorBody")
            throw Exception("Error en la API: ${e.message}")

        }
    }

    /*
    private fun searchPlantInfo(plantName: String) {
        // Mostrar indicador de carga
        findViewById<ProgressBar>(R.id.searchLoadingIndicator).visibility = View.VISIBLE
        findViewById<TextView>(R.id.searchStatusText).visibility = View.VISIBLE
        findViewById<TextView>(R.id.searchStatusText).text = "Buscando información actualizada..."

        coroutineScope.launch {
            try {
                val additionalInfo = searchWebForPlantInfo(plantName)
                displayAdditionalInfo(additionalInfo)
            } catch (e: Exception) {
                Toast.makeText(this@VisorActivity,
                    "Error al buscar información: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                findViewById<ProgressBar>(R.id.searchLoadingIndicator).visibility = View.GONE
                findViewById<TextView>(R.id.searchStatusText).visibility = View.GONE
            }
        }
    }
    */

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openAIService = retrofit.create(OpenAIService::class.java)
        openAISearchService = retrofit.create(OpenAISearchService::class.java)
    }
    private suspend fun searchWebForPlantInfo(plantName: String): String {
        val request = SearchRequest(
            model = "gpt-4-turbo",
            tools = listOf(Tool(type = "web_search")),
            input = "Proporciona información actualizada y consejos de cuidado específicos para la planta $plantName. Incluye datos sobre enfermedades comunes, temporada ideal de crecimiento y recomendaciones particulares para su cultivo."
        )

        try {
            // Realizar la solicitud con Retrofit
            val response = withContext(Dispatchers.IO) {
                openAISearchService.searchWeb("Bearer $openAIApiKey", request)
            }

            return response.output
        } catch (e: Exception) {
            Log.e("BUSQUEDA WEB: ",e.message.toString())
            throw Exception("Error en la búsqueda web: ${e.message}")
        }
    }

    /*
    private fun displayAdditionalInfo(additionalInfo: String) {
        runOnUiThread {
            findViewById<ProgressBar>(R.id.searchLoadingIndicator).visibility = View.GONE
            findViewById<TextView>(R.id.searchStatusText).visibility = View.GONE

            // Mostrar panel de información adicional
            val additionalInfoPanel = findViewById<CardView>(R.id.additionalInfoPanel)
            additionalInfoPanel.visibility = View.VISIBLE

            // Actualizar información
            findViewById<TextView>(R.id.webInfoText).text = additionalInfo
        }
    }
    */

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Inicializar imageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Configuración del preview
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()
                    .also {
                        it.setSurfaceProvider(views.viewFinder.surfaceProvider)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                } catch(exc: Exception) {
                    Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
                }
            } catch (exc: Exception) {
                Log.e("CameraX", "Error al abrir la cámara", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /*
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()
                    .also {
                        it.setSurfaceProvider(views.viewFinder.surfaceProvider)
                    }


                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            // Aquí podría ir un análisis en tiempo real si lo necesitas
                            imageProxy.close()
                        }
                    }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalyzer)
                } catch(exc: Exception) {
                    Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
                }
            } catch (exc: Exception) {
                Log.e("CameraX", "Error al abrir la cámara: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
    */

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


    override fun onPause() {
        super.onPause()

        /* Apagar el flash cuando la actividad pasa a segundo plano
        if (isFlashOn) {
            toggleFlash()
        }
        */

    }


    override fun onDestroy() {
        super.onDestroy()

        /*Asegurarse de apagar el flash al destruir la actividad
        if (isFlashOn) {
            try {
                cameraId?.let { cameraManager.setTorchMode(it, false) }
            } catch (e: Exception) {
                Log.e("CameraFlash", "Error al apagar flash", e)
            }
        }

         */
        cameraExecutor.shutdown()
        coroutineScope.cancel()
    }

}