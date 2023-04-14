package com.together.traveler.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.model.EventsResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    Context context = AppContext.getContext();
    private final SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    private final MutableLiveData<ArrayList<Event>> data;

    public HomeViewModel() {
        data = new MutableLiveData<>();
        makeHttpRequest();
//        createEventCardList(100);
    }

    public void setData(ArrayList<Event> data) {
        this.data.setValue(data);
    }

    private void createEventCardList(int n){

    }

    public LiveData<ArrayList<Event>> getData() {
        return data;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public void makeHttpRequest() {
        Future<String> future = executor.submit(() -> {
            try {
                URL url = new URL("https://traveler-ynga.onrender.com/events");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String authToken = sharedPreferences.getString("auth_token", null);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("auth-token", authToken);
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });


        try {
            String result = future.get();
            if (result != null) {
                Log.i("asd", "makeHttpRequest: " + result);
                Gson gson = new Gson();
                EventsResponse response = gson.fromJson(result, EventsResponse.class);
                List<Event> events = response.getData();
                this.data.postValue((ArrayList<Event>) events);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("asd", "makeHttpRequest: ", e);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

}
