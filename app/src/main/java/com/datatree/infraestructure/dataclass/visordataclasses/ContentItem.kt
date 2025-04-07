package com.datatree.infraestructure.dataclass.visordataclasses

data class ContentItem(
    val type: String,
    val text: String ?=null,
    val image_url : ImageUrl?=null
)
