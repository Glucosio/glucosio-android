package org.glucosio.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.tools.TimeElapsed;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for Application Lock using a PIN
 * @author Viney Ugave (viney@vinzzz.com)
 */
public class AppLockActivity extends AppCompatActivity {
    private static final String TAG="AppLockActivity";
    private static TimeElapsed timeElapsedInstance;

    @BindView(R.id.btnSubmit) Button submitButton;
    @BindView(R.id.editTextPinValue) EditText pinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.bind(this);
        timeElapsedInstance=TimeElapsed.getInstance();
        timeElapsedInstance.resetTime();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

    }

    @OnClick(R.id.btnSubmit) void onSubmit() {

        if(pinEditText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"PIN value can not be empty!",Toast.LENGTH_SHORT)
                    .show();

        }else{
            //check if PIN is correct then
            timeElapsedInstance.setStartTime();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        minimizeApp();
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
