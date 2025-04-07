package com.datatree.infraestructure.dataclass.visordataclasses

data class SearchRequest(
    val model: String,
    val tools: List<Tool>,
    val input: String
)
