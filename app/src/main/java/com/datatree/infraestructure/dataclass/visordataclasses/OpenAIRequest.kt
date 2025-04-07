package com.datatree.infraestructure.dataclass.visordataclasses

import com.datatree.infraestructure.dataclass.visordataclasses.Message

data class OpenAIRequest(
    val model: String,
    val messages : List<Message>,
    val max_tokens : Int
)
