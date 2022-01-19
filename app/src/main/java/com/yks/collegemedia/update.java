package com.yks.collegemedia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class update extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        TextView notify=findViewById(R.id.notify);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
        //    String value2= extras.getString("key2");

            notify.setText(value);
            //The key argument here must match that used in the other activity
        }else {

        notify.setText("ERROR PLEASE TRY AGAIN LATER");
    }
    }

    @Override
    public void onBackPressed() {

      //  finishAndRemoveTask();
        finishAffinity();
        finish();
        System.exit(0);
    }
}