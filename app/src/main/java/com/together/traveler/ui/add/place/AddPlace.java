package com.together.traveler.ui.add.place;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentAddPlaceBinding;
import com.together.traveler.ui.add.place.times.SelectTimesDialog;
import com.together.traveler.ui.main.MainActivity;
import com.together.traveler.ui.main.map.MapDialog;
import com.yalantis.ucrop.UCrop;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AddPlace extends Fragment implements SelectTimesDialog.MyDialogListener, MapDialog.MyDialogListener {
    private final String TAG = "AddPlace";

    private AddPlaceViewModel mViewModel;
    private EditText name;
    private EditText location;
    private EditText description;
    private EditText phone;
    private EditText url;
    private EditText times;
    private ImageButton eventImage;
    private static final int SELECT_FILE = 202;
    private ArrayAdapter<String> adapter;

    private ActivityResultLauncher<Intent> imageCroppingActivityResultLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddPlaceViewModel.class);

        imageCroppingActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri croppedImageUri = null;
                        if (data != null) {
                            croppedImageUri = UCrop.getOutput(data);
                        }
                        if (croppedImageUri != null) {
                            try {
                                Bitmap croppedImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), croppedImageUri);
                                mViewModel.setEventImage(croppedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                isGranted -> {
                    if (isGranted.containsValue(false)) {

                    } else {

                    }
                }
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        FragmentAddPlaceBinding binding = FragmentAddPlaceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCreate = binding.addPlaceBtnCreate;
        Spinner spinner = binding.addPlaceTagSpinner;
        name = binding.addPlaceEtName;
        location = binding.addPlaceEtLocation;
        description = binding.addPlaceEtDescription;
        eventImage = binding.addPlaceIbEventImage;
        phone = binding.addPlaceEtPhone;
        url = binding.addPlaceEtUrl;
        times = binding.addPlaceEtTimes;

        adapter = new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_spinner_item,
                new ArrayList<>());

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        name.addTextChangedListener(afterTextChangedListener);
        location.addTextChangedListener(afterTextChangedListener);
        description.addTextChangedListener(afterTextChangedListener);
        phone.addTextChangedListener(afterTextChangedListener);
        url.addTextChangedListener(afterTextChangedListener);
        times.addTextChangedListener(afterTextChangedListener);


        location.setOnClickListener(this::showPopupView);
        eventImage.setOnClickListener(v -> selectImage());
        times.setOnClickListener(this::showTimesPopupView);

        btnCreate.setOnClickListener(v -> {
            mViewModel.create();
            Intent switchActivityIntent = new Intent(requireActivity(), MainActivity.class);
            startActivity(switchActivityIntent);
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                mViewModel.setEventCategory(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mViewModel.getData().observe(getViewLifecycleOwner(), data -> eventImage.setImageBitmap(data.getImageBitmap()));
        mViewModel.isValid().observe(getViewLifecycleOwner(), btnCreate::setEnabled);
        mViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayList<String> categoriesList = new ArrayList<>(categories);
            adapter.clear();
            adapter.addAll(categoriesList);
            adapter.notifyDataSetChanged();
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("asd", "onActivityResult: " + requestCode + resultCode + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                startImageCropping(selectedImageUri);
            } else {
                Log.d(TAG, "onActivityResult: else");
            }
        }
    }

    private void startImageCropping(Uri sourceUri) {
        String destinationFileName = "CroppedImage";
        UCrop uCrop = UCrop.of(sourceUri, Uri.fromFile(new File(requireActivity().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(4, 3);
        uCrop.withMaxResultSize(1000, 1000);

        Intent cropIntent = uCrop.getIntent(requireActivity());
        imageCroppingActivityResultLauncher.launch(cropIntent);
    }

    private void selectImage() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissionLauncher.launch(permissions);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_FILE);
    }

    private void showPopupView(View anchorView) {
        MapDialog popupFragment = new MapDialog();
        popupFragment.setListener(this);
        popupFragment.show(getChildFragmentManager(), "popup_map");
    }

    private void showTimesPopupView(View anchorView) {
        SelectTimesDialog popupFragment = new SelectTimesDialog();
        popupFragment.setListener(this);
        popupFragment.show(getChildFragmentManager(), "popup_times");
    }
    private final TextWatcher afterTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable s) {
            mViewModel.dataChanged(name.getText().toString(), location.getText().toString(), description.getText().toString(), phone.getText().toString(), url.getText().toString());
        }
    };

    @Override
    public void onDialogResult(String result, GeoPoint geoPoint) {
        Log.i(TAG, "onDialogResult: " + geoPoint.getLongitude());
        location.setText(result);
        mViewModel.setEventLocation(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    @Override
    public void onDialogResult(String[] openingTimes, String[] closingTimes) {
        Log.i(TAG, "onDialogResult: " + Arrays.toString(openingTimes));
        mViewModel.setEventOpenTimes(openingTimes, closingTimes);
        boolean openTimesAdded = mViewModel.checkOpenTimes(Objects.requireNonNull(mViewModel.getData().getValue()));
        if (openTimesAdded){
            times.setText(String.format("%s, %s - %s, ...", getString(R.string.monday), openingTimes[0], closingTimes[0]));
        }else{
            times.setText(R.string.add_select_all_open_times);
        }


    }
}
