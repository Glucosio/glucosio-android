package org.glucosio.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import org.glucosio.android.R;
import org.glucosio.android.tools.TimeElapsed;

import java.util.Calendar;

/**
 * Created by Viney Ugave on 10/25/16.
 */
public class ParentActivity extends AppCompatActivity {

    private static final String TAG  = "ParentActivity";
    private static TimeElapsed timeElapsedInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeElapsedInstance = TimeElapsed.getInstance();
        timeElapsedInstance.resetTime();
        timeElapsedInstance.setStartTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check if app has passed a time threshold
        if(timeElapsedInstance.getStartTime() != 0){
            timeElapsedInstance.setEndTime(Calendar.getInstance().getTimeInMillis());
            long threshold = timeElapsedInstance.getEndTime()-timeElapsedInstance.getStartTime();
            Log.d(TAG,"Threshold : "+threshold);
            //Current timeout threshold set to 30s
            if(threshold>30000){
                Intent intent = new Intent(this, AppLockActivity.class);
                startActivity(intent);
            }else{
            }
        }
    }
}
