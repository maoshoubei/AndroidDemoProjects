package com.example.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.demoapp.WaterMarkResultActivity.KEY_DRAWABLE_BITES;
import static com.example.demoapp.WatermarkUtil.WATER_MARK_GRAVITY_BOTTOM_LEFT;
import static com.example.demoapp.WatermarkUtil.WATER_MARK_GRAVITY_BOTTOM_RIGHT;
import static com.example.demoapp.WatermarkUtil.WATER_MARK_GRAVITY_MID_CENTER;
import static com.example.demoapp.WatermarkUtil.WATER_MARK_GRAVITY_TOP_LEFT;
import static com.example.demoapp.WatermarkUtil.WATER_MARK_GRAVITY_TOP_RIGHT;

/**
 * @ProjectName: DemoApp
 * @Package: com.example.demoapp
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2019/12/28
 * @Version: 1.0
 */
public class DrawWaterMarkActivity extends Activity {
    Bitmap srcBitmap;
    List<String> labels = new ArrayList<>();
    private RelativeLayout rlBtns;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_water_mark);
        initUI();
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

        labels.add("君不见黄河之水天上来，奔流到海不复回。君不见高堂明镜悲白发，朝如青丝暮成雪。君不见三生三世");
        labels.add("下午 16:04:46 ");
        labels.add("武汉市江夏区武大园一路/ 晴 ");
        labels.add("张三丰");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        iv.setVisibility(View.GONE);
        rlBtns.setVisibility(View.VISIBLE);
    }

    private void initUI() {
        rlBtns = findViewById(R.id.rl_btns);
         iv = findViewById(R.id.iv);
        Button buttonTopLeft = findViewById(R.id.btn_top_left);
        Button buttonTopRight = findViewById(R.id.btn_top_right);
        Button buttonCenter = findViewById(R.id.btn_mid_center);
        Button buttonCenterFullScreen = findViewById(R.id.btn_all_screen);
        Button buttonBottomLeft = findViewById(R.id.btn_bottom_left);
        Button buttonBottomRight = findViewById(R.id.btn_bottom_right);

        MyOnClickListener listener = new MyOnClickListener();
        buttonTopLeft.setOnClickListener(listener);
        buttonTopRight.setOnClickListener(listener);
        buttonCenter.setOnClickListener(listener);
        buttonCenterFullScreen.setOnClickListener(listener);
        buttonBottomLeft.setOnClickListener(listener);
        buttonBottomRight.setOnClickListener(listener);
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_top_left:
                    Bitmap bgTopLeft = WatermarkUtil.drawTextsOnBitmap(DrawWaterMarkActivity.this,srcBitmap,labels,26,0, WATER_MARK_GRAVITY_TOP_LEFT);
                    showWaterMarkByBitmap(bgTopLeft);
                    break;
                case R.id.btn_top_right:
                    Bitmap bpTopRight = WatermarkUtil.drawTextsOnBitmap(DrawWaterMarkActivity.this,srcBitmap,labels,26,0, WATER_MARK_GRAVITY_TOP_RIGHT);
                    showWaterMarkByBitmap(bpTopRight);
                    break;
                case R.id.btn_bottom_left:
                    Bitmap bpBottomLeft = WatermarkUtil.drawTextsOnBitmap(DrawWaterMarkActivity.this,srcBitmap,labels,26,0, WATER_MARK_GRAVITY_BOTTOM_LEFT);
                    showWaterMarkByBitmap(bpBottomLeft);
                    break;
                case R.id.btn_bottom_right:
                    Bitmap bpBottomRight = WatermarkUtil.drawTextsOnBitmap(DrawWaterMarkActivity.this,srcBitmap,labels,26,0, WATER_MARK_GRAVITY_BOTTOM_RIGHT);
                    showWaterMarkByBitmap(bpBottomRight);
                    break;
                case R.id.btn_mid_center:
                    Bitmap bpMidCenter = WatermarkUtil.drawTextsOnBitmap(DrawWaterMarkActivity.this,srcBitmap,labels,26,0, WATER_MARK_GRAVITY_MID_CENTER);
                    showWaterMarkByBitmap(bpMidCenter);
                    break;
                case R.id.btn_all_screen:
                    break;
                default:
                    break;
            }
        }
    }

    private void showWaterMarkByBitmap(Bitmap bitmap){
        iv.setVisibility(View.VISIBLE);
        rlBtns.setVisibility(View.GONE);
        iv.setBackground(new BitmapDrawable(getResources(), bitmap));
    }


    /**
     * bitmap转化成byte数组2
     *
     * @param bm 需要转换的Bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}
