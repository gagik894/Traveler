package com.together.traveler.ui.chat.chatsListScreen

import androidx.lifecycle.ViewModel
import com.together.traveler.model.ChatInfo
import com.together.traveler.retrofit.ApiClient
import com.together.traveler.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatsViewModel : ViewModel() {

    private val apiService: ApiService =
        ApiClient.getRetrofitInstance().create(ApiService::class.java)

    // Expose screen UI state
    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ChatsUiState(isLoading = true)
    }

    // Handle business logic
    fun fetchData() {
        apiService.chats.enqueue(object : Callback<List<ChatInfo>> {
            override fun onResponse(
                call: Call<List<ChatInfo>>,
                response: Response<List<ChatInfo>>
            ) {
                if (response.isSuccessful) {
                    val chats = response.body() ?: emptyList()
                    _uiState.value = ChatsUiState(data = chats, isLoading = false)
                } else {
                    // Handle error
                    _uiState.value = ChatsUiState(isLoading = false)
                }
            }

            override fun onFailure(call: Call<List<ChatInfo>>, t: Throwable) {
                // Handle error
                _uiState.value = ChatsUiState(isLoading = false)
            }
        })
    }
}

data class ChatsUiState(
    val data: List<ChatInfo> = emptyList(),
    val isLoading: Boolean = true
)
