package com.together.traveler.ui.add.event;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.noowenz.customdatetimepicker.CustomDateTimePicker;
import com.together.traveler.ui.main.MainActivity;
import com.together.traveler.databinding.FragmentAddEventBinding;
import com.together.traveler.ui.main.map.MapDialog;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);
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
        btnCreate.setOnClickListener(v->{
            mViewModel.create();
            Intent switchActivityIntent = new Intent(requireActivity(), MainActivity.class);
            startActivity(switchActivityIntent);
        });
        eventImage.setOnClickListener(v-> selectImage());

        mViewModel.getData().observe(getViewLifecycleOwner(), data->eventImage.setImageBitmap(data.getImageBitmap()));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap selectedImageBitmap = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                try {
                    selectedImageBitmap = getBitmapFromUri(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
            }
        }
        if (selectedImageBitmap != null) {
            mViewModel.setEventImage(selectedImageBitmap);
        }

    }

    @Override
    public void onDialogResult(String result) {
        location.setText(result);
    }


    private void requestPermissionsIfNecessary() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = requireContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, item) -> {
            requestPermissionsIfNecessary();
            if (items[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (items[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
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
                if(start){
                    mViewModel.setStartDateAndTime(date, time);
                    startDateAndTime.setText(String.format("%s %s", date, time));
                }else{
                    mViewModel.setEndDateAndTime(date, time);
                    endDateAndTime.setText(String.format("%s %s", date, time));
                }
                Log.i("asd", "onSet: "  + date + " " + time);
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