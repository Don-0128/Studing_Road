package com.example.navitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InitialPageActivity extends AppCompatActivity {

    private TextView mTvTitle,mTvNote;
    private Button mBtnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);
        mTvTitle = findViewById(R.id.tv_page_title);
        mTvNote = findViewById(R.id.tv_page_note);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/HWxingkai.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(),"fonts/MNJlishu.ttf");
        mTvTitle.setTypeface(typeface);
        mTvNote.setTypeface(typeface);
        //mBtnEnter.setTypeface(typeface1);
        mBtnEnter = findViewById(R.id.btn_enter);

        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialPageActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
}