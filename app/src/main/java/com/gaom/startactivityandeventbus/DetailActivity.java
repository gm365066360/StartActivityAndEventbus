package com.gaom.startactivityandeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.api.EventParam;

public class DetailActivity extends AppCompatActivity {

    @EventParam
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.e("gaom", name );

    }

    public void go__back(View view) {
        DetailActivity$$EventBean.postBack("123123back");
        DetailActivity$$EventBean.postBack(true);
//        finish();
    }
}
