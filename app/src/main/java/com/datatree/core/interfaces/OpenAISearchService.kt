package com.datatree.core.interfaces

import com.datatree.infraestructure.dataclass.visordataclasses.SearchRequest
import com.datatree.infraestructure.dataclass.visordataclasses.SearchResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAISearchService {
    @POST("v1/answers")
    suspend fun searchWeb(
        @Header("Authorization") authorization: String,
        @Body request: SearchRequest
    ): SearchResponse
}
