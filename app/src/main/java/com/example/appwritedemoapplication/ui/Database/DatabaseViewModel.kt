package com.example.appwritedemoapplication.ui.Database

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appwritedemoapplication.utils.Client.client
import com.example.appwritedemoapplication.utils.Event
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.AccountService
import io.appwrite.services.DatabaseService
import kotlinx.coroutines.launch
import org.json.JSONObject

class DatabaseViewModel : ViewModel() {

    private val _error = MutableLiveData<Event<AppwriteException>>().apply {
        value = null
    }
    val error: LiveData<Event<AppwriteException>> = _error

    private val _response = MutableLiveData<Event<String>>().apply {
        value = null
    }
    val response: LiveData<Event<String>> = _response

    private val databaseService by lazy {
        DatabaseService(client)
    }

    private val COLLECTION_ID = "60632e9bb9631"


    fun createDocument(content: Editable? , isComplete: Boolean) {
        val data = mapOf(
                "content" to content.toString(),
                "isComplete" to isComplete
        )
        val read = listOf("*")
        viewModelScope.launch {
            try {
                val response = databaseService.createDocument(COLLECTION_ID, data, read, read)
                var json = response.body?.string()
                json = JSONObject(json).toString(8)
                _response.postValue(Event(json))
            } catch (e: AppwriteException) {
                _error.postValue(Event(e))
            }
        }
    }

    fun getDocuments() {
        viewModelScope.launch {
            try {
                var response = databaseService.listDocuments(COLLECTION_ID)
                var json = response.body?.string() ?: ""
                json = JSONObject(json).toString(8)
                _response.postValue(Event(json))
            } catch (e: AppwriteException) {
                _error.postValue(Event(e))
            }
        }
    }

    fun getDocument(id: Editable?) {
        viewModelScope.launch {
            try {
                var response = databaseService.getDocument(COLLECTION_ID, id.toString())
                var json = response.body?.string() ?: ""
                json = JSONObject(json).toString(8)
                _response.postValue(Event(json))
            } catch (e: AppwriteException) {
                _error.postValue(Event(e))
            }
        }
    }

    fun deleteDocument(id: Editable?) {
        viewModelScope.launch {
            try {
                var response = databaseService.deleteDocument(COLLECTION_ID, id.toString())
                var json = response.body?.string() ?: ""
                json = JSONObject(json).toString(8)
                _response.postValue(Event(json))
            } catch (e: AppwriteException) {
                _error.postValue(Event(e))
            }
        }
    }
}