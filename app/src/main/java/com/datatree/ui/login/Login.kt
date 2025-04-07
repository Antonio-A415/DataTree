package com.datatree.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.datatree.MainActivity
import com.datatree.databinding.ActivityLoginBinding
import com.datatree.core.repositories.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.datatree.R


@AndroidEntryPoint
class Login : AppCompatActivity() {

    private lateinit var views: ActivityLoginBinding
    @Inject lateinit var mainRepository: MainRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
        val windowsActual= window  //referencia a la ventana
        windowsActual.statusBarColor= ContextCompat.getColor(this, R.color.green_more_strong)
    }

    private fun makeToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun init(){

        views.apply {

            btnLogin.setOnClickListener {

                mainRepository.login(
                    etName.text.toString(), etPassword.text.toString()
                ){ isDone, reason ->
                    makeToast("Ejecutando la función lambda")
                    if (!isDone){
                        Toast.makeText(this@Login, reason, Toast.LENGTH_SHORT).show()
                    }else{
                        makeToast("Iniciando la siguiente actividad.")
                        //start moving to our main activity
                       /* Intent(this@Login, HomeView::class.java).apply {
                            //putExtra("username", etName.text.toString())
                        }*/
                        val intent = Intent(this@Login, MainActivity::class.java)

                        intent.putExtra("username", etName.text.toString())

                        startActivity(intent)
                       /* startActivity(Intent(this@Login, MainActivity::class.java).apply {
                            putExtra("username", etName.text.toString())
                        })
                        */
                    }
                }
            }
        }
    }
}