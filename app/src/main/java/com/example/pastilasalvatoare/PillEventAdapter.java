package com.example.pastilasalvatoare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PillEventAdapter extends ArrayAdapter<PillEvent> {
    private ArrayList<PillEvent> data = null;

    public PillEventAdapter(@NonNull Context context, @NonNull ArrayList<PillEvent> objects) {
        super(context, 0, objects);
        this.data = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.pill_event_list_item, parent, false);
        }

        PillEvent currentEventPosition = getItem(position);

        TextView textViewTitle = currentItemView.findViewById(R.id.listItemTile);
        TextView textViewDescription = currentItemView.findViewById(R.id.listItemDescription);
        TextView textViewDate = currentItemView.findViewById(R.id.listItemDate);
        TextView textViewTime = currentItemView.findViewById(R.id.listItemTime);


        textViewTitle.setText("Event Title: " + currentEventPosition.getTitle());
        textViewDescription.setText("Event Description: " + currentEventPosition.getDescription());
        textViewDate.setText("Event Date: " + currentEventPosition.getDate());
        textViewTime.setText("Event Schedule Time: " + currentEventPosition.getTime());


        // Add event listener to delete button from list item
        currentItemView.findViewById(R.id.listItemDeleteBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.removeListItem(position);
            }
        });


        // Add event listener to send text message from list item
        currentItemView.findViewById(R.id.listItemSendMessageBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    MainActivity.sendTextMessage(
                        currentEventPosition.getTitle(),
                        currentEventPosition.getDescription(),
                        currentEventPosition.getDate(),
                        currentEventPosition.getTime()
                    );
                }
            }
        });



        return currentItemView;
    }
}
