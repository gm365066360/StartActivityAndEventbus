package com.gaom.startactivityandeventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.api.EventParam;

import java.util.ArrayList;
import java.util.Arrays;

public class BActivity extends AppCompatActivity {

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
    public  Data data;
    @EventParam
    public String s1;
    @EventParam
    public  int int1;
    @EventParam
    public  boolean b1;
    @EventParam
    public  float aFloat;
    @EventParam
    public ArrayList<String> stringArrayList;
    public void go__back(View view) {
//        setResult(100,new Intent().putExtra("back","gaom2"));
//        finish();
        BActivity$$EventBean.postBack( true );
        BActivity$$EventBean.postBack( 2 );
        BActivity$$EventBean.postBack( "3" );
        BActivity$$EventBean.postBack( .4 );
        BActivity$$EventBean.postBack( new Data("data") );

        BActivity$$EventBean.postBack(
                new Intent()
                .putExtra("userName","userName123")
                .putExtra("age",22)
        );
//        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        Log.e("gaom string1= ",data.string1);
        Log.e("gaom s1= ", s1);
        Log.e("gaom b1= ", b1+"");
        Log.e("gaom int1= ", int1+"");
        Log.e("gaom aFloat= ", aFloat+"");
        Log.e("gaom Arrays= ", Arrays.toString(stringArrayList.toArray()));

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("gaom = ","onStop");
        Log.e("gaom = ",data.string1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("gaom = ","onResume");
        Log.e("gaom = ",data.string1);

    }


}
