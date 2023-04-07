package com.together.traveler.ui.add.event;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.noowenz.customdatetimepicker.CustomDateTimePicker;
import com.together.traveler.MainActivity;
import com.together.traveler.databinding.FragmentAddEventBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class AddEvent extends Fragment{

    private AddEventViewModel mViewModel;
    private EditText startDateAndTime;
    private EditText endDateAndTime;


    public static AddEvent newInstance() {
        return new AddEvent();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentAddEventBinding binding = FragmentAddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final Button btnCreate = binding.addBtnCreate;
        final EditText title = binding.addEtTitle;
        final EditText location = binding.addEtLocation;
        startDateAndTime = binding.addEtStartDate;
        endDateAndTime = binding.addEtEndDate;
        final EditText description = binding.addEtDescription;
        final EditText ticketsCount = binding.addEtTicketsCount;

        TextWatcher afterTextChangedListener = new TextWatcher() {
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

        title.addTextChangedListener(afterTextChangedListener);
        location.addTextChangedListener(afterTextChangedListener);
        startDateAndTime.addTextChangedListener(afterTextChangedListener);
        endDateAndTime.addTextChangedListener(afterTextChangedListener);
        description.addTextChangedListener(afterTextChangedListener);
        ticketsCount.addTextChangedListener(afterTextChangedListener);
        startDateAndTime.setOnClickListener(v -> showDateTimePicker(true));
        endDateAndTime.setOnClickListener(v -> showDateTimePicker(false));
        btnCreate.setOnClickListener(v->{
            mViewModel.create();
            Intent switchActivityIntent = new Intent(requireActivity(), MainActivity.class);
            startActivity(switchActivityIntent);
        });
        return root;
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
                String date = weekDayShortName + ", " + monthFullName + " " + day;
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
}