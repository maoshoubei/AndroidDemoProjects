package com.example.demoapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

/**
 * @ProjectName: DemoApp
 * @Package: com.example.demoapp
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2019/12/28
 * @Version: 1.0
 */
public class OpenCameraActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);
        initUI();
    }

    private void initUI(){
        Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getCameraIntent();
                OpenCameraActivity.this.startActivity(intent);
            }
        });
    }

    public  Intent getCameraIntent() {
        String localPath = Environment.getExternalStorageDirectory().getPath();
        String rootPath = "//DemoApp//";
        String mediaPath = localPath + rootPath +"//media//";

        hasFileDir(mediaPath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public boolean hasFileDir(String var1) {
        File var2;
        if(!(var2 = new File(var1)).exists()) {
            var2.mkdirs();
        }

        return var2.exists();
    }

    public static void launchCamera(Activity activity, int code, String dirName, String fileName) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(activity, "没有储存卡", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, code);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "没有找到储存目录", Toast.LENGTH_LONG).show();
        }
    }
}
