package com.together.traveler.ui.event.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.together.traveler.ui.event.EventViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context ctx = getContext();
        EventViewModel eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        ImageButton onCenterButton = binding.mapBtnCenterOnLocation;
        EditText locationSearch = binding.mapEtLocation;
        MapView map = requireView().findViewById(R.id.map);
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


        if (eventViewModel.getData().getValue() != null) {
            double longitude = eventViewModel.getData().getValue().getLongitude();
            double latitude = eventViewModel.getData().getValue().getLatitude();
            GeoPoint location = new GeoPoint(longitude, latitude);
            Marker marker = new Marker(map);
            marker.setPosition(location);
            map.getOverlays().add(marker);
            mapController.setCenter(location);
            map.invalidate();
        }




    }


    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

