package com.together.traveler.ui.add.event;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.noowenz.customdatetimepicker.CustomDateTimePicker;
import com.together.traveler.databinding.FragmentAddEventBinding;
import com.together.traveler.ui.main.MainActivity;
import com.together.traveler.ui.main.map.MapDialog;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddEvent extends Fragment implements MapDialog.MyDialogListener {

    private AddEventViewModel mViewModel;
    private EditText title;
    private EditText location;
    private EditText startDateAndTime;
    private EditText endDateAndTime;
    private EditText description;
    private EditText ticketsCount;
    private ImageButton eventImage;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CAMERA = 201;
    private static final int SELECT_FILE = 202;
    private ActivityResultLauncher<Intent> imageCroppingActivityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        FragmentAddEventBinding binding = FragmentAddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCreate = binding.addBtnCreate;
        title = binding.addEtTitle;
        location = binding.addEtLocation;
        startDateAndTime = binding.addEtStartDate;
        endDateAndTime = binding.addEtEndDate;
        description = binding.addEtDescription;
        ticketsCount = binding.addEtTicketsCount;
        eventImage = binding.addIbEventImage;

        title.addTextChangedListener(afterTextChangedListener);
        location.addTextChangedListener(afterTextChangedListener);
        startDateAndTime.addTextChangedListener(afterTextChangedListener);
        endDateAndTime.addTextChangedListener(afterTextChangedListener);
        description.addTextChangedListener(afterTextChangedListener);
        ticketsCount.addTextChangedListener(afterTextChangedListener);

        location.setOnClickListener(this::showPopupView);
        startDateAndTime.setOnClickListener(v -> showDateTimePicker(true));
        endDateAndTime.setOnClickListener(v -> showDateTimePicker(false));
        btnCreate.setOnClickListener(v -> {
            mViewModel.create();
            Intent switchActivityIntent = new Intent(requireActivity(), MainActivity.class);
            startActivity(switchActivityIntent);
        });
        eventImage.setOnClickListener(v -> selectImage());

        mViewModel.getData().observe(getViewLifecycleOwner(), data -> eventImage.setImageBitmap(data.getImageBitmap()));
        mViewModel.isValid().observe(getViewLifecycleOwner(), btnCreate::setEnabled);


        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDialogResult(String result) {
        location.setText(result);
    }

    private void startImageCropping(Uri sourceUri) {
        String destinationFileName = "CroppedImage";
        UCrop uCrop = UCrop.of(sourceUri, Uri.fromFile(new File(requireActivity().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(4, 3);
        uCrop.withMaxResultSize(1000, 1000);

        // Launch the image cropping activity
        Intent cropIntent = uCrop.getIntent(requireActivity());
        imageCroppingActivityResultLauncher.launch(cropIntent);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("asd", "onActivityResult: " + requestCode + resultCode + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA) {
                Bitmap selectedImageBitmap = null;
                Uri selectedImageUri = data.getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (selectedImageBitmap != null) {
                    Uri imageUri = getImageUri(requireContext(), selectedImageBitmap);
                    startImageCropping(imageUri);
                }
            } else {
                Log.d("asd", "onActivityResult: else");
            }
        }
    }

    private void requestPermissionsIfNecessary(String permission) {

        String[] permissions = new String[1];
        if (Objects.equals(permission, "camera")) {
            permissions[0] = Manifest.permission.CAMERA;
        }else if (Objects.equals(permission, "storage")){
            permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(requireActivity(), permissions , PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestPermissionsIfNecessary("camera");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (items[item].equals("Choose from Library")) {
                requestPermissionsIfNecessary("storage");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showPopupView(View anchorView) {
        MapDialog popupFragment = new MapDialog();
        popupFragment.setListener(this);
        popupFragment.show(getChildFragmentManager(), "popup_map");
    }

    private void showDateTimePicker(boolean start) {
        new CustomDateTimePicker(requireActivity(), new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(@NotNull Dialog dialog, @NotNull Calendar calendar,
                              @NotNull Date fullDate, int year, @NotNull String monthFullName,
                              @NotNull String monthShortName, int monthNumber, int day,
                              @NotNull String weekDayFullName, @NotNull String weekDayShortName,
                              int hour24, int hour12, int min, int sec, @NotNull String AM_PM) {
                Toast.makeText(requireContext(), "Date and time selected!", Toast.LENGTH_SHORT).show();
                String date = monthShortName + " " + day;
                String time = hour24 + ":" + min;
                if (start) {
                    mViewModel.setStartDateAndTime(date, time);
                    startDateAndTime.setText(String.format("%s %s", date, time));
                } else {
                    mViewModel.setEndDateAndTime(date, time);
                    endDateAndTime.setText(String.format("%s %s", date, time));
                }
                Log.i("asd", "onSet: " + date + " " + time);
            }

            @Override
            public void onCancel() {
                Toast.makeText(requireContext(), "Date and Time selection is cancelled", Toast.LENGTH_SHORT).show();
            }
        }).showDialog();
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
            mViewModel.dataChanged(title.getText().toString(), location.getText().toString(), description.getText().toString(), Integer.parseInt(String.valueOf(ticketsCount.getText()).equals("") ? String.valueOf(0) : String.valueOf(ticketsCount.getText())));
        }
    };

}