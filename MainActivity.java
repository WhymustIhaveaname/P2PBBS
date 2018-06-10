package com.example.administrator.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Note> notelist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNotes();
        NoteAdapter adapter = new NoteAdapter(MainActivity.this, R.layout.note_item, notelist);
        ListView listView = (ListView) findViewById(R.id.浏览帖子);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                Note note = notelist.get(position);
                intent.putExtra("phash", note.getHash());
                startActivity(intent);
            }
        });

        if(DataBase.checkDB() == 1){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("首次接入");
            dialog.setCancelable(false);
            dialog.setMessage("Welcome!");
            dialog.setPositiveButton("接入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this,Main3Activity.class);
                    startActivity(intent);
                }
            });
            dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }

        Transmission.onCreate();

        findViewById(R.id.发帖).setOnClickListener(MainActivity.this);
        findViewById(R.id.与我相关).setOnClickListener(MainActivity.this);
        findViewById(R.id.设置).setOnClickListener(MainActivity.this);
        findViewById(R.id.刷新).setOnClickListener(MainActivity.this);
    }

    private void initNotes(){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s = "SELECT * FROM POST ORDER BY TIME DESC;";
            PreparedStatement preStat = conn.prepareStatement(s);
            ResultSet rs = preStat.executeQuery();
            for (int i = 0; i < 50 && rs.next(); i++) {
                Note notei = new Note(rs.getInt("HASH"), rs.getString("CONTENT"));
                notelist.add(notei);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //设置按钮事件
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.发帖:
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                break;
            case R.id.与我相关:
                break;
            case R.id.设置:
                Intent intent3 = new Intent(MainActivity.this,Main4Activity.class);
                startActivity(intent3);
                break;
            case R.id.刷新:
                finish();
                Intent intent4 = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent4);
                break;
        }
    }
}
