package com.example.pastilasalvatoare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements EventDialog.EventDialogListener {

    // Class State Definition
    CalendarView calendarView;
    Calendar calendar = Calendar.getInstance();
    ListView listView;



    // Event ListArray definition
    static ArrayList<PillEvent> events = new ArrayList<>();
    static ArrayList<PillEvent> filteredEvents = new ArrayList<>();


    // Event Adapter
    static PillEventAdapter pillEventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a ref to the calendarView component from activity_main
        calendarView = findViewById(R.id.calendarView);

        // Create a ref to the ListView component from activity_main
        listView = findViewById(R.id.pillEventsListView);

        // Get date on renders
        getDate();


        // Create a PillEventAdapter instance
        pillEventAdapter = new PillEventAdapter(MainActivity.this, filteredEvents);
        listView.setAdapter(pillEventAdapter);



        // TEMP
        // Get filtered Pill Events and pass them to array adapter
        setFilterPillEvents();
        pillEventAdapter.notifyDataSetChanged();



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Toast.makeText(MainActivity.this, dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

                // Set calendar date on date change event
                setDate(dayOfMonth, month, year);

                // Get filtered Pill Events and pass them to array adapter
                setFilterPillEvents();
                pillEventAdapter.notifyDataSetChanged();

            }
        });


        // Create a ref for createPilEventBTN button and define onClick listener
        Button createPillEventButton = findViewById(R.id.createPilEventBTN);
        createPillEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pillEventAdapter.notifyDataSetChanged();
                openCreateEventDialog();
            }
        });
    }


    // Class method used to get calendar date
    public void getDate() {
        // Extract calendarView date
        long date = calendarView.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(date);

        // Extract current calendar date as string
        String selectedDate = simpleDateFormat.format(calendar.getTime());

        // Set Toast app notification with selected date
        Toast.makeText(MainActivity.this, selectedDate, Toast.LENGTH_LONG).show();
    }


    // Class method used to set calendar date
    public void setDate(int day, int month, int year) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);
    }


    public void openCreateEventDialog() {
        EventDialog eventDialog = new EventDialog();
        eventDialog.show(getSupportFragmentManager(), "Event Dialog");
    }


    @Override
    public void generateEvents(String title, String description, String startDate, String endDate, String eventTime) {
        try {
            // Generate array of dates between medication startDate and endDate
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-M-yyyy");

            LocalDate localStartDate = LocalDate.parse(startDate, dateFormat);
            LocalDate localEndDate = LocalDate.parse(endDate, dateFormat);

            ArrayList<LocalDate> allDates = new ArrayList<>();

            if (localStartDate.isAfter(localEndDate)) {
                throw new IllegalStateException("start date must be before or equal to end date");
            }

            while (!localStartDate.isAfter(localEndDate)) {
                allDates.add(localStartDate);
                localStartDate = localStartDate.plusDays(1);
            }
            // Based on allDates array, create ArrayList of medication events
            for (LocalDate d : allDates) {
                events.add(new PillEvent(
                        title,
                        description,
                        d,
                        eventTime
                ));
            }
            setFilterPillEvents();
            pillEventAdapter.notifyDataSetChanged();
        }
        catch (IllegalStateException e) {
            System.out.println(e);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }



    public void setFilterPillEvents() {

        // Define Date formatter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        // Extract calendarView date as string
        long date = calendarView.getDate();
        String selectedDate = simpleDateFormat.format(calendar.getTime());


        // Empty Array List and populate it with current date pill events
        filteredEvents.removeAll(filteredEvents);

        for(PillEvent e: events) {
            if (e.date.toString().equals(selectedDate)) {
                filteredEvents.add(e);
            }
        }
    }



    public static void removeListItem(int listItemIndex) {
        filteredEvents.remove(listItemIndex);
        events.remove(listItemIndex);
        pillEventAdapter.notifyDataSetChanged();
    }



    public static void sendTextMessage(String title, String description, String date, String time) {
        String textMessage = "Reminder: " + title + ". " + description + ". You have to take the pill on: " + date + ", at: " + time;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("1-555-123-4567", null, textMessage, null, null);
    }
}