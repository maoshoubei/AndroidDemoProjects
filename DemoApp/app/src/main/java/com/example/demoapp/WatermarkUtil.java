package com.example.demoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demoapp.TransPixelUtil.CHINESE;

/**
 * 添加 水印工具类
 * 1.支持 设置 水印绘制内容 绘制位置 水印绘制样式 水印字体大小 字体颜色 水印背景颜色 水印透明度
 * 2.支持传入Bitmap 返回Bitmap
 * 3.支持传入Drawable 返回Drawable
 *
 * @author lotus
 */
public class WatermarkUtil {
    private static final String TAG = "WatermarkUtil";
    private static final int COLOR = Color.parseColor("#f7c215");
    //00ff00 0000ff  000000 660099
    private static final int BACKGROUND_COLOR = Color.parseColor("#000000");
    // 0~255
    private static final int TRANSPARENCY = 200;

    public static int WATER_MARK_GRAVITY_TOP_LEFT = 0;
    public static int WATER_MARK_GRAVITY_TOP_RIGHT = 1;
    public static int WATER_MARK_GRAVITY_BOTTOM_LEFT = 2;
    public static int WATER_MARK_GRAVITY_BOTTOM_RIGHT = 3;
    public static int WATER_MARK_GRAVITY_MID_CENTER = 4;

    public static int WATER_MARK_STYLE_FULL_SCREEN = 0;
    public static int WATER_MARK_STYLE_EXACT_POSITION = 1;

    private float DEFAULT_TEXT_SIZE = 16;
    private int DEFAULT_TEXT_COLOR = Color.parseColor("#f7c215");
    private int DEFAULT_TEXT_BG_COLOR = Color.parseColor("#000000");
    private int DEFAULT_TRANSPARENCY = 200;

    /**
     * Draw texts in the bottom-left of src bitmap.
     *
     * @param srcBitmap
     * @param texts
     * @return
     */
    public static Bitmap drawTextsOnBitmap(Context context, Bitmap srcBitmap, List<String> texts, int textSize, int markType, int markGravity) {
        if (srcBitmap == null) {
            return srcBitmap;
        }

        if (ListUtil.isEmpty(texts)) {
            return srcBitmap;
        }

        Bitmap newBmp = srcBitmap;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
                continue;
            }
            int lastTextLinCont = 0;
            if (i != 0) {
                lastTextLinCont =  map.get(i - 1);
            }
            newBmp = drawTextOnBitmap(context, newBmp, text, i, textSize, markGravity, map, lastTextLinCont);
        }

        return newBmp;
    }

    /**
     * Draw a given text in the bottom-right of src bitmap.
     *
     * @param srcBitmap a new Bitmap with text on it.
     * @param text
     * @return
     */
    private static Bitmap drawTextOnBitmap(Context context, Bitmap srcBitmap, String text, int index, int textSize, int markGravity, Map<Integer, Integer> map,int lastLinCount) {
        if (srcBitmap == null) {
            return srcBitmap;
        }

        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            return srcBitmap;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();


        Rect textRect = getTextRect(text, context);
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        Bitmap newBmp = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBmp);
        canvas.drawBitmap(srcBitmap, 0, 0, null);

        Canvas canvas1 = new Canvas(newBmp);
        canvas1.drawBitmap(srcBitmap, 0, 0, null);

        String familyName = "Arial";
        Typeface font = Typeface.create(familyName, Typeface.NORMAL);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(BACKGROUND_COLOR);
        textPaint.setTypeface(font);
        textPaint.setTextSize(TransPixelUtil.sp2px(context, textSize, CHINESE));
        textPaint.setStrokeWidth(5);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setFakeBoldText(true);

        int marginBottom = TransPixelUtil.dip2px(context, 25);
        int offset = index + 1;

        TextPaint textPaint2 = new TextPaint();
        textPaint2.setAntiAlias(true);
        textPaint2.setColor(COLOR);
        textPaint2.setTypeface(font);
        textPaint2.setTextSize(TransPixelUtil.sp2px(context, textSize, CHINESE));

        textPaint2.setStrokeWidth(0);
        textPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint2.setFakeBoldText(false);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(BACKGROUND_COLOR);
        paint.setAlpha(TRANSPARENCY);
        //处理text换行
        StaticLayout layout1 = new StaticLayout(text, textPaint, srcWidth - 5, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        int lineCount = layout1.getLineCount();
        map.put(index, lineCount);
        int y = 0;
        float x = 10;
        if (markGravity == WATER_MARK_GRAVITY_TOP_LEFT) {
            y = (textRect.height() + marginBottom) * (offset + lineCount + lastLinCount);
        } else if (markGravity == WATER_MARK_GRAVITY_BOTTOM_LEFT) {
            y = srcHeight - (textRect.height() + marginBottom) * (offset + lineCount + lastLinCount);
        } else if (markGravity == WATER_MARK_GRAVITY_TOP_RIGHT) {
            if (lineCount == 1) {
                x = screenWidth - (TransPixelUtil.dip2px(context, textRect.width()));
            }
            y = (textRect.height() + marginBottom) * (offset + lineCount);
        } else if (markGravity == WATER_MARK_GRAVITY_BOTTOM_RIGHT) {
            if (lineCount == 1) {
                x = srcWidth - (TransPixelUtil.dip2px(context, textRect.width()));
            }
            y = srcHeight - (textRect.height() + marginBottom) * (offset + lineCount + lastLinCount);
        } else if (markGravity == WATER_MARK_GRAVITY_MID_CENTER) {
            if (lineCount == 1) {
                x = (srcWidth - textRect.width()) / 2;
            }
            y = srcHeight / 2 - (textRect.height() + marginBottom) * (offset + lineCount + lastLinCount);
        }

        canvas.translate(x, y);
        layout1.draw(canvas);

        canvas1.translate(x, y);

        StaticLayout layout2 = new StaticLayout(text, textPaint2, srcWidth - 5, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        layout2.draw(canvas1);

        canvas.save();
        canvas.restore();// 把当前画布返回（调整）到上一个save()状态之前

        canvas1.save();
        canvas1.restore();//把当前画布返回（调整）到上一个save()状态之前

        return newBmp;
    }


    private static Rect getTextRect(String text, Context context) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            return null;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(TransPixelUtil.sp2px(context, 12, TransPixelUtil.NUMBER_OR_CHARACTER));
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);

        return rect;
    }
}
