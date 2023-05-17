package com.example.pastilasalvatoare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


public class CalendarEventAdapter extends ArrayAdapter<CalendarEvent> {

    private ArrayList<CalendarEvent> data = null;
    private Context mContext = null;

    public CalendarEventAdapter(@NonNull Context context, @NonNull ArrayList<CalendarEvent> objects) {
        super(context, 0, objects);
        this.data = objects;
        this.mContext = context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.pill_event_list_item, parent, false);
        }

        CalendarEvent currentEventPosition = getItem(position);

        TextView textViewTitle = currentItemView.findViewById(R.id.listItemTile);
        TextView textViewDescription = currentItemView.findViewById(R.id.listItemDescription);
        TextView textViewOrganizer = currentItemView.findViewById(R.id.listItemOrganizer);
        TextView textViewID = currentItemView.findViewById(R.id.listItemID);
        TextView textViewStartDate = currentItemView.findViewById(R.id.listItemStartDate);
        TextView textViewEndDate = currentItemView.findViewById(R.id.listItemEndDate);


        textViewTitle.setText("Event Title: " + currentEventPosition.getTitle());
        textViewDescription.setText("Event Description: " + currentEventPosition.getDescription());
        textViewOrganizer.setText("Event Organizer: " + currentEventPosition.getOrganizer());
        textViewID.setText("Event ID: " + currentEventPosition.getId());
        textViewStartDate.setText("Event Start Date: " + currentEventPosition.getStartDate());
        textViewEndDate.setText("Event End Date: " + currentEventPosition.getEndDate());


        // Add event listener to delete button from list item
        currentItemView.findViewById(R.id.listItemDeleteBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.removeCalendarEvent(mContext, data.get(position).getId());

            }
        });

        return currentItemView;
    }
}
