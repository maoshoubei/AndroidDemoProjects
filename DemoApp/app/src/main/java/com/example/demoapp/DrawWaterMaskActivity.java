package com.example.demoapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
public class DrawWaterMaskActivity extends Activity {
    private ImageView mSourImage;
    private ImageView mWartermarkImage;
    private ImageView mWartermarkImage2;
    private ImageView mWartermarkImage3;
    private ImageView mWartermarkImage4;
    private ImageView mWartermarkImage5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_water_mask);

        initView();
    }

    private void initView() {
        mSourImage = (ImageView) findViewById(R.id.sour_pic);
        mWartermarkImage = (ImageView) findViewById(R.id.wartermark_pic);
        mWartermarkImage2 = (ImageView) findViewById(R.id.wartermark_pic2);
        mWartermarkImage3 = (ImageView) findViewById(R.id.wartermark_pic3);
        mWartermarkImage4 = (ImageView) findViewById(R.id.wartermark_pic4);
        mWartermarkImage5 = (ImageView) findViewById(R.id.wartermark_pic5);
        Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_03);
        mSourImage.setImageBitmap(sourBitmap);

        Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.pic_03);

        Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        Bitmap watermarkBitmap2 = ImageUtil.createWaterMaskLeftBottom(this, sourBitmap, waterBitmap, 0, 0);
        Bitmap watermarkBitmap3 = ImageUtil.createWaterMaskRightBottom(this, sourBitmap, waterBitmap, 0, 0);
        Bitmap watermarkBitmap4 = ImageUtil.createWaterMaskLeftTop(this, sourBitmap, waterBitmap, 0, 0);
        Bitmap watermarkBitmap5 = ImageUtil.createWaterMaskRightTop(this, sourBitmap, waterBitmap, 0, 0);

        Bitmap textBitmap = ImageUtil.drawTextToLeftTop(this, watermarkBitmap4, "左上角", 16, Color.RED, 0, 0);
        Bitmap textBitmap2 = ImageUtil.drawTextToRightBottom(this, watermarkBitmap3, "君不见黄河之水天上来奔流到海不复回君不见高堂明镜悲白发朝如青丝暮成雪", 16, Color.RED, 0, 0);
        Bitmap textBitmap3 = ImageUtil.drawTextToRightTop(this, watermarkBitmap5, "右上角", 16, Color.RED, 0, 0);
        Bitmap textBitmap4 = ImageUtil.drawTextToLeftBottom(this, watermarkBitmap2, "左下角", 16, Color.RED, 0, 0);
        Bitmap textBitmap5 = ImageUtil.drawTextToCenter(this, watermarkBitmap, "中间", 16, Color.RED);

        mWartermarkImage.setImageBitmap(textBitmap);
        mWartermarkImage2.setImageBitmap(textBitmap2);
        mWartermarkImage3.setImageBitmap(textBitmap3);
        mWartermarkImage4.setImageBitmap(textBitmap4);
        mWartermarkImage5.setImageBitmap(textBitmap5);
    }
}
