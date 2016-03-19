/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener, WearableListView.ClickListener {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final long CONNECTION_TIME_OUT_MS = 10000;
    private String spokenText;
    private DelayedConfirmationView mDelayedView;
    private String[] typeArray;
    private FrameLayout listFrame;
    private FrameLayout confirmFrame;
    private TextView confirmTextView;
    private String finalString;
    private GoogleApiClient client;
    private String nodeId;

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeArray = getResources().getStringArray(R.array.dialog_add_measured_list);

        initApi();
        displaySpeechRecognizer();

        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.reading_type_list);
        mDelayedView =
                (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        mDelayedView.setListener(this);
        listFrame = (FrameLayout) findViewById(R.id.list_frame);
        confirmFrame = (FrameLayout) findViewById(R.id.confirm_frame);
        confirmTextView = (TextView) findViewById(R.id.confirm_textview);

        // Assign an adapter to the list
        listView.setAdapter(new Adapter(this, typeArray));

        // Set a click listener
        listView.setClickListener(this);
    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     *
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendMessage() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, "/GLUCOSIO_READING_WEAR", finalString.getBytes());
                    Log.i(getPackageName(), "New reading sent to phone");
                    finish();
                    client.disconnect();
                }
            }).start();
            // Show Success ConfirmationActivity
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.wear_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimerFinished(View view) {
        sendMessage();
    }

    @Override
    public void onTimerSelected(View view) {
        finish();
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        // use this data to complete some action ...
        String type = typeArray[tag];


        // Show confirm dialog
        listFrame.setVisibility(View.GONE);
        confirmFrame.setVisibility(View.VISIBLE);

        finalString = spokenText + ", " + type;
        confirmTextView.setText(finalString);

        // Two seconds to cancel the action
        mDelayedView.setTotalTimeMs(2000);
        // Start the timer
        mDelayedView.start();
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            if (!isNumeric(spokenText)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.reading_invalid), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
