package com.together.traveler.ui.event.scan;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.model.CheckTicketResponse;
import com.together.traveler.requests.WebRequests;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ScanViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading;
    private String _id;
    private final WebRequests webRequests;

    public ScanViewModel() {
        loading = new MutableLiveData<>(false);
        _id ="";
        webRequests = new WebRequests();
    }

    public void set_id(String _id){
        Log.i("asd", "set_id: " + _id);
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public CheckTicketResponse checkTicket(String userId){
        JSONObject json = new JSONObject();
        String result;
        RequestBody requestBody;
        try {
            json.put("userId", userId);
            json.put("eventId", this.get_id());
            loading.setValue(true);
            requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());
            result = webRequests.makeHttpPostRequest("https://traveler-ynga.onrender.com/events/checkTicket", requestBody);
        } catch (JSONException e) {
//            loading.setValue(false);
            e.printStackTrace();
            return new CheckTicketResponse(false);
        }
        loading.setValue(false);
        Gson gson = new Gson();
        return gson.fromJson(result, CheckTicketResponse.class);
    }
}