package com.together.traveler.ui.place;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Place;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PlaceViewModel extends ViewModel {
    private final String TAG = "PlaceViewModel";

    private final MutableLiveData<Place> placeData;
    private String userId;
    private final ApiService apiService;
    private int dayIndex;

    public PlaceViewModel() {
        placeData = new MutableLiveData<>();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public void setPlaceData(Place placeData) {
        Log.d(TAG, "setPlaceData: "+ placeData.getDescription());
        this.placeData.setValue(placeData);
    }

    public void setUserId(String userId) {
        Log.d(TAG, "setUserId: " + userId);
        this.userId = userId;
    }

    public MutableLiveData<Place> getPlaceData() {
        return placeData;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isOpen() {
        Place place = this.placeData.getValue();
        String[] openingTimes = new String[7];
        String[] closingTimes = new String[7];
        if (place != null) {
            openingTimes = place.getOpeningTimes();
            closingTimes = place.getClosingTimes();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");
        LocalTime currentTime = LocalTime.now();
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        int dayIndex = currentDayOfWeek.getValue() - 1;

        if (openingTimes[dayIndex] == null || closingTimes[dayIndex] == null) {
            return false;
        }

        LocalTime openingTime = LocalTime.parse(openingTimes[dayIndex], formatter);
        LocalTime closingTime = LocalTime.parse(closingTimes[dayIndex], formatter);

        return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime);


    }


    public String getNextTime() {
        Place place = this.placeData.getValue();
        if (place == null) {
            return null;
        }
        if (this.isOpen()) {
            return place.getClosingTimes()[dayIndex];
        }else{
            dayIndex = (dayIndex + 1) % 7;
            return place.getOpeningTimes()[dayIndex];
        }
    }
}