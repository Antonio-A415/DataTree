package com.datatree.activitys.informationplanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.datatree.R
import com.datatree.databinding.ActivityPlantaBinding
class PlantaActivity : AppCompatActivity() {

    // This activity is used to display information about plant

    //enlace con viewbinding
    private lateinit var binding: ActivityPlantaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planta)
    }
}