package com.together.traveler.ui.add.event;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentAddEventBinding;
import com.together.traveler.ui.main.MainActivity;
import com.together.traveler.ui.main.map.MapDialog;
import com.yalantis.ucrop.UCrop;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEvent extends Fragment implements MapDialog.MyDialogListener {
    private final String TAG = "AddEvent";


    private AddEventViewModel mViewModel;
    private EditText title;
    private EditText location;
    private EditText startDateAndTime;
    private EditText endDateAndTime;
    private EditText description;
    private EditText ticketsCount;
    private EditText newTag;
    private ImageButton eventImage;

    private ChipGroup chipGroup;
    private static final int SELECT_FILE = 202;
    private ArrayAdapter<String> adapter;

    private ActivityResultLauncher<Intent> imageCroppingActivityResultLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);

        // Set up activity result launcher for image cropping
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

        // Set up activity result launcher for requesting permissions
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                isGranted -> {
                    if (isGranted.containsValue(false)) {
                        // Handle permission not granted
                    } else {
                        // Handle permission granted
                    }
                }
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        FragmentAddEventBinding binding = FragmentAddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inflate the layout for this fragment
        Button btnCreate = binding.addBtnCreate;
        Spinner spinner = binding.addTagSpinner;
        Button addTag = binding.addBtnAddTag;
        title = binding.addEtTitle;
        location = binding.addEtLocation;
        startDateAndTime = binding.addEtStartDate;
        endDateAndTime = binding.addEtEndDate;
        description = binding.addEtDescription;
        ticketsCount = binding.addEtTicketsCount;
        newTag = binding.addEtNewTag;
        eventImage = binding.addIbEventImage;
        chipGroup = binding.chipGroup;
        adapter = new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_spinner_item,
                new ArrayList<>());

        // Set up adapter for spinner
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        title.addTextChangedListener(afterTextChangedListener);
        location.addTextChangedListener(afterTextChangedListener);
        startDateAndTime.addTextChangedListener(afterTextChangedListener);
        endDateAndTime.addTextChangedListener(afterTextChangedListener);
        description.addTextChangedListener(afterTextChangedListener);
        ticketsCount.addTextChangedListener(afterTextChangedListener);
        newTag.addTextChangedListener(tagsTextWatcher);

        // Set click listeners for specific views
        location.setOnClickListener(this::showPopupView);
        startDateAndTime.setOnClickListener(v -> showDatePicker(true));
        endDateAndTime.setOnClickListener(v ->{
            if (mViewModel.getStart().length() > 0){
                showDatePicker(false);
            }else{
                Toast.makeText(requireContext(), R.string.select_start_date_first, Toast.LENGTH_SHORT).show();
            }
        });
        eventImage.setOnClickListener(v -> selectImage());
        btnCreate.setOnClickListener(v -> {
            mViewModel.create();
            Intent switchActivityIntent = new Intent(requireActivity(), MainActivity.class);
            startActivity(switchActivityIntent);
        });
        addTag.setOnClickListener(v -> {
            mViewModel.addTag(String.valueOf(newTag.getText()));
            newTag.setText("");
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

        // Observe LiveData and update UI accordingly
        mViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            eventImage.setImageBitmap(data.getImageBitmap());
            chipGroup.removeAllViews();
            Log.i(TAG, "onCreateView: " + data.getTags());
            if (data.getTags() != null) {
                for (int i = 0; i < data.getTags().size(); i++) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(data.getTags().get(i));
                    chip.setCloseIconVisible(true);
                    chip.setClickable(true);
                    chip.setCheckable(false);
                    chip.setOnCloseIconClickListener(v -> mViewModel.deleteTag((String) chip.getText()));
                    chipGroup.addView(chip);
                }
            }
        });

        mViewModel.getIsTagValid().observe(getViewLifecycleOwner(), addTag::setEnabled);
        mViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayList<String> categoriesList = new ArrayList<>(categories);
            adapter.clear();
            adapter.addAll(categoriesList);
            adapter.notifyDataSetChanged();
        });
        mViewModel.getFormState().observe(getViewLifecycleOwner(), data->{
            if (data == null){
                return;
            }
            location.setError(null);
            startDateAndTime.setError(null);
            endDateAndTime.setError(null);
            if (data.getTitleError() != null) {
                title.setError(getString(data.getTitleError()));
            }
            if (data.getDescriptionError() != null) {
                description.setError(getString(data.getDescriptionError()));
            }
            if (data.getLocationError() != null) {
                location.setError(getString(data.getLocationError()));
            }
            if (data.getStartDateError() != null) {
                startDateAndTime.setError(getString(data.getStartDateError()));
            }
            if (data.getEndDateError() != null) {
                endDateAndTime.setError(getString(data.getEndDateError()));
            }
            if (data.getTicketsError() != null) {
                ticketsCount.setError(getString(data.getTicketsError()));
            }
            btnCreate.setEnabled(data.isDataValid());
        });
        return root;
    }

    // Implement other methods and listeners
    @Override
    public void onDialogResult(String result, GeoPoint geoPoint) {
        Log.i(TAG, "onDialogResult: " + geoPoint.getLongitude());
        location.setText(result);
        mViewModel.setEventLocation(geoPoint.getLatitude(), geoPoint.getLongitude());
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

    private void showDatePicker(boolean isStartDate){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth1);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view1, hourOfDay1, minute1) -> {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(year1, monthOfYear, dayOfMonth1, hourOfDay1, minute1);
                if (isStartDate) {
                    if (selectedTime.getTimeInMillis() < System.currentTimeMillis()) {
                        Toast.makeText(requireContext(), R.string.select_future_date, Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                        String dateTimeString = dateFormat.format(selectedTime.getTime());
                        mViewModel.setStartDateAndTime(dateTimeString);
                        startDateAndTime.setText(dateTimeString);
                    }
                } else {
                    Date date = convertToDate(mViewModel.getStart());
                    if (selectedTime.getTimeInMillis() < date.getTime()) {
                        Toast.makeText(requireContext(), R.string.end_must_be_after_start, Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                        String dateTimeString = dateFormat.format(selectedTime.getTime());
                        mViewModel.setEndDateAndTime(dateTimeString);
                        endDateAndTime.setText(dateTimeString);
                    }
                }
                if (selectedTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    view1.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                    view1.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
                }
            }, hourOfDay, minute, true);

            timePickerDialog.show();
        }, year, month, dayOfMonth);

        datePickerDialog.show();
        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        else {
            Date date = convertToDate(mViewModel.getStart());
            if (date != null) {
                datePickerDialog.getDatePicker().setMinDate(date.getTime());
            }
        }
    }

    private Date convertToDate(String dateTimeString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // TextWatcher to listen for changes in input fields
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
    // TextWatcher to listen for changes in the newTag input field
    private final TextWatcher tagsTextWatcher = new TextWatcher() {
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
            mViewModel.checkTag(s.toString());
        }
    };
}