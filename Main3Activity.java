package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener{

    protected EditText editText;
    protected EditText editText2;
    public String myaddr, apaddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        findViewById(R.id.接入).setOnClickListener(Main3Activity.this);
        editText = findViewById(R.id.输入自己地址);
        editText2 = findViewById(R.id.输入接入地址);
    }
    //设置按钮事件
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.接入:
                //获取用户输入信息
                myaddr = editText.getText().toString();
                apaddr = editText2.getText().toString();
                Transmission.requestPeerList(new Peer(apaddr,null));
                Transmission.requestPost(new Peer(apaddr,null),0);
                finish();
                //Intent intent = new Intent(Main3Activity.this,MainActivity.class);
                //startActivity(intent);
                break;
        }
    }
}
