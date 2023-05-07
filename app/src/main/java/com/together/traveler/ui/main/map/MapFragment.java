package com.together.traveler.ui.main.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentMapBinding;
import com.together.traveler.model.Event;
import com.together.traveler.model.Place;
import com.together.traveler.ui.main.home.HomeViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment extends Fragment {
    private final String TAG = "MapFragment";

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final ArrayList<OverlayItem> items = new ArrayList<>();
    private FragmentMapBinding binding;
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private IMapController mapController;
    private EditText locationSearch;
    private MapViewModel mapViewModel;
    private HomeViewModel homeViewModel;
    private Timer timer = new Timer();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final Context ctx = getContext();
        final GeoPoint startPoint = new GeoPoint(40.740295, 44.865835);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        ImageButton onCenterButton = binding.mapBtnCenterOnLocation;
        Button eventsButton = binding.mapBtnEvents;
        ChipGroup chipGroup = binding.mapChgcategories;
        Button placesButton = binding.mapBtnPlaces;
        FragmentContainerView eventContainerView = binding.mapFcvUser;

        final int textColor = placesButton.getCurrentTextColor();

        locationSearch = binding.mapEtLocation;
        map = requireView().findViewById(R.id.map);
        mapController = map.getController();
        assert ctx != null;
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        mapController.setZoom(10);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);

        mapViewModel.setCenter(startPoint);

        ItemizedOverlayWithFocus<OverlayItem> mPointsOverlay = new ItemizedOverlayWithFocus<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int position, final OverlayItem item) {
                        if (mapViewModel.getState().getValue() != null && mapViewModel.getState().getValue() == 0) {
                            eventContainerView.setVisibility(View.VISIBLE);
                            homeViewModel.setMapSelectedEvent(position);
                        }else{
                            mapViewModel.setMapSelectedPlace(position);
                        }
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int position, final OverlayItem item) {
                        return false;
                    }
                }, ctx);

        mPointsOverlay.setFocusItemsOnTap(true);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.d(TAG, "Single tap helper");
                mPointsOverlay.unSetFocusedItem();
                eventContainerView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.d(TAG, "LongPressHelper");
                return false;
            }

        };

        MapEventsOverlay mEventOverlay = new MapEventsOverlay(ctx, mReceive);
        map.getOverlays().add(mLocationOverlay);
        map.getOverlays().add(mPointsOverlay);
        map.getOverlays().add(mEventOverlay);
        map.invalidate();

        onCenterButton.setOnClickListener(v -> centerOnLocation(18));

        locationSearch.addTextChangedListener(afterTextChangedListener);

//        startRoadManagerTask(ctx, startPoint);


        mapViewModel.getOverlayItems().observe(getViewLifecycleOwner(), data -> {
            Log.i(TAG, "onViewCreated: " + data);
            mPointsOverlay.removeAllItems();
            mPointsOverlay.addItems(data);
            map.invalidate();
        });

        mapViewModel.getCenter().observe(getViewLifecycleOwner(), data->{
            mLocationOverlay.disableFollowLocation();
            mapController.setCenter(data);
        });

        mapViewModel.getState().observe(getViewLifecycleOwner(), state->{
            int orangeColor = ContextCompat.getColor(requireActivity(), R.color.orange);
            eventsButton.setTextColor(textColor);
            placesButton.setTextColor(textColor);

            switch (state){
                case 0:
                    eventsButton.setTextColor(orangeColor);
//                    newEvents = userViewModel.getData().getValue().getUpcomingEvents();
                    break;
                case 1:
                    placesButton.setTextColor(orangeColor);
//                    newEvents = userViewModel.getData().getValue().getSavedEvents();
                    break;
            }
        });

        mapViewModel.getPlaces().observe(getViewLifecycleOwner(), places -> {
            Place place;
            mPointsOverlay.removeAllItems();
            for (int i = 0; i < places.size(); i++) {
                place = places.get(i);
                mPointsOverlay.addItem(new OverlayItem(place.getName(), place.getDescription(), new GeoPoint(place.getLatitude(), place.getLongitude())));
            }
            map.invalidate();
        });

        mapViewModel.getCategories().observe(getViewLifecycleOwner(), categories ->{
            chipGroup.removeAllViews();
            for (int i = 0; i < categories.size(); i++) {
                Chip chip = new Chip(requireContext());
                chip.setText(categories.get(i));
                chip.setClickable(true);
                chip.setCheckable(false);
                chip.setOnCloseIconClickListener(v -> {
                    //TODO: Handle the click event here
                });
                chipGroup.addView(chip);
            }
        });

        homeViewModel.getData().observe(getViewLifecycleOwner(), data->{
            Event event;
            mPointsOverlay.removeAllItems();
            for (int i = 0; i < data.size(); i++) {
                event = data.get(i);
                mPointsOverlay.addItem(new OverlayItem(event.getTitle(), event.getDescription(), new GeoPoint(event.getLatitude(), event.getLongitude())));
            }
            map.invalidate();
        });

        eventContainerView.setOnClickListener(v->{
            NavDirections action = MapFragmentDirections.actionMapFragmentToEventFragment(homeViewModel.getMapSelectedEvent().getValue(), homeViewModel.getUserId());
            NavHostFragment.findNavController(this).navigate(action);
        });

        eventsButton.setOnClickListener(v -> {
            mapViewModel.setState(0);
            homeViewModel.fetchEvents();
        });
        placesButton.setOnClickListener(v -> {
            mapViewModel.setState(1);
            mapViewModel.fetchPlaces();
        });

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissionsIfNecessary(perms);
    }


    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationOverlay.disableMyLocation();
        mLocationOverlay.disableFollowLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private final TextWatcher afterTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            timer.cancel();
            timer = new Timer();
        }

        @Override
        public void afterTextChanged(Editable s) {
            long DELAY = 750;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!locationSearch.getText().toString().equals("")) {
                        Log.i("asd", "afterTextChanged: " + locationSearch.getText().toString());
                        mapViewModel.setSearch(locationSearch.getText().toString());
                    }
                }
            }, DELAY);


        }
    };

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void centerOnLocation(float zoom) {
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mapController.setZoom(zoom);
    }

    private void startRoadManagerTask(Context ctx , GeoPoint startPoint) {
        // Create a HandlerThread
        HandlerThread handlerThread = new HandlerThread("LocationThread");
        handlerThread.start();

        // Get a handler associated with the HandlerThread
        Handler locationHandler = new Handler(handlerThread.getLooper());

        // Post a runnable to the handler that will wait until getMyLocation is not null
        locationHandler.post(() -> {
            while (mLocationOverlay.getMyLocation() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Call RoadManagerTask once getMyLocation is not null
            new RoadManagerTask(ctx, startPoint, mLocationOverlay.getMyLocation()).execute();
        });

    }
    @SuppressLint("StaticFieldLeak")
    private class RoadManagerTask extends AsyncTask<Void, Void, Road> {

        private final Context mContext;
        private final GeoPoint mStartPoint;
        private final GeoPoint mEndPoint;

        public RoadManagerTask(Context context, GeoPoint startPoint, GeoPoint endPoint) {
            mContext = context;
            mStartPoint = startPoint;
            mEndPoint = endPoint;
        }

        @Override
        protected Road doInBackground(Void... params) {
            RoadManager roadManager = new OSRMRoadManager(mContext, MapViewModel.MY_USER_AGENT);
            ArrayList<GeoPoint> waypoints = new ArrayList<>(Arrays.asList(mStartPoint, mEndPoint));
            return roadManager.getRoad(waypoints);
        }

        @Override
        protected void onPostExecute(Road road) {
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            map.getOverlays().add(roadOverlay);
            map.invalidate();
        }
    }
}

