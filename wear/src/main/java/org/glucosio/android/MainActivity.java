package org.glucosio.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener, WearableListView.ClickListener{

    private static final int SPEECH_REQUEST_CODE = 0;
    private GoogleApiClient mGoogleApiClient;
    private String spokenText;
    private DelayedConfirmationView mDelayedView;
    private String[] typeArray;
    private FrameLayout listFrame;
    private FrameLayout confirmFrame;
    private TextView confirmTextView;
    private String finalString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeArray = getResources().getStringArray(R.array.dialog_add_measured_list);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

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

    @Override
    public void onTimerFinished(View view) {
        onDoneButtonPressed(view);
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
            // Send text to phone

        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onDoneButtonPressed(View target) {
        if (mGoogleApiClient == null) {
            return;
        }

        final PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                final List<Node> nodes = result.getNodes();
                if (nodes != null) {
                    for (int i=0; i<nodes.size(); i++) {
                        final Node node = nodes.get(i);

                        // Send glucose reading to phone
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/GLUCOSIO_READING_WEAR", finalString.getBytes());
                        Log.e("wear", "sent to phone");
                        finish();
                    }
                }
            }
        });
    }
}
