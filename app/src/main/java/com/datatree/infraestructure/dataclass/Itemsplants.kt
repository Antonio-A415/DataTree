package com.datatree.infraestructure.dataclass


class Itemsplants {

    var nombre2: String? =null
    var nombre: String
    var img: Int = 0
    var viewtype : Int

    constructor(viewType:Int, nombre: String, imgilus:Int) {
        this.viewtype=viewType
        this.nombre = nombre
        this.img= imgilus
    }
    // Constructor secundario
    constructor(viewType: Int, nombre: String,  nombre2:String,  img: Int) {
        this.viewtype=viewType
        this.nombre = nombre
        this.img = img
        this.nombre2 = nombre2
    }




    // Otro constructor secundario con un valor por defecto






}
data class Resultslands(var key:String, var value:String)