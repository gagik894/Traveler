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
import android.os.Bundle;
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider != null && locationManager.isProviderEnabled(provider)) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChangedListener.onLocationChanged(getNameFromLocation(location));
            } else {
                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            onLocationChangedListener.onLocationChanged(getNameFromLocation(location));
            locationManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {
            getLastKnownLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {}
    };


    private String getNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        String cityName = "";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                cityName = address.getLocality();

                if (cityName == null) {
                    cityName = address.getSubLocality();
                }
                if (cityName == null) {
                    cityName = address.getAdminArea();
                }
                if (cityName.equals("Moskva")) {
                    cityName = "Moscow";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }



    public interface OnLocationChangedListener {
        void onLocationChanged(String cityName);
    }
}


