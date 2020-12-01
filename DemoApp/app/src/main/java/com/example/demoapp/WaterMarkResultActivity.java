package com.example.demoapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * @ProjectName: DemoApp
 * @Package: com.example.demoapp
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2019/12/28
 * @Version: 1.0
 */
public class WaterMarkResultActivity extends Activity {
    public static String KEY_DRAWABLE_BITES="KEY_DRAWABLE_BITES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark_result);
        byte[] drawableBytes = getIntent().getByteArrayExtra(KEY_DRAWABLE_BITES);
        ImageView imageView = findViewById(R.id.iv);
        if(null!=drawableBytes){
           Bitmap bitmap =  BitmapFactory.decodeByteArray(drawableBytes,0,drawableBytes.length);
            imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }
}
