package com.example.mydiary_ver6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import com.example.mydiary_ver6.WriteActivity;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private com.example.mydiary_ver6.RecyclerViewAdapter adapter;

    Button newD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");


        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        //layoutManager = new GridLayoutManager(this,2);
        layoutManager.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(this, new RecyclerViewAdapter.RecyclerViewAdapterEventListener() {        //recyclerViewAdapter 생성자에 현재 액티비티 넘김
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);  //클릭된 뷰 객체를 넘겨 position 값을 찾음

                Diary diary = adapter.getDiary(position);
                if(diary != null){
                    Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                    intent.putExtra("fileName", diary.fileName);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        newD = (Button)findViewById(R.id.new_d);
        newD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshDiaryList(); //리사이클러뷰 업데이트
    }
}