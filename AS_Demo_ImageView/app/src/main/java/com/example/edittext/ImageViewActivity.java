package com.example.edittext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView mIv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        mIv3 =  findViewById(R.id.iv_3);
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1607326751820&di=e4b03d25bcf18574b21c01b2f5fd3416&imgtype=0&src=http%3A%2F%2Fwww.ynspdk.com%2Fzb_users%2Fupload%2F2020%2F03%2F202003161584348597156853.png").into(mIv3);

    }
}