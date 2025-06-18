package com.datatree.infraestructure.dataclass

import android.graphics.Region
import java.util.Date

data class AlertPost(
    val id : Long = 0,
    val userId : String = "",
    val region: String ="",
    val title : String = "",
    val description : String ="",
    val images : String ="[]",
    val createdAt : Date = Date()

)
