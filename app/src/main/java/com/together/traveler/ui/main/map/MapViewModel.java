package com.together.traveler.ui.main.map;

import android.location.Address;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    private final MutableLiveData<ArrayList<String>> categories;

    public static final String MY_USER_AGENT = "Traveler";
    private final MutableLiveData<ArrayList<OverlayItem>> overlayItems;
    private final MutableLiveData<ArrayList<Place>> places;
    private final MutableLiveData<String> search;
    private final MutableLiveData<GeoPoint> center;
    private final MutableLiveData<Integer> state;
    private final ApiService apiService;
    private String locationName;

    private final ArrayList<String> placeCategories;
    private final ArrayList<String> eventCategories;

    private final MutableLiveData<Place> mapSelectedPlace;

    public MapViewModel() {
        placeCategories = new ArrayList<>();
        eventCategories = new ArrayList<>();
        categories = new MutableLiveData<>(new ArrayList<>());
        mapSelectedPlace = new MutableLiveData<>();
        overlayItems = new MutableLiveData<>(new ArrayList<>());
        search = new MutableLiveData<>();
        center = new MutableLiveData<>();
        places = new MutableLiveData<>();
        state = new MutableLiveData<>();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        setState(0);
    }

    public void setSearch(String data) {
        this.search.postValue(data);
        getLocationFromName(data);
    }

    public void setMapSelectedPlace(int position){
        this.mapSelectedPlace.setValue(this.getPlaces().getValue().get(position));
    }

    public void setState(Integer state){
        if (eventCategories.size() == 0) {
            fetchCategories("events");
            fetchCategories("places");
        }
        if (state == 0){
            categories.setValue(eventCategories);
        }else{
            categories.setValue(placeCategories);
        }
        this.state.setValue(state);
    }

    public void setItem(GeoPoint p){
        OverlayItem overlayItem = new OverlayItem(" ", "", new GeoPoint(p.getLatitude(), p.getLongitude()));
        Objects.requireNonNull(overlayItems.getValue()).clear();
        getNameFromLocation(p);
        overlayItems.getValue().add(overlayItem);
        overlayItems.postValue(overlayItems.getValue());
    }

    public void setCenter(GeoPoint center){
        this.center.setValue(center);
    }

    public MutableLiveData<Place> getMapSelectedPlace() {
        return mapSelectedPlace;
    }

    public MutableLiveData<ArrayList<String>> getCategories() {
        return categories;
    }

    public LiveData<ArrayList<OverlayItem>> getOverlayItems() {
        return overlayItems;
    }

    public MutableLiveData<Integer> getState() {
        return state;
    }

    public LiveData<GeoPoint> getCenter() {
        Log.d("asd", "getCenter: " + center.getValue());
        return center;
    }

    public MutableLiveData<ArrayList<Place>> getPlaces() {
        return places;
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

    public void fetchPlaces(){
        apiService.getPlaces().enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(@NonNull Call<List<Place>> call, @NonNull Response<List<Place>> response) {
                if (response.isSuccessful()) {
                    Log.d("UserViewModel", "onResponse: " + response.body());
                    List<Place> placesRes = response.body();

                    places.postValue((ArrayList<Place>) placesRes);
                } else {
                    Log.e(TAG, "fetchEvents request failed with code: " + response.code() +response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Place>> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchEvents request failed with error: " + t.getMessage());
            }
        });
    }

    public void fetchCategories(String type) {
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
                    categories.setValue((ArrayList<String>) categoriesResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle the error
            }
        });
    }

}
