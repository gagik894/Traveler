package com.together.traveler.ui.admin.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentMapBinding;
import com.together.traveler.ui.admin.AdminViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private AdminViewModel adminViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("ghj", "onViewCreated: ");

        ImageButton onCenterButton = binding.mapBtnCenterOnLocation;
        EditText locationSearch = binding.mapEtLocation;
        MapView map = binding.map;
        IMapController mapController = map.getController();
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(false);
        mapController.setZoom(18);

        onCenterButton.setVisibility(View.GONE);
        locationSearch.setVisibility(View.GONE);
        map.setClickable(false);
        map.setFocusable(false);
        map.setFocusableInTouchMode(false);
        map.setOnTouchListener((v, event) -> true);
        disableClickEvents(map);

        adminViewModel.getSelectedPlaceData().observe(getViewLifecycleOwner(), data->{
            Log.i("adminMap", "onViewCreated: "+data + map);
            double longitude = data.getLongitude();
            double latitude = data.getLatitude();
            GeoPoint location = new GeoPoint(latitude, longitude);
            Marker marker = new Marker(map);
            marker.setPosition(location);
            map.getOverlays().add(marker);
            mapController.setCenter(location);
            map.invalidate();
        });
    }


    @Override
    public void onResume() {
        Log.i("ghj", "onResume: ");
        super.onResume();
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i("ghj", "onStop: ");
    }
    @Override
    public void onPause() {
        Log.i("ghj", "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i("ghj", "onDestroyView: ");
        super.onDestroyView();
        adminViewModel = null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ghj", "onDestroy: ");
    }

    private void disableClickEvents(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setClickable(false);
            child.setFocusable(false);
            child.setFocusableInTouchMode(false);
            if (child instanceof ViewGroup) {
                disableClickEvents((ViewGroup) child);
            }
        }
    }
}