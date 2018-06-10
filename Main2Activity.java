package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    protected EditText editText;
    public String newnote;
    private int phash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        phash = intent.getIntExtra("phash", 0);

        //设置回复帖子
        TextView tv = findViewById(R.id.回复);
        if(phash == 0)
            tv.setText("发表新帖");
        else if(phash == -1)
            tv.setText("反馈");
        else
            tv.setText("回复帖子：<"+phash+">");

        findViewById(R.id.发布).setOnClickListener(Main2Activity.this);
        editText = findViewById(R.id.发帖内容);
    }
    //设置按钮事件
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.发布:
                newnote = editText.getText().toString();
                Post post;
                if(phash != 0){
                    post = new Post(Transmission.getNetTime(), newnote, phash);
                }
                else{
                    post = new Post(Transmission.getNetTime(), newnote);
                }
                Transmission.floodfill(post.toString());
                finish();
                //Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                //startActivity(intent);
                break;
        }
    }
}
