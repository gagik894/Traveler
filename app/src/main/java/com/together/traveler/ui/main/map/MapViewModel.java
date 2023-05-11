package com.together.traveler.ui.main.map;

import android.location.Address;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.together.traveler.model.Event;
import com.together.traveler.model.MapItem;
import com.together.traveler.model.Place;
import com.together.traveler.requests.ApiClient;
import com.together.traveler.requests.ApiService;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel {
    private final String TAG = "MapViewModel";

    public static final String MY_USER_AGENT = "Traveler";
    private final ApiService apiService;

    private final MutableLiveData<ArrayList<OverlayItem>> overlayItems;
    private final MutableLiveData<ArrayList<MapItem>> mapItems;
    private final MutableLiveData<ArrayList<String>> categories;
    private final MutableLiveData<String> search;
    private final MutableLiveData<GeoPoint> center;
    private final MutableLiveData<Integer> state;
    private final MutableLiveData<Place> mapSelectedPlaceData;
    private final MutableLiveData<Event> mapSelectedEventData;

    private final ArrayList<String> placeCategories;
    private final ArrayList<String> eventCategories;
    private final ArrayList<MapItem> events;
    private final ArrayList<MapItem> places;


    private String locationName;

    public MapViewModel() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        overlayItems = new MutableLiveData<>(new ArrayList<>());
        mapItems = new MutableLiveData<>(new ArrayList<>());
        categories = new MutableLiveData<>(new ArrayList<>());
        search = new MutableLiveData<>();
        center = new MutableLiveData<>();
        state = new MutableLiveData<>();
        mapSelectedPlaceData = new MutableLiveData<>();
        mapSelectedEventData = new MutableLiveData<>();

        placeCategories = new ArrayList<>();
        eventCategories = new ArrayList<>();
        events = new ArrayList<>();
        places = new ArrayList<>();
        setState(0);
    }

    public MutableLiveData<ArrayList<OverlayItem>> getOverlayItems() {
        return overlayItems;
    }

    public MutableLiveData<ArrayList<MapItem>> getMapItems() {
        return mapItems;
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public MutableLiveData<GeoPoint> getCenter() {return center;}

    public MutableLiveData<Integer> getState() {
        return state;
    }

    public MutableLiveData<Place> getMapSelectedPlaceData() {
        return mapSelectedPlaceData;
    }

    public MutableLiveData<Event> getMapSelectedEventData() {
        return mapSelectedEventData;
    }


    public void setMapSelectedEventData(int position){
        fetchEventData(events.get(position).get_id());
    }

    public void setMapSelectedPlaceData(int position){
        fetchPlaceData(places.get(position).get_id());
    }

    public void setSearch(String data) {
        this.search.postValue(data);
        getLocationFromName(data);
    }

    public void setCenter(GeoPoint center){
        this.center.setValue(center);
    }

    public void setState(Integer state){
        if (state == 0) {
            if (eventCategories.size() == 0){
                fetchMapItems("events");
                fetchCategories("events");
            }else{
                categories.setValue(eventCategories);
                mapItems.setValue(events);
            }
        }else{
            if (placeCategories.size() == 0){
                fetchMapItems("places");
                fetchCategories("places");
            }else{
                categories.setValue(placeCategories);
                mapItems.setValue(places);
            }
        }

        this.state.setValue(state);
    }

//    public void setMapSelectedPlace(int position){
//        this.mapSelectedPlace.setValue(Objects.requireNonNull(places.get(position));
//    }

    public void setItem(GeoPoint p){
        OverlayItem overlayItem = new OverlayItem(" ", "", new GeoPoint(p.getLatitude(), p.getLongitude()));
        Objects.requireNonNull(overlayItems.getValue()).clear();
        getNameFromLocation(p);
        overlayItems.getValue().add(overlayItem);
        overlayItems.postValue(overlayItems.getValue());
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    private void getLocationFromName(final String query) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            GeocoderNominatim geocoder = new GeocoderNominatim(MY_USER_AGENT);
            try {
                List<Address> addresses = geocoder.getFromLocationName(query, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();
                    center.postValue(new GeoPoint(latitude, longitude));
                    setItem(new GeoPoint(latitude, longitude));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getNameFromLocation( GeoPoint location) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            GeocoderNominatim geocoder = new GeocoderNominatim(MY_USER_AGENT);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String locationName =
                            address.getLocality()+
                                    (address.getThoroughfare() != null ? ", " + address.getThoroughfare() : "") +
                                    (address.getSubThoroughfare() != null ? ", " +address.getSubThoroughfare() : "");
                    this.center.postValue(new GeoPoint(location));
                    setLocationName(locationName);
                    Log.d("asd", "getNameFromLocation: " + locationName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void fetchMapItems(String type){
        apiService.getMapItems(type,"longitude,latitude,category").enqueue(new Callback<List<MapItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<MapItem>> call, @NonNull Response<List<MapItem>> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    List<MapItem> placesRes = response.body();

                    if (placesRes != null){
                        if (Objects.equals(type, "events")) {
                            events.addAll(placesRes);
                        }else{
                            places.addAll(placesRes);
                        }
                    }
                    mapItems.postValue((ArrayList<MapItem>) placesRes);
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() +response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MapItem>> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }

    private void fetchCategories(String type) {
        apiService.getCategories(type).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categoriesResponse = response.body();
                    Log.i(TAG, "onResponse: " + categoriesResponse);
                    if (categoriesResponse != null){
                        if (Objects.equals(type, "events")) {
                            eventCategories.addAll(categoriesResponse);
                        }else{
                            placeCategories.addAll(categoriesResponse);
                        }
                    }
                    categories.postValue((ArrayList<String>) categoriesResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle the error
            }
        });
    }

    private void fetchEventData(String id){
        apiService.getEvent(id, null).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    Event event = response.body();
                    mapSelectedEventData.postValue(event);
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }

    private void fetchPlaceData(String id){
        apiService.getPlace(id, null).enqueue(new Callback<Place>() {
            @Override
            public void onResponse(@NonNull Call<Place> call, @NonNull Response<Place> response) {
                if (response.isSuccessful()) {
                    Place place = response.body();
                    mapSelectedPlaceData.postValue(place);
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Place> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }
}
