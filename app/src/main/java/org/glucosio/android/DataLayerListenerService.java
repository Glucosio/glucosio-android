package org.glucosio.android;

/**
 * Created by paolo on 16/12/15.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if("/MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
        }
    }
}