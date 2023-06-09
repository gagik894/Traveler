package com.together.traveler.ui.add.place.times;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.together.traveler.R;
import com.together.traveler.databinding.DialogSelectTimesBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SelectTimesDialog extends DialogFragment {

    // Binding for the dialog layout
    private DialogSelectTimesBinding binding;
    // Listener to communicate with the parent activity/fragment
    private SelectTimesDialog.MyDialogListener listener;
    // EditText fields for opening times
    private final EditText[] openingTimesEt = new EditText[7];
    // EditText fields for closing times
    private final EditText[] closingTimesEt = new EditText[7];
    // CheckBoxes for closed days
    private final CheckBox[] closedChb = new CheckBox[7];

    // Array to store opening times
    private final String[] openingTimes = new String[7];
    // Array to store closing times
    private final String[] closingTimes = new String[7];
    // Array to store closed days
    private final boolean[] isClosed = new boolean[7];

    // CheckBox for always open option
    private CheckBox alwaysOpenChb;

    // Variable to store the selected hour
    private int hour;
    // Variable to store the selected minute
    private int minute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DialogSelectTimesBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog()).setTitle("popup_times");
        return binding.getRoot();
    }

    // Loop through the days of the week
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context ctx = getContext();
        alwaysOpenChb = binding.timesChbAlwaysOpen;
        openingTimesEt[0] = binding.timesEt0Open;
        openingTimesEt[1] = binding.timesEt1Open;
        openingTimesEt[2] = binding.timesEt2Open;
        openingTimesEt[3] = binding.timesEt3Open;
        openingTimesEt[4] = binding.timesEt4Open;
        openingTimesEt[5] = binding.timesEt5Open;
        openingTimesEt[6] = binding.timesEt6Open;

        closingTimesEt[0] = binding.timesEt0Close;
        closingTimesEt[1] = binding.timesEt1Close;
        closingTimesEt[2] = binding.timesEt2Close;
        closingTimesEt[3] = binding.timesEt3Close;
        closingTimesEt[4] = binding.timesEt4Close;
        closingTimesEt[5] = binding.timesEt5Close;
        closingTimesEt[6] = binding.timesEt6Close;

        closedChb[0] = binding.timesChb0;
        closedChb[1] = binding.timesChb1;
        closedChb[2] = binding.timesChb2;
        closedChb[3] = binding.timesChb3;
        closedChb[4] = binding.timesChb4;
        closedChb[5] = binding.timesChb5;
        closedChb[6] = binding.timesChb6;

        final Button cancelBtn = binding.timesBtnCancel;
        final Button okBtn = binding.timesBtnOk;

        // Click listener for the cancel button
        cancelBtn.setOnClickListener(v -> this.dismiss());
        // Click listener for the OK button
        okBtn.setOnClickListener(v -> {
            updateListener();
            this.dismiss();
        });

        // Loop through the days of the week
        for (int i = 0; i < 7; i++) {
            int index = i;
            openingTimesEt[i].setOnClickListener(v -> {
                // Click listener for opening times EditText
                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                        (view1, hourOfDay, minute) -> {
                            // Convert selected time to Date object
                            Date selectedTime = convertToTime(String.format("%s:%s", hourOfDay, minute));
                            // Format the Date object to a time string
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                            String dateTimeString = dateFormat.format(selectedTime.getTime());
                            if (closingTimes[index] != null && closingTimes[index].length() > 0) {
                                // Check if the selected opening time is after the corresponding closing time
                                Date time = convertToTime(closingTimes[index]);
                                Log.i("time", "onViewCreated: " + closingTimes[index] + " " + time);
                                if (selectedTime.getTime() > time.getTime()) {
                                    Toast.makeText(requireContext(), R.string.end_must_be_after_start, Toast.LENGTH_SHORT).show();
                                } else {
                                    // Update the opening time and EditText
                                    openingTimes[index] = dateTimeString;
                                    openingTimesEt[index].setText(dateTimeString);
                                    // If it's the first day (index 0) and all other opening times are null, set all opening times
                                    if (index == 0 && allNullExceptFirst(true)) {
                                        setAllTimes(true);
                                    }
                                }
                            } else {
                                // Update the opening time and EditText
                                openingTimes[index] = dateTimeString;
                                openingTimesEt[index].setText(dateTimeString);
                                // If it's the first day (index 0) and all other opening times are null, set all opening times
                                if (index == 0 && allNullExceptFirst(true)) {
                                    setAllTimes(true);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            });
            // Click listener for closing times EditText
            closingTimesEt[i].setOnClickListener(v -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                        (view1, hourOfDay, minute) -> {
                            // Convert selected time to Date object
                            Date selectedTime = convertToTime(String.format("%s:%s", hourOfDay, minute));
                            // Format the Date object to a time string
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                            String dateTimeString = dateFormat.format(selectedTime.getTime());
                            if (openingTimes[index] != null && openingTimes[index].length() > 0) {
                                // Check if the selected closing time is before the corresponding opening time
                                Date time = convertToTime(openingTimes[index]);
                                Log.i("time", "onViewCreated: " + openingTimes[index] + " " + time.toString());
                                if (selectedTime.getTime() < time.getTime()) {
                                    Toast.makeText(requireContext(), R.string.end_must_be_after_start, Toast.LENGTH_SHORT).show();
                                } else {
                                    // Update the closing time and EditText
                                    closingTimes[index] = dateTimeString;
                                    closingTimesEt[index].setText(dateTimeString);
                                    if (index == 0 && allNullExceptFirst(false)) {
                                        setAllTimes(false);
                                    }
                                }
                            } else {
                                // Update the closing time and EditText
                                closingTimes[index] = dateTimeString;
                                closingTimesEt[index].setText(dateTimeString);
                                if (index == 0 && allNullExceptFirst(false)) {
                                    setAllTimes(false);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            });
            // Click listener for closed days CheckBox
            closedChb[i].setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    openingTimes[index] = null;
                    closingTimes[index] = null;
                    isClosed[index] = true;
                    openingTimesEt[index].setEnabled(false);
                    closingTimesEt[index].setEnabled(false);
                } else {
                    isClosed[index] = false;
                    openingTimes[index] = String.valueOf(openingTimesEt[index].getText());
                    closingTimes[index] = String.valueOf(closingTimesEt[index].getText());
                    openingTimesEt[index].setEnabled(true);
                    closingTimesEt[index].setEnabled(true);
                }
            });
        }
        // Click listener for always open CheckBox
        alwaysOpenChb.setOnCheckedChangeListener((v, isChecked) -> {
            for (int i = 0; i < 7; i++) {
                boolean isClosed = closedChb[i].isChecked();
                openingTimesEt[i].setEnabled(!isChecked && !isClosed);
                closingTimesEt[i].setEnabled(!isChecked && !isClosed);
                closedChb[i].setEnabled(!isChecked);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface MyDialogListener {
        void onDialogResult(String[] openingTimes, String[] closingTimes, boolean[] isClosed, boolean isAlwaysOpen);
    }

    public void setListener(SelectTimesDialog.MyDialogListener listener) {
        this.listener = listener;
    }

    private void updateListener() {
        listener.onDialogResult(openingTimes, closingTimes, isClosed, alwaysOpenChb.isChecked());
    }

    // Method to check if all opening/closing times are null except the first day
    private boolean allNullExceptFirst(boolean opening) {
        boolean allNullExceptFirst = true;
        for (int i = 1; i < 7; i++) {
            if ((opening && openingTimes[i] != null) || (!opening && closingTimes[i] != null)) {
                allNullExceptFirst = false;
                break;
            }
        }
        return allNullExceptFirst;
    }

    // Method to set all opening/closing times based on the first day's time
    private void setAllTimes(boolean opening) {
        for (int i = 1; i < 7; i++) {
            if (opening) {
                openingTimes[i] = openingTimes[0];
                openingTimesEt[i].setText(openingTimes[i]);
            } else {
                closingTimes[i] = closingTimes[0];
                closingTimesEt[i].setText(closingTimes[i]);
            }
        }
    }

    // Method to convert time string to Date object
    private Date convertToTime(String TimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(TimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}