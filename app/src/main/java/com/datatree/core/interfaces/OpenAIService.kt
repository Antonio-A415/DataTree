package com.datatree.core.interfaces

import com.datatree.infraestructure.dataclass.visordataclasses.OpenAIRequest
import com.datatree.infraestructure.dataclass.visordataclasses.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface OpenAIService {

    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") authorization: String,
        @Body request : OpenAIRequest

    ) : OpenAIResponse

}