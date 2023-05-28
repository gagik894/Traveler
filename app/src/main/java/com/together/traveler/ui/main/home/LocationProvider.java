package com.together.traveler.ui.main.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.together.traveler.context.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationProvider {
    private final String TAG = "LocationProvider";
    private final Context context = AppContext.getContext();
    private final LocationManager locationManager;
    private final MutableLiveData<String> locationLiveData = new MutableLiveData<>();

    public LocationProvider() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            locationLiveData.setValue(getNameFromLocation(location));
        } else {
            Log.i(TAG, "getLastKnownLocation: else");
            // Request a single location update
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        }
    }

    public LiveData<String> getLocationLiveData() {
        return locationLiveData;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            locationLiveData.setValue(getNameFromLocation(location));
            // Remove the location listener after receiving a single update
            locationManager.removeUpdates(this);
        }
    };

    private String getNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String cityName = "";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

}



