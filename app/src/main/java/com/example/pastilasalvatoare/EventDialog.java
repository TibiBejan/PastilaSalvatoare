package com.example.pastilasalvatoare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;


public class EventDialog extends AppCompatDialogFragment {

    // Class State
    private EditText editTextEventTile;
    private EditText editTextEventDescription;
    private DatePicker datePickerStartDate;
    private DatePicker datePickerEndDate;
    private TimePicker timePickerTime;
    private EventDialogListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Define dialog and uitlity methods
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_event, null);

        builder.setView(view).setTitle("Create your event for medication").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Set State data on submit button click
                String title = editTextEventTile.getText().toString();
                String description = editTextEventDescription.getText().toString();

                // Obtain a String representation of event start date
                String startDateDay = String.valueOf(datePickerStartDate.getDayOfMonth());
                String startDateMonth = String.valueOf(datePickerStartDate.getMonth() + 1);
                String startDateYear = String.valueOf(datePickerStartDate.getYear());
                String startDate = startDateDay + "-" + startDateMonth + "-" + startDateYear;


                // Obtain a String representation of event end date
                String endDateDay = String.valueOf(datePickerEndDate.getDayOfMonth());
                String endDateMonth = String.valueOf(datePickerEndDate.getMonth() + 1);
                String endDateYear = String.valueOf(datePickerEndDate.getYear());
                String endDate = endDateDay + "-" + endDateMonth + "-" + endDateYear;


                // Obtain a String representation of time from timePicker
                String hourOfDay = String.valueOf(timePickerTime.getHour());
                String minutesOfDay = String.valueOf(timePickerTime.getMinute());
                String eventTime = hourOfDay + ":" + minutesOfDay;

                // Send user input to main activity using the created listener
                listener.generateEvents(title, description, startDate, endDate, eventTime);
            }
        });


        // Get dialog inputs value
        editTextEventTile = view.findViewById(R.id.eventTitle);
        editTextEventDescription = view.findViewById(R.id.eventDescription);
        timePickerTime = view.findViewById(R.id.eventTime);
        datePickerStartDate = view.findViewById(R.id.eventStartDate);
        datePickerEndDate = view.findViewById(R.id.eventEndDate);


        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EventDialogListener) context;
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + "must implement EventDialogListener");
        }
    }

    public interface EventDialogListener {
        void generateEvents(String title, String description, String startDate, String endDate, String eventTime);
    }
}
