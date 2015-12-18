package org.glucosio.android.service;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by paolo on 16/12/15.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
            if("/GLUCOSIO_READING_WEAR".equals(messageEvent.getPath())) {
/*                DatabaseHandler dB = new DatabaseHandler(this);
                GlucoseReading gReading = new GlucoseReading(finalReading, type, finalDateTime, "");
                dB.addGlucoseReading(gReading);*/
            }
    }
}