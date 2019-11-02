package com.gaom.startactivityandeventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.api.EventParam;

import java.util.ArrayList;
import java.util.Arrays;

public class CActivity extends AppCompatActivity {

    public static class Data{
        public Data(String string1) {
            this.string1 = string1;
        }

        public String string1;

        @Override
        public String toString() {
            return "Data{" +
                    "string1='" + string1 + '\'' +
                    '}';
        }
    }

    @EventParam
    public String name;

    public void go__back(View view) {
        CActivity$$EventBean.postBack("data1","data2",33 ,true ,new CActivity .Data("cData"));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        Log.e("gaom name= ",name);

    }


}
