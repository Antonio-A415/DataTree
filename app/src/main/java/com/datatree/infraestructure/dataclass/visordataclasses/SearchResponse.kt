package com.datatree.infraestructure.dataclass.visordataclasses

data class SearchResponse(
    val id: String,
    val model: String,
    val created: Long,
    val `object`: String,
    val output: String
)
