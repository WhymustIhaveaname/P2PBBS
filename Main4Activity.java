package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Main4Activity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        findViewById(R.id.返回).setOnClickListener(Main4Activity.this);
        findViewById(R.id.重新接入).setOnClickListener(Main4Activity.this);
        findViewById(R.id.空间清理).setOnClickListener(Main4Activity.this);
        findViewById(R.id.反馈).setOnClickListener(Main4Activity.this);
        findViewById(R.id.关于我们).setOnClickListener(Main4Activity.this);
    }

    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.返回:
                finish();
                //Intent intent = new Intent(Main4Activity.this,MainActivity.class);
                //startActivity(intent);
                break;
            case R.id.重新接入:
                Intent intent2 = new Intent(Main4Activity.this,Main3Activity.class);
                startActivity(intent2);
                break;
            case R.id.空间清理:
                break;
            case R.id.反馈:
                Intent intent4 = new Intent(Main4Activity.this,Main2Activity.class);
                startActivity(intent4);
                break;
            case R.id.关于我们:
                break;
        }
    }
}
