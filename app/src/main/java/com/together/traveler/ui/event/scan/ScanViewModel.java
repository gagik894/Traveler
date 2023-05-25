package com.together.traveler.ui.event.scan;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.CheckTicketResponse;
import com.together.traveler.web.ApiClient;
import com.together.traveler.web.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanViewModel extends ViewModel {
    private final String TAG = "ScanViewModel";
    private final MutableLiveData<CheckTicketResponse> checkTicketResponse;

    private String _id;
    private final ApiService apiService;

    public ScanViewModel() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        checkTicketResponse = new MutableLiveData<>();
        _id = "";
    }

    public void set_id(String _id) {
        Log.i("asd", "set_id: " + _id);
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public MutableLiveData<CheckTicketResponse> getCheckTicketResponse() {
        return checkTicketResponse;
    }

    public void checkTicket(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("eventId", this.get_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        apiService.checkTicket(requestBody).enqueue(new Callback<CheckTicketResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckTicketResponse> call, @NonNull Response<CheckTicketResponse> response) {
                if (response.isSuccessful()) {
                    CheckTicketResponse checkTicket = response.body();
                    checkTicketResponse.postValue(checkTicket);
                } else {
                    Log.e(TAG, "checkTicket request failed with code: " + response.code());
                    checkTicketResponse.postValue(new CheckTicketResponse(false));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckTicketResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "checkTicket request failed", t);
                checkTicketResponse.postValue(new CheckTicketResponse(false));
            }
        });
    }

}