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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SelectTimesDialog extends DialogFragment {

    private DialogSelectTimesBinding binding;
    private SelectTimesDialog.MyDialogListener listener;
    private final EditText[] openingTimesEt = new EditText[7];
    private final EditText[] closingTimesEt = new EditText[7];
    private final CheckBox[] closedChb = new CheckBox[7];

    private final String[] openingTimes = new String[7];
    private final String[] closingTimes = new String[7];

    private CheckBox alwaysOpenChb;

    int hour;
    int minute;

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

        cancelBtn.setOnClickListener(v->this.dismiss());
        okBtn.setOnClickListener(v->{
            updateListener();
            this.dismiss();
        });
        for (int i = 0; i < 7; i++) {
            int index = i;
            openingTimesEt[i].setOnClickListener(v->{
                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                        (view1, hourOfDay, minute) -> {
                            if (openingTimes[index] != null){
                                Date time = convertToTime(openingTimes[index]);
                                Date selectedTime = convertToTime(String.format("%s:%s", hourOfDay, minute));
                                if (selectedTime.getTime() > time.getTime()) {
                                    Toast.makeText(requireContext(), R.string.end_must_be_after_start, Toast.LENGTH_SHORT).show();
                                }else{
                                    openingTimes[index] = String.format("%s:%s", hourOfDay, minute);
                                    openingTimesEt[index].setText(String.format("%s:%s", hourOfDay, minute));
                                    if (index == 0 && allNullExceptFirst(true)){
                                        setAllTimes(true);
                                    }
                                }
                            }else{
                                openingTimes[index] = String.format("%s:%s", hourOfDay, minute);
                                openingTimesEt[index].setText(String.format("%s:%s", hourOfDay, minute));
                                if (index == 0 && allNullExceptFirst(true)){
                                    setAllTimes(true);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            });
            closingTimesEt[i].setOnClickListener(v->{
                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                        (view1, hourOfDay, minute) -> {
                            if (openingTimes[index] != null){
                                Date time = convertToTime(openingTimes[index]);
                                Date selectedTime = convertToTime(String.format("%s:%s", hourOfDay, minute));
                                if (selectedTime.getTime() < time.getTime()) {
                                    Toast.makeText(requireContext(), R.string.end_must_be_after_start, Toast.LENGTH_SHORT).show();
                                }else{
                                    closingTimes[index] = String.format("%s:%s", hourOfDay, minute);
                                    closingTimesEt[index].setText(String.format("%s:%s", hourOfDay, minute));
                                    if (index == 0 && allNullExceptFirst(false)){
                                        setAllTimes(false);
                                    }
                                }
                            }else{
                                closingTimes[index] = String.format("%s:%s", hourOfDay, minute);
                                closingTimesEt[index].setText(String.format("%s:%s", hourOfDay, minute));
                                if (index == 0 && allNullExceptFirst(false)){
                                    setAllTimes(false);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            });
            closedChb[i].setOnCheckedChangeListener((v, isChecked) ->{
                if (isChecked){
                    openingTimes[index] = "0:0";
                    closingTimes[index] = "0:0";
                    openingTimesEt[index].setEnabled(false);
                    closingTimesEt[index].setEnabled(false);
                }else{
                    openingTimes[index] = String.valueOf(openingTimesEt[index].getText());
                    closingTimes[index] = String.valueOf(closingTimesEt[index].getText());
                    openingTimesEt[index].setEnabled(true);
                    closingTimesEt[index].setEnabled(true);
                }
            });
        }
        alwaysOpenChb.setOnCheckedChangeListener((v, isChecked)->{
            if (isChecked) {
                for (int i = 0; i < 7; i++) {
                    openingTimesEt[i].setEnabled(false);
                    closingTimesEt[i].setEnabled(false);
                    closedChb[i].setEnabled(false);
                }
            }else{
                for (int i = 0; i < 7; i++) {
                    openingTimesEt[i].setEnabled(true);
                    closingTimesEt[i].setEnabled(true);
                    closedChb[i].setEnabled(true);
                }
            }
        });
    }

    private Date convertToTime(String TimeString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(TimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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
        void onDialogResult(String[] openingTimes, String[] closingTimes, boolean isAlwaysOpen);
    }

    public void setListener(SelectTimesDialog.MyDialogListener listener) {
        this.listener = listener;
    }

    private void updateListener(){
        listener.onDialogResult(openingTimes, closingTimes, alwaysOpenChb.isChecked());
    }

    private boolean allNullExceptFirst(boolean opening) {
        boolean allNullExceptFirst = true;
        for (int i = 1; i < 7; i++) {
            if ((opening && openingTimes[i] != null) || (!opening && closingTimes[i] != null)) {
                allNullExceptFirst = false;
                break;
            }
        }
        return  allNullExceptFirst;
    }

    private void setAllTimes(boolean opening) {
        for (int i = 1; i < 7; i++) {
            if (opening){
                openingTimes[i] = openingTimes[0];
                openingTimesEt[i].setText(openingTimes[i]);
            }else{
                closingTimes[i] = closingTimes[0];
                closingTimesEt[i].setText(closingTimes[i]);
            }
        }
    }
}
