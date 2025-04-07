package com.datatree.infraestructure.dataclass.visordataclasses

data class Message(
    val role: String,
    val content: List<ContentItem>
)
