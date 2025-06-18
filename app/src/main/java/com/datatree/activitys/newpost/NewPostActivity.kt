package com.datatree.activitys.newpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.datatree.R
import com.datatree.databinding.ActivityNewPostBinding
import java.util.Date

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewPostBinding

    //variables para crear un nuevo dataclass AlertPost
    private val idUser: Int = 0
    private lateinit var user : String
    private lateinit var titulo : String
    private lateinit var description : String
    private lateinit var region : String
    private lateinit var images : List<String>
    private lateinit var createdAt : Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}