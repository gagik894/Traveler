package com.together.traveler.ui.admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.together.traveler.model.Place
import com.together.traveler.retrofit.ApiClient
import com.together.traveler.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminViewModel : ViewModel() {
    private val TAG = "AdminViewModel"
    private val apiService: ApiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
    private val _placesData: MutableLiveData<List<Place>?> = MutableLiveData()
    private val _selectedPlaceData: MutableLiveData<Place> = MutableLiveData()
    private val _placeCategories: MutableLiveData<List<String>?> = MutableLiveData()
    private val _eventCategories: MutableLiveData<List<String>?> = MutableLiveData()

    val placesData: MutableLiveData<List<Place>?> = _placesData
    val selectedPlaceData: MutableLiveData<Place> = _selectedPlaceData
    val placeCategories: MutableLiveData<List<String>?> = _placeCategories
    val eventCategories: MutableLiveData<List<String>?> = _eventCategories

    val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        fetchPlace()
        fetchCategories()
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
    private fun fetchCategories(){
        apiService.getCategories("events").enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful)
                    _eventCategories.postValue(response.body())
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

        })
        apiService.getCategories("places").enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                Log.d("Admin", "onResponse: " + response.body())
                if (response.isSuccessful)
                    _placeCategories.postValue(response.body())
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

        })
    }

    fun deleteCategory(name:String, type: String){
        if (type == "event"){
            fetchDeleteCategories("events", name)
            val updatedList = _eventCategories.value?.filter { it != name }
            _eventCategories.value = updatedList
//            deletedEventCategories.add(name)
        }else{
            fetchDeleteCategories("places", name)
            val updatedList = _placeCategories.value?.filter { it != name }
            _placeCategories.value = updatedList
//            deletedPlaceCategories.add(name)
        }
    }

    fun addCategory(name:String, type: String){
        if (type == "event"){
            fetchAddCategories("events", name)
            val updatedList = _eventCategories.value?.plus(name)
            _eventCategories.value = updatedList
//            addedEventCategories.add(name)
        }else{
            fetchAddCategories("places", name)
            val updatedList = _placeCategories.value?.plus(name)
            _placeCategories.value = updatedList
//            addedPlaceCategories.add(name)
        }
    }

//    fun submitCategories(type: String){
//        if (type == "event"){
//            if (addedEventCategories.size>0){
//                fetchAddCategories("events")
//            }
//            if (deletedEventCategories.size>0){
//                fetchDeleteCategories("events")
//            }
//        }else{
//            if (addedPlaceCategories.size>0){
//                fetchAddCategories("places")
//            }
//            if (deletedPlaceCategories.size>0){
//                Log.i(TAG, "submitCategories: $deletedPlaceCategories")
//                fetchDeleteCategories("places")
//            }
//        }
//    }

    fun verifyPlace(place: Place){
        val json = JSONObject()
        try {
            json.put("name", place.name)
            json.put("description", place.description)
            json.put("url", place.url)
            json.put("phone", place.phone)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.i(TAG, "verifyPlace: ")
        apiService.verifyAdminPlace(place._id, requestBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Admin", "onResponse: " + response.body())
                if(response.isSuccessful) {
                    val updatedList = _placesData.value?.filter { it._id != place._id }
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

    private fun fetchAddCategories(type: String, category: String){
        val json = JSONObject()
        try {
            json.put("categories", arrayOf(category).contentToString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i("admin", "fetchAddCategories: " + json.toString())
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        loading.value = true
        apiService.addAdminCategories(type, requestBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Admin", "onResponse: " + response.body())
                if (response.isSuccessful){
                    fetchCategories()
                }
                loading.value = false
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                loading.value = false
            }

        })
    }
    private fun fetchDeleteCategories(type: String, category: String){
        val json = JSONObject()
        try {
            json.put("categories", arrayOf(category).contentToString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        loading.value = true
        apiService.deleteAdminCategories(type, requestBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Admin", "onResponse: " + response.body())
                if (response.isSuccessful){
                    fetchCategories()
                }
                loading.value = false
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                loading.value = false
            }
        })
    }
}

