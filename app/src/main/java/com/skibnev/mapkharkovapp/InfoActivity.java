package com.skibnev.mapkharkovapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class InfoActivity extends Activity {


    TextView tvLargeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //tvFrom=(TextView)findViewById(R.id.textView3);
        //tvTo=(TextView)findViewById(R.id.textView4);
        tvLargeInfo=(TextView)findViewById(R.id.textView6);

        //tvFrom.setText(getIntent().getStringExtra("from"));
        //tvTo.setText(getIntent().getStringExtra("to"));
        tvLargeInfo.setText(getIntent().getStringExtra("inf"));
    }

}
