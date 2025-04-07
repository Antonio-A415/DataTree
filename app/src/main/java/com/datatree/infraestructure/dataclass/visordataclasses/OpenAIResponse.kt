package com.datatree.infraestructure.dataclass.visordataclasses

import java.util.Objects

data class OpenAIResponse(
    val id: String,
    val objects: String,
    val created : Long,
    val model: String,
    val choices : List<Choice>

)
