package com.gaom.startactivityandeventbus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void go__detail(View view) {
        DetailActivity$$EventBean
                .builder()
                .setName("gaom1")
                .create()
                .postForResult(
                        this,
                        new DetailActivity$$EventBean.Callback<String>() {
                            @Override
                            public void onResult() {
                                Log.e("gaom", it);
                            }
                        });
    }

    public void go__Activity(View view) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("123123a");
        strings.add("123123b");
        strings.add("123123c");


        /**
         * 痛点:
         * 1.key 定义   => intent.putExtra("str","gaom123");
         * 2.getIntent 取值  =>  String str = getIntent().getStringExtra("str");
         * 3.数据bean需要实现序列化接口 =>intent.putExtra("data",new BActivity.Data("gaom123"));
         * 4.new Intent => new Intent(this,AActivity.class);
         */

        //你就瞅瞅麻烦不麻烦
        Intent intent = new Intent(this, BActivity.class);
//        intent.putExtra("data",new BActivity.Data("gaom123")); //需要实现序列化接口
        intent.putExtra("str", "gaom123");
        intent.putExtra("b", true);
//        startActivity(intent);

        //你就瞅瞅麻烦不麻烦
        String str = getIntent().getStringExtra("str");
        boolean b = getIntent().getBooleanExtra("b", false);


        //优雅
        BActivity$$EventBean
                .builder()
                .setData(new BActivity.Data("gaom123"))
                .setAFloat(1f)
                .setB1(true)
                .setInt1(11)
                .setS1("111")
                .setStringArrayList(strings)
                .create()
                .postForResult(this, new BActivity$$EventBean.Callback<Intent>() {
                    @Override
                    public void onResult() {
                        Log.e("gaom onResult= ", it.getStringExtra("userName"));
                        Log.e("gaom onResult= ", it.getIntExtra("age", -1) + "");
                    }


                });

        //优雅
        BActivity$$EventBean
                .builder()
                .setData(new BActivity.Data("gaom123"))
                .setAFloat(1f)
                .setB1(true)
                .setInt1(11)
                .setS1("111")
                .setStringArrayList(strings)
                .create()
                .postForResult(this, new BActivity$$EventBean.Callback<Boolean>() {
                    @Override
                    public void onResult() {
                        Log.e("gaom onResult= ", it.toString());
                    }

                });
    }


}
