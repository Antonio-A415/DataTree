package com.datatree.core.esp32interfaces

import com.datatree.infraestructure.dataclass.esp32.SensorData

interface Esp32Listener {
    fun onDataReceived(data: SensorData)
    fun onConnectionStatusChanged(isConnected: Boolean)
    fun onError(error: String)
}