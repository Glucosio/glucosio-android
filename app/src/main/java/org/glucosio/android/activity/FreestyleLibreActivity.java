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

package org.glucosio.android.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.object.PredictionData;
import org.glucosio.android.object.ReadingData;
import org.glucosio.android.tools.AlgorithmUtil;
import org.glucosio.android.tools.AnimationTools;

import java.io.IOException;
import java.util.Arrays;

public class FreestyleLibreActivity extends Activity {

    private static final String TAG = "FreestyleLibreActivity";

    private NfcAdapter mNfcAdapter;
    private TextView readingTextView;
    private User user;

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private ReadingData mResult = new ReadingData(PredictionData.Result.ERROR_NO_NFC);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freestyle_libre);

        final GlucosioApplication app = (GlucosioApplication) getApplicationContext();
        DatabaseHandler dB = app.getDBHandler();
        user = dB.getUser(1);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        readingTextView = (TextView) findViewById(R.id.activity_freestyle_textview_reading);
        Button saveButton = (Button) findViewById(R.id.activity_freestyle_button_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddGlucoseActivity();
            }
        });

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, R.string.freestylelibre_nfc_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, R.string.freestylelibre_nfc_not_enabled, Toast.LENGTH_LONG).show();
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Log.d("glucosio", "NfcAdapter.ACTION_TECH_DISCOVERED");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            new NfcVReaderTask().execute(tag);
        }
    }

    private void showReadingLayout() {
/*        SimpleDatabase database = new SimpleDatabase(this);
        long id = database.saveMessage(mResult);
        ReadingData.TransferObject transferObject = new ReadingData.TransferObject(id, mResult);
        database.close();
        WearableApi.sendMessage(mGoogleApiClient, WearableApi.GLUCOSE, new Gson().toJson(transferObject), mMessageListener);
        mMessagesBeingSent++;
        mFinishAfterSentMessages = true;*/

        // Apply values in TextViews
        readingTextView.setText(mResult.trend.get(0).glucose(user.getPreferred_unit().equals("mmol/L")));

        new Runnable() {
            @Override
            public void run() {
                View view = findViewById(R.id.activity_freestyle_reading);
                view.setVisibility(View.INVISIBLE);
                AnimationTools.startCircularReveal(view);
            }
        }.run();
        // Reveal ReadingLayout

    }

    private void openAddGlucoseActivity() {
        DatabaseHandler dB = new DatabaseHandler(getApplicationContext());
        if (dB.getUser(1) != null) {
            // Start AddGlucose Activity passing the reading value
            Intent intent = new Intent(getApplicationContext(), AddGlucoseActivity.class);
            Bundle bundle = new Bundle();
            String currentGlucose = mResult.trend.get(0).glucose(user.getPreferred_unit().equals("mmol/L"));
            bundle.putString("reading", currentGlucose + "");
            intent.putExtras(bundle);
            startActivity(intent);
            FreestyleLibreActivity.this.finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), HelloActivity.class);
            startActivity(intent);
        }
    }

    private class NfcVReaderTask extends AsyncTask<Tag, Void, Tag> {

        private byte[] data = new byte[360];

        @Override
        protected void onPostExecute(Tag tag) {
            if (tag == null) return;
            String tagId = bytesToHexString(tag.getId());
            int attempt = 1;
            mResult = AlgorithmUtil.parseData(attempt, tagId, data);
            showReadingLayout();
        }

        @Override
        protected Tag doInBackground(Tag... params) {
            Tag tag = params[0];
            NfcV nfcvTag = NfcV.get(tag);
            try {
                nfcvTag.connect();
                final byte[] uid = tag.getId();
                for (int i = 0; i <= 40; i++) {
                    byte[] cmd = new byte[]{0x60, 0x20, 0, 0, 0, 0, 0, 0, 0, 0, (byte) i, 0};
                    System.arraycopy(uid, 0, cmd, 2, 8);
                    byte[] oneBlock;
                    Long time = System.currentTimeMillis();
                    while (true) {
                        try {
                            oneBlock = nfcvTag.transceive(cmd);
                            break;
                        } catch (IOException e) {
                            if ((System.currentTimeMillis() > time + 2000)) {
                                Log.e(TAG, "tag read timeout");
                                return null;
                            }
                        }
                    }

                    oneBlock = Arrays.copyOfRange(oneBlock, 2, oneBlock.length);
                    System.arraycopy(oneBlock, 0, data, i * 8, 8);
                }

            } catch (Exception e) {
                Log.i(TAG, e.toString());
                return null;
            } finally {
                try {
                    nfcvTag.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing tag!");
                }
            }

            return tag;
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }

        char[] buffer = new char[2];
        for (byte b : src) {
            buffer[0] = Character.forDigit((b >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(b & 0x0F, 16);
            builder.append(buffer);
        }

        return builder.toString();
    }
}
