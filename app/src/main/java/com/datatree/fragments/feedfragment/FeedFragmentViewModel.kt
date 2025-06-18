package com.datatree.fragments.feedfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datatree.infraestructure.dataclass.AlertPost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONArray
import org.json.JSONException
import javax.inject.Inject
import com.google.android.material.tabs.TabLayoutMediator
import android.content.res.ColorStateList



@HiltViewModel
class FeedFragmentViewModel @Inject constructor(): ViewModel() {

    private val _alert = MutableLiveData<AlertPost>()
    val alert : LiveData<AlertPost> = _alert

    private val _isLiked = MutableLiveData(false)
    val isLiked : LiveData<Boolean> = _isLiked


    fun setAlert(alert: AlertPost) {
        _alert.value = alert
    }

    fun toggleLike() {
        _isLiked.value = _isLiked.value?.not()
    }

    fun parseImages(jsonImages: String): List<String> {
        val imageUrls = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonImages)
            for (i in 0 until jsonArray.length()) {
                imageUrls.add(jsonArray.getString(i))
            }
        } catch (e: JSONException) {
            imageUrls.add("drawable://default") // Placeholder
        }
        return imageUrls
    }
}