package com.datatree.infraestructure.dataclass.esp32

data class SensorData(
    val temperature: Float,
    val airHumidity: Float,
    val soilHumidity: Int,
    val validReadings: Int,
    val failedReadings: Int,
    val errorPercent: Float,
    val timestamp: Long = System.currentTimeMillis()
)