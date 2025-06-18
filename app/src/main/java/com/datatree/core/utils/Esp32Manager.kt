package com.datatree.core.utils
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datatree.core.esp32interfaces.Esp32Listener
import com.datatree.infraestructure.dataclass.esp32.SensorData
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class Esp32Manager(private val context: Context)  {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var isConnected = false
    private var listener: Esp32Listener? = null

    // Coroutines
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var readingJob: Job? = null

    // Arduino communication constants
    private val ARDUINO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val BUFFER_SIZE = 1024

    companion object {
        private const val TAG = "Esp32Manager"
        const val COMMAND_START_MONITORING = "START"
        const val COMMAND_STOP_MONITORING = "STOP"
        const val COMMAND_GET_DATA = "DATA"
    }

    fun setListener(listener: Esp32Listener){
        this.listener = listener
    }

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null
    }

    fun connectToDevice(deviceAddress: String) {
        scope.launch {
            try {
                disconnect() // Ensure clean state

                val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                device?.let {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling ActivityCompat#requestPermissions
                        return@launch
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.w(TAG, "Permiso BLUETOOTH_CONNECT no concedido")
                            return@launch
                        }
                    }

                    bluetoothSocket = it.createRfcommSocketToServiceRecord(ARDUINO_UUID)
                    bluetoothSocket?.connect()

                    inputStream = bluetoothSocket?.inputStream
                    outputStream = bluetoothSocket?.outputStream

                    isConnected = true

                    withContext(Dispatchers.Main) {
                        listener?.onConnectionStatusChanged(true)
                    }

                    startReading()
                    Log.d(TAG, "Connected to ESP32: $deviceAddress")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Connection failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    listener?.onError("Error de conexión: ${e.message}")
                    listener?.onConnectionStatusChanged(false)
                }
            }
        }
    }

    fun disconnect() {
        readingJob?.cancel()

        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing connections: ${e.message}")
        }

        inputStream = null
        outputStream = null
        bluetoothSocket = null
        isConnected = false

        // CORREGIDO: Ejecutar el callback en el hilo principal
        scope.launch(Dispatchers.Main) {
            listener?.onConnectionStatusChanged(false)
        }
    }

    fun sendCommand(command: String) {
        if (!isConnected) {
            scope.launch(Dispatchers.Main) {
                listener?.onError("ESP32 no conectado")
            }
            return
        }

        scope.launch {
            try {
                outputStream?.write("$command\n".toByteArray())
                outputStream?.flush()
                Log.d(TAG, "Command sent: $command")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending command: ${e.message}")
                withContext(Dispatchers.Main) {
                    listener?.onError("Error enviando comando: ${e.message}")
                }
            }
        }
    }

    fun startMonitoring() {
        sendCommand(COMMAND_START_MONITORING)
    }

    fun stopMonitoring() {
        sendCommand(COMMAND_STOP_MONITORING)
    }

    fun requestData() {
        sendCommand(COMMAND_GET_DATA)
    }

    private fun startReading() {
        readingJob = scope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            var messageBuilder = StringBuilder()

            while (isConnected && isActive) {
                try {
                    val bytesRead = inputStream?.read(buffer) ?: 0
                    if (bytesRead > 0) {
                        val receivedData = String(buffer, 0, bytesRead)
                        messageBuilder.append(receivedData)

                        // Process complete messages (ending with newline)
                        while (messageBuilder.contains("\n")) {
                            val lineEnd = messageBuilder.indexOf("\n")
                            val completeLine = messageBuilder.substring(0, lineEnd)
                            messageBuilder.delete(0, lineEnd + 1)

                            if (completeLine.isNotEmpty()) {
                                processEsp32Message(completeLine)
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading data: ${e.message}")
                    if (isConnected) {
                        withContext(Dispatchers.Main) {
                            listener?.onError("Error leyendo datos: ${e.message}")
                        }
                        disconnect()
                    }
                    break
                }
            }
        }
    }

    // Almacenamiento temporal para construir los datos completos del sensor
    private var tempSensorData = mutableMapOf<String, Any>()

    private suspend fun processEsp32Message_DEPRECATED(message: String) {
        try {
            Log.d(TAG, "Processing message: $message")

            // Procesar cada línea según el formato de tu ESP32
            when {
                message.contains("Temperatura promedio:") -> {
                    val temp = extractFloatValue(message, "°C")
                    tempSensorData["temperature"] = temp
                    Log.d(TAG, "Temperature: $temp")
                }
                message.contains("Humedad del aire promedio:") -> {
                    val humidity = extractFloatValue(message, "%")
                    tempSensorData["airHumidity"] = humidity
                    Log.d(TAG, "Air Humidity: $humidity")
                }
                message.contains("Humedad del suelo promedio:") -> {
                    // Para humedad del suelo no hay unidad, solo el número
                    val soil = extractFloatValue(message, "").toInt()
                    tempSensorData["soilHumidity"] = soil
                    Log.d(TAG, "Soil Humidity: $soil")
                }
                message.contains("Lecturas válidas:") -> {
                    val valid = extractIntValue(message)
                    tempSensorData["validReadings"] = valid
                    Log.d(TAG, "Valid readings: $valid")
                }
                message.contains("Lecturas fallidas:") -> {
                    val failed = extractIntValue(message)
                    tempSensorData["failedReadings"] = failed
                    Log.d(TAG, "Failed readings: $failed")
                }
                message.contains("Error porcentual:") -> {
                    val errorPercent = extractFloatValue(message, "%")
                    tempSensorData["errorPercent"] = errorPercent
                    Log.d(TAG, "Error percent: $errorPercent")

                    // Esta es la última línea, compilar y enviar datos completos
                    compileAndSendSensorData()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing message: $message, Error: ${e.message}")
        }
    }
    // Para el enfoque delimitado, reemplaza processEsp32Message() con:
    private suspend fun processEsp32Message(message: String) {
        try {
            if (message.startsWith("DATA|")) {
                val parts = message.split("|")
                if (parts.size == 7) {
                    val sensorData = SensorData(
                        temperature = parts[1].toFloat(),
                        airHumidity = parts[2].toFloat(),
                        soilHumidity = parts[3].toInt(),
                        validReadings = parts[4].toInt(),
                        failedReadings = parts[5].toInt(),
                        errorPercent = parts[6].toFloat()
                    )

                    withContext(Dispatchers.Main) {
                        listener?.onDataReceived(sensorData)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing delimited message: $message", e)
        }
    }
    private fun extractFloatValue(message: String, unit: String): Float {
        return try {
            val startIndex = message.lastIndexOf(":") + 1
            val endIndex = if (unit.isNotEmpty()) {
                val unitIndex = message.lastIndexOf(unit)
                if (unitIndex != -1) unitIndex else message.length
            } else {
                message.length
            }
            val valueStr = message.substring(startIndex, endIndex).trim()
            valueStr.toFloat()
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting float from: $message", e)
            0f
        }
    }

    private fun extractIntValue(message: String): Int {
        return try {
            val startIndex = message.lastIndexOf(":") + 1
            val valueStr = message.substring(startIndex).trim().split(" ")[0]
            valueStr.toInt()
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting int from: $message", e)
            0
        }
    }

    private suspend fun compileAndSendSensorData() {
        try {
            val sensorData = SensorData(
                temperature = tempSensorData["temperature"] as? Float ?: 0f,
                airHumidity = tempSensorData["airHumidity"] as? Float ?: 0f,
                soilHumidity = tempSensorData["soilHumidity"] as? Int ?: 0,
                validReadings = tempSensorData["validReadings"] as? Int ?: 0,
                failedReadings = tempSensorData["failedReadings"] as? Int ?: 0,
                errorPercent = tempSensorData["errorPercent"] as? Float ?: 0f
            )

            Log.d(TAG, "Compiled sensor data: $sensorData")

            withContext(Dispatchers.Main) {
                listener?.onDataReceived(sensorData)
            }

            // Limpiar datos temporales
            tempSensorData.clear()

        } catch (e: Exception) {
            Log.e(TAG, "Error compiling sensor data: ${e.message}")
        }
    }

    fun getAvailableDevices(): List<BluetoothDevice> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Permiso BLUETOOTH_CONNECT no concedido")
                return emptyList()
            }
        }

        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun cleanup() {
        disconnect()
        scope.cancel()
    }
}

// Extension functions for easier ESP32 communication
fun Esp32Manager.connectToArduino(context: Context, deviceName: String = "ESP32-DataTree") {
    // Verificar permiso en Android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        != PackageManager.PERMISSION_GRANTED
    ) {
        Log.w("Esp32Manager", "Permiso BLUETOOTH_CONNECT no concedido")
        return
    }

    val devices = getAvailableDevices()
    val esp32Device = devices.find { it.name?.contains(deviceName, ignoreCase = true) == true }

    esp32Device?.let {
        connectToDevice(it.address)
    } ?: run {
        Log.w("Esp32Manager", "ESP32 device '$deviceName' not found in paired devices")
    }
}