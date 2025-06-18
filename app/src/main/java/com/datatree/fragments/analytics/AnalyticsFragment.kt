package com.datatree.fragments.analytics

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.datatree.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.datatree.core.esp32interfaces.Esp32Listener
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.datatree.core.utils.Esp32Manager
import com.datatree.core.utils.connectToArduino
import com.datatree.infraestructure.dataclass.esp32.SensorData
import kotlin.random.Random


class AnalyticsFragment : Fragment(), Esp32Listener {
    // UI Components
    private lateinit var tvTemperature: TextView
    private lateinit var tvTemperatureStatus: TextView
    private lateinit var tvAirHumidity: TextView
    private lateinit var tvAirHumidityStatus: TextView
    private lateinit var tvSoilHumidity: TextView
    private lateinit var tvSoilHumidityStatus: TextView
    private lateinit var tvValidReadings: TextView
    private lateinit var tvFailedReadings: TextView
    private lateinit var tvErrorPercent: TextView
    private lateinit var tvConnectionStatus: TextView
    private lateinit var progressSoil: LinearProgressIndicator
    private lateinit var btnStartMonitoring: Button
    private lateinit var btnStopMonitoring: Button
    private lateinit var btnRefresh: View

    // Monitoring variables
    private var isMonitoring = false
    private var validReadings = 0
    private var failedReadings = 0
    private val handler = Handler(Looper.getMainLooper())
    private var monitoringRunnable: Runnable? = null

    // Sensor data simulation
    private var currentTemperature = 0f
    private var currentAirHumidity = 0f
    private var currentSoilHumidity = 0

    @RequiresApi(Build.VERSION_CODES.S)
    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION // Necesario en algunos casos
    )
    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (granted) {
            esp32Manager.connectToArduino(requireContext(), "ESP32-DataTree")
        } else {
            showError("Se requieren permisos de Bluetooth para continuar.")
        }
    }
    companion object {
        fun newInstance() = AnalyticsFragment()
    }



    private lateinit var viewModel: AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }


    private fun initializeViews(view: View) {
        tvTemperature = view.findViewById(R.id.tv_temperature_value)
        tvTemperatureStatus = view.findViewById(R.id.tv_temperature_status)
        tvAirHumidity = view.findViewById(R.id.tv_air_humidity_value)
        tvAirHumidityStatus = view.findViewById(R.id.tv_air_humidity_status)
        tvSoilHumidity = view.findViewById(R.id.tv_soil_humidity_value)
        tvSoilHumidityStatus = view.findViewById(R.id.tv_soil_humidity_status)
        tvValidReadings = view.findViewById(R.id.tv_valid_readings)
        tvFailedReadings = view.findViewById(R.id.tv_failed_readings)
        tvErrorPercent = view.findViewById(R.id.tv_error_percent)
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status)
        progressSoil = view.findViewById(R.id.progress_soil)
        btnStartMonitoring = view.findViewById(R.id.btn_start_monitoring)
        btnStopMonitoring = view.findViewById(R.id.btn_stop_monitoring)
        btnRefresh = view.findViewById(R.id.btn_refresh)
    }
    private fun setupListeners_data_simulation() {
        btnStartMonitoring.setOnClickListener { startMonitoring() }
        btnStopMonitoring.setOnClickListener { stopMonitoring() }
        btnRefresh.setOnClickListener { refreshData() }
    }

    private fun setupListeners() {
        btnStartMonitoring.setOnClickListener {
            esp32Manager.requestData() // envia "DATA\n"
        }

        btnStopMonitoring.setOnClickListener {
            esp32Manager.stopMonitoring()
        }

        btnRefresh.setOnClickListener {
            esp32Manager.requestData()
        }
    }

    private fun startMonitoring() {
        if (isMonitoring) return

        isMonitoring = true
        validReadings = 0
        failedReadings = 0

        btnStartMonitoring.isEnabled = false
        btnStopMonitoring.isEnabled = true
        tvConnectionStatus.text = "Monitoreando..."

        monitoringRunnable = object : Runnable {
            private var cycleCount = 0
            private val maxCycles = 20

            override fun run() {
                if (cycleCount < maxCycles && isMonitoring) {
                    simulateArduinoReading()
                    cycleCount++
                    handler.postDelayed(this, 1000)
                } else {
                    stopMonitoring()
                }
            }
        }

        handler.post(monitoringRunnable!!)
    }

    private fun stopMonitoring() {
        isMonitoring = false
        monitoringRunnable?.let { handler.removeCallbacks(it) }

        btnStartMonitoring.isEnabled = true
        btnStopMonitoring.isEnabled = false
        tvConnectionStatus.text = "Sistemas conectados"

        updateUI()
    }


    private fun simulateArduinoReading() {
        val readingSuccess = Random.nextFloat() > 0.1f
        if (readingSuccess) {
            currentTemperature = Random.nextFloat() * 1f + 1f
            currentAirHumidity = Random.nextFloat() * 4f + 4f
            currentSoilHumidity = Random.nextInt(150, 350)
            validReadings++
        } else {
            failedReadings++
        }
        updateUI()
    }

    private fun refreshData() {
        simulateArduinoReading()
    }

    private fun updateUI() {
        tvTemperature.text = String.format("%.1f°C", currentTemperature)
        tvAirHumidity.text = String.format("%.0f%%", currentAirHumidity)
        tvSoilHumidity.text = currentSoilHumidity.toString()

        tvValidReadings.text = validReadings.toString()
        tvFailedReadings.text = failedReadings.toString()

        val totalReadings = validReadings + failedReadings
        val errorPercent = if (totalReadings > 0) {
            (failedReadings.toFloat() / totalReadings * 100)
        } else 0f
        tvErrorPercent.text = String.format("%.1f%%", errorPercent)

        val soilProgress = ((4095 - currentSoilHumidity) / 4095f * 100).toInt()
        progressSoil.progress = soilProgress.coerceIn(0, 100)

        updateTemperatureStatus()
        updateHumidityStatus()
        updateSoilStatus()
    }

    private fun updateTemperatureStatus() {
        tvTemperatureStatus.setTemperatureStatus(currentTemperature)
    }

    private fun updateHumidityStatus() {
        tvAirHumidityStatus.setHumidityStatus(currentAirHumidity)
    }

    private fun updateSoilStatus() {
        tvSoilHumidityStatus.setSoilStatus(currentSoilHumidity)
    }


    //extensiones de uso para actualizar datos.

    fun TextView.setTemperatureStatus(temperature: Float) {
        val (textStr, color, bg) = when {
            temperature < 15f -> Triple("Frío", R.color.white, R.drawable.status_background_warning)
            temperature > 30f -> Triple("Alto", R.color.white, R.drawable.status_background_warning)
            else -> Triple("Óptimo", R.color.white, R.drawable.status_background_good)
        }
        text = textStr
        setTextColor(context.getColor(color))
        setBackgroundResource(bg)
    }

    fun TextView.setHumidityStatus(humidity: Float) {
        val (textStr, color, bg) = when {
            humidity < 40f -> Triple("Bajo", R.color.white, R.drawable.status_background_warning)
            humidity > 80f -> Triple("Alto", R.color.white, R.drawable.status_background_warning)
            else -> Triple("Normal", R.color.white, R.drawable.status_background_normal)
        }
        text = textStr
        setTextColor(context.getColor(color))
        setBackgroundResource(bg)
    }

    fun TextView.setSoilStatus(value: Int) {
        val (textStr, color, bg) = when {
            value > 3000 -> Triple("Seco", R.color.white, R.drawable.status_background_warning)
            value > 2000 -> Triple("Bajo", R.color.white, R.drawable.status_background_normal)
            else -> Triple("Bueno", R.color.white, R.drawable.status_background_good)
        }
        text = textStr
        setTextColor(context.getColor(color))
        setBackgroundResource(bg)
    }

    //se ejecuta cuando el view se ha creado
    private lateinit var esp32Manager: Esp32Manager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        esp32Manager = Esp32Manager(requireContext())
        esp32Manager.setListener(this)

        if (esp32Manager.initialize()) {
            checkAndRequestPermissions()
        } else {
            showError("Bluetooth no disponible en este dispositivo.")
        }



        initializeViews(view)
        setupListeners()
        updateUI()
    }
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val notGranted = bluetoothPermissions.any {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGranted) {
                permissionRequestLauncher.launch(bluetoothPermissions)
            } else {
                esp32Manager.connectToArduino(requireContext(), "ESP32-DataTree")
            }
        } else {
            esp32Manager.connectToArduino(requireContext(), "ESP32-DataTree")
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AnalyticsViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onDestroyView() {
        super.onDestroyView()
        stopMonitoring()
        esp32Manager.cleanup()
    }

    override fun onDataReceived(data: SensorData) {
        currentTemperature = data.temperature
        currentAirHumidity = data.airHumidity
        currentSoilHumidity = data.soilHumidity
        validReadings = data.validReadings
        failedReadings = data.failedReadings

        updateUI()
    }

    override fun onConnectionStatusChanged(isConnected: Boolean) {
        tvConnectionStatus.text = if (isConnected) "Sistemas conectados" else "Sin conexión"
    }

    override fun onError(error: String) {
        showError(error)
    }
    private fun showError(msg: String) {
        Log.e("AnalyticsFragment", msg)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }



}