package com.datatree.infraestructure.dataclass.visordataclasses

data class Choice(
    val index: Int,
    val message : ResponseMessage,
    val finishReason : String
)
