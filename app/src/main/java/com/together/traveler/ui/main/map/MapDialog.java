package com.together.traveler.ui.main.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MapDialog extends DialogFragment {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final ArrayList<OverlayItem> items = new ArrayList<>();
    private FragmentMapBinding binding;
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private IMapController mapController;
    private EditText locationSearch;
    private MapViewModel mapViewModel;
    private Timer timer = new Timer();
    private MyDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.popup_horizontal_padding);
        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.popup_vertical_padding);
        int popupWidth = screenWidth - 2 * paddingHorizontal;
        int popupHeight = screenHeight - 2 * paddingVertical;
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(popupWidth , popupHeight);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        Objects.requireNonNull(getDialog()).setTitle("popup_map");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context ctx = getContext();
        final GeoPoint startPoint = new GeoPoint(40.740295, 44.865835);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        ImageButton onCenterButton = binding.mapBtnCenterOnLocation;
        locationSearch = binding.mapEtLocation;
        map = requireView().findViewById(R.id.map);
        mapController = map.getController();
        assert ctx != null;
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        mapController.setZoom(18);
        mapController.setCenter(startPoint);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);

        ItemizedOverlayWithFocus<OverlayItem> mPointsOverlay = new ItemizedOverlayWithFocus<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int position, final OverlayItem item) {
                        //do something
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
                Log.d("debug", "Single tap helper");
                mPointsOverlay.unSetFocusedItem();
                return false;

            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.d("debug", "LongPressHelper");
                mapViewModel.setItem(p);
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

        mapViewModel.setFromAdd(true);
        mapViewModel.getOverlayItems().observe(getViewLifecycleOwner(), data -> {
            Log.i("asd", "onViewCreated: " + data);
            mPointsOverlay.removeAllItems();
            mPointsOverlay.addItems(data);
            map.invalidate();
        });
        mapViewModel.getCenter().observe(getViewLifecycleOwner(), data->{
            mLocationOverlay.disableFollowLocation();
            mapController.setCenter(data);
        });
        mapViewModel.getLocationName().observe(getViewLifecycleOwner(), data-> {
            Toast.makeText(ctx, data, Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onDialogResult(data);
            }
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

    public interface MyDialogListener {
        void onDialogResult(String result);
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
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
}
