package com.together.traveler.ui.admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.together.traveler.model.Place
import com.together.traveler.web.ApiClient
import com.together.traveler.web.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminViewModel : ViewModel() {
    private val TAG = "AdminViewModel"
    private val apiService: ApiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
    private val _placesData: MutableLiveData<List<Place>?> = MutableLiveData()
    private val _selectedPlaceData: MutableLiveData<Place> = MutableLiveData()

    val placesData: MutableLiveData<List<Place>?> = _placesData
    val selectedPlaceData: MutableLiveData<Place> = _selectedPlaceData
        get() {
            Log.d("admin", ": getter")
            return field
        }

    init {
        fetchPlace()
    }

    fun setSelectedPlaceData(place: Place){
        _selectedPlaceData.value = place
    }

    private fun fetchPlace(){
        apiService.adminPlaces.enqueue(object : Callback<List<Place>> {
            override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                Log.d("Admin", "onResponse: " + response.body())
                _placesData.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

        })
    }
    fun verifyPlace(id: String){
        Log.i(TAG, "verifyPlace: ")
        apiService.verifyAdminPlace(id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Admin", "onResponse: " + response.body())
                if(response.isSuccessful) {
                    val updatedList = _placesData.value?.filter { it._id != id }
                    _placesData.value = updatedList
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

        })
    }
    fun deletePlace(id: String){
        apiService.deleteAdminPlace(id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Admin", "onResponse: " + response.body())
                if(response.isSuccessful) {
                    val updatedList = _placesData.value?.filter { it._id != id }
                    _placesData.value = updatedList
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

        })
    }
}

