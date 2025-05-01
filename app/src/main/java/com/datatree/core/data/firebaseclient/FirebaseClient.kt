package com.datatree.core.data.firebaseclient

import android.content.ContentValues.TAG
import android.util.Log
import com.datatree.core.utils.DataModel
import com.datatree.core.utils.FirebaseFieldNames.LATEST_EVENT
import com.datatree.core.utils.FirebaseFieldNames.PASSWORD
import com.datatree.core.utils.FirebaseFieldNames.STATUS
import com.datatree.core.utils.MyEventListener
import com.datatree.core.utils.UserStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val dbRef: DatabaseReference,
    private val gson: Gson
) {
    //Cuerpo de la clase

    private var currentUsername:String?=null

    private fun setUsername(username: String){
        this.currentUsername = username
    }

    fun login(username: String, password: String, done: (Boolean, String?) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object  : MyEventListener(){
            override fun onDataChange(snapshot: DataSnapshot) {
                //if the current user exists
                if (snapshot.hasChild(username)){
                    //user exists , its time to check the password
                    val dbPassword = snapshot.child(username).child(PASSWORD).value
                    if (password == dbPassword) {
                        //password is correct and sign in
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUsername(username)
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,"${it.message}")
                            }
                    }else{
                        //password is wrong, notify user
                        done(false,"La contraseña es incorrecta. Intente de nuevo.")
                    }

                }else{
                    //en caso contrario, crear al usuario con los camops.
                    dbRef.child(username).child(PASSWORD).setValue(password).addOnCompleteListener {
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUsername(username)
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,it.message)
                            }
                    }.addOnFailureListener {
                        done(false,it.message)
                    }

                }
            }
        })
    }


    //Observador de estados del usuario
    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        dbRef.addValueEventListener(object : MyEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.filter { it.key !=currentUsername }.map {
                    it.key!! to it.child(STATUS).value.toString()
                }
                status(list)
            }
        })
    }

    fun subscribeForLatestEvent(listener: Listener) {
        currentUsername?.let { username ->
            dbRef.child(username).child(LATEST_EVENT).addValueEventListener(
                object : MyEventListener() {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val event = gson.fromJson(snapshot.value.toString(), DataModel::class.java)
                            event?.let {
                                listener.onLatestEventReceived(it)
                            } ?: run {
                                Log.w(TAG, "Received null event from Firebase")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing latest event", e)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Firebase event cancelled: ${error.message}")
                    }
                }
            )
        } ?: run {
            Log.e(TAG, "Attempted to subscribe for latest event with null username")
        }
    }
    /*
    fun subscribeForLatestEvent(listener: Listener){
        try{
                dbRef.child(currentUsername !! ).child(LATEST_EVENT).addValueEventListener(
                    object : MyEventListener() {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            super.onDataChange(snapshot)

                            val event=try{
                                gson.fromJson(snapshot.value.toString(), DataModel::class.java)
                            }catch (e: Exception){

                            }
                        }

                    })
        }catch ( e: Exception){
        e.printStackTrace()
        }
    }*/


    fun sendMessageToOtherClient(message:DataModel, success:(Boolean) -> Unit){
        val convertedMessage = gson.toJson(message.copy(sender = currentUsername))
        dbRef.child(message.target).child(LATEST_EVENT).setValue(convertedMessage)
            .addOnCompleteListener {
                success(true)
            }.addOnFailureListener {
                success(false)
            }
    }

    fun changeMyStatus(status: UserStatus) {
        dbRef.child(currentUsername!!).child(STATUS).setValue(status.name)
    }

    fun clearLatestEvent() {
        dbRef.child(currentUsername!!).child(LATEST_EVENT).setValue(null)
    }

    fun logOff(function:()->Unit) {
        dbRef.child(currentUsername!!).child(STATUS).setValue(UserStatus.OFFLINE)
            .addOnCompleteListener { function() }
    }

    interface Listener {
        fun onLatestEventReceived(event:DataModel)
    }
}