package com.example.pastilasalvatoare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class TextMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Extract intent data
        String messageTitle = intent.getStringExtra("MessageTitle");

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("15551234567", null, "Reminder pastila: " + messageTitle + ".Pacientul nu si-a luat pastila la timp!", null, null);

    }
}