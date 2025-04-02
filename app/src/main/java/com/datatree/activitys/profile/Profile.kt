package com.datatree.activitys.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.datatree.R
import com.datatree.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {


    private lateinit var binding : ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //setContentView(R.layout.activity_profile)
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}