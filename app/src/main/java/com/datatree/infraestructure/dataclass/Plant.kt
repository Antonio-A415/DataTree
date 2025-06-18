package com.datatree.infraestructure.dataclass

data class Plant(
    val nombre: String,
    val nombreCientifico: String,
    val tipoCultivo: String,
    val necesidadesRiego: String,
    val exposicionSolar: String,
    val tipoSuelo: String,
    val tiempoGerminacion: String,
    val tiempoCosecha: String,
    val distanciaSiembra: String,
    val imagenUrl: String, // Imagen desde URL
    val imagenId : Int?
)


