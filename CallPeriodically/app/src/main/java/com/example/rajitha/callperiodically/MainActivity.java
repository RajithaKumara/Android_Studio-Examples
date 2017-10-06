package com.example.rajitha.callperiodically;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;
    Button callButton;
    Button stopButton;
    ProgressBar progressBar;
    float count=0;
    int timeM;

    static final int REQUEST_PHONE_CALL = 1;
    static boolean callSwitch=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText) findViewById(R.id.editText);
        editText2=(EditText) findViewById(R.id.editText2);
        callButton=(Button) findViewById(R.id.button);
        stopButton=(Button) findViewById(R.id.button2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        callButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        callButton.setEnabled(false);
        editText2.setText("0");


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


    public void call(View view) {
        callSwitch=true;
        if (editText2.getText().toString().length()==0){
            editText2.setText("0");
            Toast.makeText(getApplicationContext(),"Interval cann't be empty", Toast.LENGTH_SHORT).show();
        }else{
            callF();
            callInterval();
            count=0;
            progressBar(0);
            //progressHandler();
        }

    }

    public void callF(){
        if (callSwitch){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editText.getText().toString()));
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(intent);
                }
            }
            else
            {
                startActivity(intent);
            }
        }
    }

    public void callInterval(){
        Handler handler=new Handler();

        int timeD= Integer.parseInt(editText2.getText().toString());
        if (callSwitch){
            if (timeD!=0) {
                stopButton.setVisibility(View.VISIBLE);
                callButton.setVisibility(View.INVISIBLE);
                editText.setEnabled(false);
                editText2.setEnabled(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callF();
                        count=0;
                        progressBar(0);
                        callInterval();
                    }
                }, timeD * 60000);
            }
        }
    }

    /*public void progressHandler(){
        Handler handler1=new Handler();

        int timeD= Integer.parseInt(editText2.getText().toString());
        if (callSwitch){
            if (timeD!=0) {
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count=0;
                        progressBar(0);
                        progressHandler();
                    }
                }, timeD * 60000);
            }
        }
    }*/
    static final int second=1000;
    public void progressBar(float i){
        Handler handler2=new Handler();

        timeM= Integer.parseInt(editText2.getText().toString());
        count=i;
        if (callSwitch){
            if (timeM!=0) {
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int) count/1);
                        progressBar(count = (float) (count +(10.0/(timeM*6.0))));
                    }
                }, second);
            }
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

    public void call_stop(View view) {
        callSwitch=false;
        callButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        editText.setEnabled(true);
        editText2.setEnabled(true);
        progressBar.setProgress(0);
    }
}
