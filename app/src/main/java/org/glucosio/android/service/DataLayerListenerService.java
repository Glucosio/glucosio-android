package org.glucosio.android.service;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if("/GLUCOSIO_READING_WEAR".equals(messageEvent.getPath())) {

                // Convert data to String
            String finalString = null;
            try {
                finalString = new String(messageEvent.getData(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Split string in glucose value and reading type
            String[] finalArray = finalString.split(", ");
            Log.i(getPackageName(), "New reading from wear " + finalArray[0] + ", " + finalArray [1]);

            showNotification(finalArray[0], finalArray[1]);
            addToDatabase(finalArray[0], finalArray[1]);

        }
    }

    private void addToDatabase(String reading, String readingType){
        int glucoseValue = Integer.parseInt(reading);
        Calendar cal = Calendar.getInstance();
        DatabaseHandler dB = new DatabaseHandler(this);
        GlucoseReading gReading = new GlucoseReading(glucoseValue, readingType, cal.getTime(), "");
        dB.addGlucoseReading(gReading);
    }

    private void showNotification(String reading, String readingType){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_glucosio)
                        .setContentTitle(getResources().getString(R.string.wear_new_reading))
                        .setColor(getResources().getColor(R.color.glucosio_pink))
                        .setContentText(reading + ", " + readingType);
        mBuilder.build();

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}