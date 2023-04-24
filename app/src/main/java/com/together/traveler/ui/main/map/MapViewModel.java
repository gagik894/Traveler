package com.together.traveler.ui.main.map;

import android.app.Activity;
import android.location.Address;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.together.traveler.ui.event.EventViewModel;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapViewModel extends ViewModel {
    public static final String MY_USER_AGENT = "Traveler";
    private final MutableLiveData<ArrayList<OverlayItem>> overlayItems;
    private final MutableLiveData<String> search;
    private final MutableLiveData<GeoPoint> center;
    private final MutableLiveData<String> locationName;

    public MapViewModel() {
        overlayItems = new MutableLiveData<>(new ArrayList<>());
        search = new MutableLiveData<>();
        center = new MutableLiveData<>();
        locationName = new MutableLiveData<>();
    }

    public void setSearch(String data) {
        this.search.postValue(data);
        getLocationFromName(data);
    }

    public void addItem(GeoPoint p) {
        Objects.requireNonNull(this.overlayItems.getValue()).add(new OverlayItem("Dilijan", "", new GeoPoint(p.getLatitude(), p.getLongitude())));
        this.overlayItems.setValue(this.overlayItems.getValue());
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


    public LiveData<ArrayList<OverlayItem>> getOverlayItems() {
        return overlayItems;
    }

    public LiveData<GeoPoint> getCenter() {
        Log.d("asd", "getCenter: " + center.getValue());
        return center;
    }

    public LiveData<String> getLocationName() {
        return locationName;
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
                    this.locationName.postValue(locationName);
                    Log.d("asd", "getNameFromLocation: " + locationName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
