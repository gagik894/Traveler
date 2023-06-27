package com.together.traveler.ui.chat.singleChatScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.together.traveler.model.Chat
import com.together.traveler.model.Message
import com.together.traveler.retrofit.ApiClient
import com.together.traveler.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class ChatViewModel : ViewModel() {

    private val apiService: ApiService =
        ApiClient.getRetrofitInstance().create(ApiService::class.java)

    // Expose screen UI state
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun addMessage(message: Message) {
        val currentData = _uiState.value.data
        val updatedChat = currentData.chat.toMutableList().apply {
            add(0, message)
        }
        val updatedData = currentData.copy(chat = updatedChat)
        _uiState.value = _uiState.value.copy(data = updatedData)
    }

    fun addMessageToDb(content: String) {
        val message = Message(content = content, timestamp = Date(), userId = uiState.value.data.user)
        val json = JSONObject()
        try {
            json.put("content", message.content)
            json.put("timestamp", message.timestamp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i("admin", "fetchAddCategories: " + json.toString())
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        apiService.addMessage(uiState.value.data._id, requestBody).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.i("asd", "onResponse: ")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("asd", "onFailure: ", t)
            }

        })
    }

    // Handle business logic
    fun fetchData(id: String) {
        _uiState.value = ChatUiState(isLoading = true)
        apiService.getChat(id).enqueue(object : Callback<Chat> {
            override fun onResponse(
                call: Call<Chat>,
                response: Response<Chat>
            ) {
                if (response.isSuccessful) {
                    val chat = response.body()
                    if (chat!=null){
                        _uiState.value = ChatUiState(data = chat, isLoading = false)
                    }else{
                        _uiState.value = ChatUiState(isLoading = false)
                    }
                } else {
                    // Handle error
                    _uiState.value = ChatUiState(isLoading = false)
                }
            }

            override fun onFailure(call: Call<Chat>, t: Throwable) {
                // Handle error
                _uiState.value = ChatUiState(isLoading = false)
            }
        })
    }
}

data class ChatUiState(
    val data: Chat = Chat(),
    val isLoading: Boolean = true
)