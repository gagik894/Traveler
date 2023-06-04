package com.together.traveler.ui.main.home;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.together.traveler.context.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationProvider {
    private final String TAG = "LocationProvider";
    private final Context context = AppContext.getContext();
    private final LocationManager locationManager;
    private final OnLocationChangedListener onLocationChangedListener;

    public LocationProvider(OnLocationChangedListener onLocationChangedListener) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.onLocationChangedListener = onLocationChangedListener;
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChangedListener.onLocationChanged(getNameFromLocation(location));
        } else {
            Log.i(TAG, "getLastKnownLocation: else");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            onLocationChangedListener.onLocationChanged(getNameFromLocation(location));
            // Remove the location listener after receiving a single update
            locationManager.removeUpdates(this);
        }
    };

    private String getNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        String cityName = "";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(String cityName);
    }
}


