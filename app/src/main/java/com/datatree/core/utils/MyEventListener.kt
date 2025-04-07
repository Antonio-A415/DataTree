package com.datatree.core.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

open class MyEventListener : ValueEventListener {

    override fun onCancelled(error: DatabaseError) {
        //Sin implementar
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        //Sin implementar
    }
}