package com.datatree.infraestructure.dataclass

data class Itemsplants(
    var viewtype: Int,
    var nombre: String,
    var nombre2: String?,          // nombre científico, null si header
    var img: Int,
    var urlImagenes: MutableList<String>,
    var plantId: Long
) {

    // Constructor para headers (3 parámetros)
    constructor(viewtype: Int, nombre: String, img: Int) :
            this(viewtype, nombre, null, img, mutableListOf(), 0L)

    // Constructor para plantas (5 parámetros)
    constructor(viewtype: Int, nombre: String, nombre2: String?, img: Int, urlImagenes: List<String>?) :
            this(viewtype, nombre, nombre2, img, urlImagenes?.toMutableList() ?: mutableListOf(), 0L)

    // Métodos de utilidad
    fun hasImages(): Boolean = urlImagenes.isNotEmpty()
    fun getImageCount(): Int = urlImagenes.size
    fun getPrimaryImageUrl(): String? = urlImagenes.firstOrNull()
    fun addImageUrl(imageUrl: String?) {
        if (!imageUrl.isNullOrBlank()) urlImagenes.add(imageUrl)
    }

    override fun toString(): String {
        return "Itemsplants(viewtype=$viewtype, nombre='$nombre', nombre2=$nombre2, plantId=$plantId, imageCount=${getImageCount()})"
    }
}
