package com.example.pastilasalvatoare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements EventDialog.EventDialogListener {

    // Calendar permissions request var
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;
    private static final int REQUEST_CODE = 1;

    // Calendar Provider State
    CalendarProvider calendarProviderAccount;
    CalendarEventAdapter calendarEventAdapter;
    CalendarView calendarView;
    Calendar calendar = Calendar.getInstance();
    static ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a ref to the calendarView component from activity_main
        calendarView = findViewById(R.id.calendarView);
        // Create a ref to the ListView component from activity_main
        listView = findViewById(R.id.pillEventsListView);
        // Create a CalendarEventAdapter instance
        calendarEventAdapter = new CalendarEventAdapter(MainActivity.this, calendarEvents);
        listView.setAdapter(calendarEventAdapter);
        // Get date on renders
        String calendarCurrentDate = getDate();
        readCalendarsByAccount();
        readEvents(calendarCurrentDate);
        calendarEventAdapter.notifyDataSetChanged();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(MainActivity.this, dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                // Set calendar date on date change event
                setDate(dayOfMonth, month, year);
                // Get date on renders
                String calendarCurrentDate = getDate();
                readEvents(calendarCurrentDate);
                calendarEventAdapter.notifyDataSetChanged();
            }
        });
        // Create a ref for createPilEventBTN button and define onClick listener
        Button createPillEventButton = findViewById(R.id.createPilEventBTN);
        createPillEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateEventDialog();
            }
        });
    }


    // Class method used to get calendar date
    public String getDate() {
        // Extract calendarView date
        long date = calendarView.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar.setTimeInMillis(date);

        // Extract current calendar date as string
        String selectedDate = simpleDateFormat.format(calendar.getTime());

        // Set Toast app notification with selected date
        Toast.makeText(MainActivity.this, selectedDate, Toast.LENGTH_LONG).show();


        return selectedDate;
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


    // ---------------------------- CALENDAR PROVIDER METHODS ---------------------------- //
    // Class method to fetch all calendar istances
    public void getCalendars() {
        // Request runtime permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, 1);
            return;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        ContentResolver contentResolver = getContentResolver();
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);

        ArrayList<String> calendarInfos = new ArrayList<>();

        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
            calendarInfos.add(calendarInfo);

            for (String c : calendarInfos) {
                System.out.println(c);
            }
        }
    }


    // Class method used to fetch all calendar instances based on user profile - google account
    public void readCalendarsByAccount() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        // Filtering
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"proiect.pastila2023@gmail.com", "com.google", "proiect.pastila2023@gmail.com"};
        // Submit the query and get a Cursor object back.

        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;
            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            calendarProviderAccount = new CalendarProvider(calID, displayName, accountName, ownerName);
        }
    }


    // Class method to check if event with provided title already exists
    private boolean checkEventAlreadyExist(String eventTitle, LocalDate currentDate) {
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE          // 2
        };


        // Specify the date range you want to search for recurring event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(currentDate.getYear(), currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth(), 0, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(currentDate.getYear(), currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth(), 24, 0);
        long endMillis = endTime.getTimeInMillis();

        String selection = "((" + CalendarContract.Instances.TITLE + " = ?) AND ("
                + CalendarContract.Instances.DTSTART + " = ?) AND ("
                + CalendarContract.Instances.DTEND + " = ?))";
        String[] selectionArgs = new String[]{eventTitle, String.valueOf(startMillis), String.valueOf(endMillis)};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur = getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);

        return cur.getCount() > 0;
    }


    // Class method used to create an event
    public void addEvent(String title, String description, String organizer, LocalDate currentDate, String eventTime) {
        if (checkEventAlreadyExist(title, currentDate)) {
            Toast.makeText(MainActivity.this, "Event already exists!", Toast.LENGTH_LONG).show();
            return;
        }
        // Specify the date range you want to search for recurring event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth(),
                Integer.parseInt(eventTime.split(":")[0]), Integer.parseInt(eventTime.split(":")[1]));
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth(),
                Integer.parseInt(eventTime.split(":")[0]) + 1, Integer.parseInt(eventTime.split(":")[1]));
        long endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarProviderAccount.calendarID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "GMT +2 Time");
        values.put(CalendarContract.Events.ORGANIZER, organizer);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());

            // Set reminder
            setReminder(cr, eventID, 15);

            // Send message
            sendMessage(startMillis, title);

            // Set alarm
            setAlarm(eventTime, title);

            Toast.makeText(MainActivity.this, "Event Created, the event id is: " + eventID, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }
    }


    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAlarm(String eventTime, String alarmTitle) {
        int hour = Integer.parseInt(eventTime.split(":")[0]);
        int minutes = Integer.parseInt(eventTime.split(":")[1]);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Reminder pastila: " + alarmTitle);

        startActivity(intent);
    }


    public void sendMessage(Long DateTime, String title) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, TextMessageReceiver.class);

            // Add arguments (data) to intent
            intent.putExtra("MessageTitle", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, DateTime, pendingIntent);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }
    }


    // Class method used to fetch all events based on provided date -- need parameters
    public void readEvents(String currentDate) {

        // Empty the array with current events
        calendarEvents.removeAll(calendarEvents);

        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.DTSTART,       // 1
                CalendarContract.Instances.DTEND,         // 2
                CalendarContract.Instances.TITLE,         // 3
                CalendarContract.Instances.DESCRIPTION,   // 4
                CalendarContract.Instances.ORGANIZER,     // 5
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_DTSTART_INDEX = 1;
        final int PROJECTION_DTEND_INDEX = 2;
        final int PROJECTION_TITLE_INDEX = 3;
        final int PROJECTION_DESCRIPTION_INDEX = 4;
        final int PROJECTION_ORGANIZER_INDEX = 5;

        // Extract info from string currentDate
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/M/yyyy");
        LocalDate localCurrentDate = LocalDate.parse(currentDate, dateFormat);

        // Specify the date range you want to search for recurring event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(localCurrentDate.getYear(), localCurrentDate.getMonthValue() - 1, localCurrentDate.getDayOfMonth(), 0, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(localCurrentDate.getYear(), localCurrentDate.getMonthValue() - 1, localCurrentDate.getDayOfMonth(), 23, 59);
        long endMillis = endTime.getTimeInMillis();

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur = getContentResolver().query(builder.build(), INSTANCE_PROJECTION, null, null, null);

        while (cur.moveToNext()) {

            // Get the field values
            long eventID = cur.getLong(PROJECTION_ID_INDEX);
            long startVal = cur.getLong(PROJECTION_DTSTART_INDEX);
            long endVal = cur.getLong(PROJECTION_DTEND_INDEX);
            String title = cur.getString(PROJECTION_TITLE_INDEX);
            String description = cur.getString(PROJECTION_DESCRIPTION_INDEX);
            String organizer = cur.getString(PROJECTION_ORGANIZER_INDEX);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            calendarEvents.add(new CalendarEvent(eventID, simpleDateFormat.format(startVal), simpleDateFormat.format(endVal), title, description, organizer));
        }
    }


    public void addCalendarEvent(String title, String description, String organizer, String date, String eventTime) {

        // Generate array of dates between medication startDate and endDate
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/M/yyyy");
        LocalDate localCurrentDate = LocalDate.parse(date, dateFormat);

        addEvent(title, description, organizer, localCurrentDate, eventTime);


        readEvents(date);
        calendarEventAdapter.notifyDataSetChanged();
    }


    static void removeCalendarEvent(Context context, long eventID) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
    }
    // ---------------------------- CALENDAR PROVIDER METHODS ---------------------------- //
}