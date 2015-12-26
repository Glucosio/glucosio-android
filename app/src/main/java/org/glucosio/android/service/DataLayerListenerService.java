package org.glucosio.android.service;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
            if("/GLUCOSIO_READING_WEAR".equals(messageEvent.getPath())) {

                // Convert data to String
                String finalString = new String(messageEvent.getData(), StandardCharsets.UTF_8);

                // Split string in glucose value and reading type
                String[] finalArray = finalString.split(", ");
                Log.e("glucosio", "received " + finalArray[0] + " and " + finalArray [1]);

                Toast.makeText(getApplicationContext(), finalArray[1], Toast.LENGTH_SHORT).show();

                /*DatabaseHandler dB = new DatabaseHandler(this);
                GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime, "");
                dB.addGlucoseReading(gReading);*/
            }
    }
}