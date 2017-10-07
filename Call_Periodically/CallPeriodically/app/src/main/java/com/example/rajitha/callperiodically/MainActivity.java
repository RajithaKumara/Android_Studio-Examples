package com.example.rajitha.callperiodically;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.support.design.widget.Snackbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;
    Button callButton;
    Button stopButton;
    ProgressBar progressBar;
    float count=0;
    int timeCount=1;
    int callCount=0;
    int timeDelay=0;
    TextView textTimer;
    TelephonyManager telephonyManager;

    static final int REQUEST_PHONE_CALL = 1;
    static boolean callSwitch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();

        editText=(EditText) findViewById(R.id.editText);
        editText2=(EditText) findViewById(R.id.editText2);
        callButton=(Button) findViewById(R.id.button);
        stopButton=(Button) findViewById(R.id.button2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        textTimer=(TextView) findViewById(R.id.textTimer);
        callButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        //textTimer.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        callButton.setEnabled(false);
        editText2.setText("0");

        telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                callButton.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                callButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().toString().length()==0){
                    callButton.setEnabled(false);
                }else{
                    callButton.setEnabled(true);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callSwitch) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }else{
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }
    View v;
    public void call(View view) { //"Call" button method calling
        callCount=0;
        timeCount=0;
        if (editText2.getText().toString().length()==0){
            editText2.setText("0");
            Toast.makeText(getApplicationContext(),"Interval can't be empty", Toast.LENGTH_SHORT).show();
        }else{
            callSwitch=true;
            timeDelay= Integer.parseInt(editText2.getText().toString());
            v=view;
            callF();
            if (timeDelay!=0){
                textTimer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                callInterval();
                count = 0;
                progressBar(0);
            }

        }

    }


    public void call_stop(View view) { // "Stop Call" button calling this method
            callSwitch = false;
            callButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
            editText.setEnabled(true);
            editText2.setEnabled(true);
            textTimer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            handlerCallInterval.removeCallbacks(mCallIntervalRunnable);
            handlerProgressBar.removeCallbacks(mProgressBarRunnable);
    }


    public void callF(){ //make single call with requesting permission if not
        int state=telephonyManager.getCallState();
        if (state == 0) {//CALL_STATE_IDLE
            if (callSwitch) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editText.getText().toString()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        callCount += 1;
//                        Toast.makeText(getApplicationContext(),"Calling...", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }
            }
        } else if (state == 2) {//CALL_STATE_OFFHOOK
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            call_stop(v);
            Snackbar.make(v, "Can't make call while another call is ongoing...", Snackbar.LENGTH_LONG).show();
        } else if (state == 1) {//CALL_STATE_RINGING
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            call_stop(v);
            Snackbar.make(v, "Can't make call while ringing...", Snackbar.LENGTH_LONG).show();
        }
    }
    Handler handlerCallInterval=new Handler();
    public void callInterval(){ //make call with given time periods after first call


        if (callSwitch){
            stopButton.setVisibility(View.VISIBLE);
            callButton.setVisibility(View.INVISIBLE);
            editText.setEnabled(false);
            editText2.setEnabled(false);
            handlerCallInterval.postDelayed(mCallIntervalRunnable, timeDelay * 60000);
        }
    }

    static final int second=1000;
    Handler handlerProgressBar=new Handler();
    public void progressBar(float i){ // process progress bar
        count=i;
        if (callSwitch){
            handlerProgressBar.postDelayed(mProgressBarRunnable, second);
        }else{
            progressBar.setProgress(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callF();
                }
                return;
            }
        }
    }

    private final Runnable mProgressBarRunnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress((int) count/1);
            count = (float) (count +(10.0/(timeDelay*6.0)));
            timeCount+=1;
            textTimer.setText("Call count: "+String.valueOf(callCount)+"\n"+"Time: "+String.valueOf(timeCount)+"s");
            if (callSwitch) {
                progressBar(count);
            }
        }
    };

    private final Runnable mCallIntervalRunnable = new Runnable() {
        @Override
        public void run() {
            callF();
            count=0;
            timeCount=0;
            if (callSwitch) {
                callInterval();
            }
        }
    };


}

/**sim state
 *
 * TelephonyManager.getSimState();
 *
 * SIM_STATE_UNKNOWN=0              |API Level 1
 * SIM_STATE_ABSENT=1               |API Level 1
 * SIM_STATE_PIN_REQUIRED=2         |API Level 1
 * SIM_STATE_PUK_REQUIRED=3         |API Level 1
 * SIM_STATE_NETWORK_LOCKED=4       |API Level 1
 * SIM_STATE_READY=5                |API Level 1
 * SIM_STATE_NOT_READY=6            |API Level 26
 * SIM_STATE_PERM_DISABLED=7        |API Level 26
 * SIM_STATE_CARD_IO_ERROR=8        |API Level 26
 * SIM_STATE_CARD_RESTRICTED=9      |API Level 26
 */