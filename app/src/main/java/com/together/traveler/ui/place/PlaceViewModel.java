package com.together.traveler.ui.place;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Place;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");

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
        if (this.placeData.getValue().isAlwaysOpen()){
            return true;
        }
        String[] openingTimes = new String[7];
        String[] closingTimes = new String[7];
        if (place != null) {
            openingTimes = place.getOpeningTimes();
            closingTimes = place.getClosingTimes();
        }

        LocalTime currentTime = LocalTime.now();
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        dayIndex = currentDayOfWeek.getValue() - 1;

        if (openingTimes[dayIndex] == null || closingTimes[dayIndex] == null) {
            return false;
        }

        LocalTime openingTime = LocalTime.parse(openingTimes[dayIndex], formatter);
        LocalTime closingTime = LocalTime.parse(closingTimes[dayIndex], formatter);

        return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime);
    }

    public String getNextTime() {
        Place place = this.placeData.getValue();
        if (place == null || place.isAlwaysOpen() || place.getIsClosedDays()[dayIndex]) {
            return null;
        }
        if (this.isOpen()) {
            return place.getClosingTimes()[dayIndex];
        }
        LocalTime currentTime = LocalTime.now();
        LocalTime openingTime = LocalTime.parse(place.getOpeningTimes()[dayIndex], formatter);
        if (currentTime.isBefore(openingTime)) {
            return place.getOpeningTimes()[dayIndex];
        }
        int index = (dayIndex + 1) % 7;
        if (!place.getIsClosedDays()[index]){
            return "Tomorrow";
        }
        return null;
    }

}