package com.zzht.media_utils;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.lansosdk.videoeditor.LanSongFileUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.zhaoss.weixinrecorded.activity.RecordedActivity;
import com.zzht.media_utils.mediarecorder.PlayVideoActivity;
import com.zzht.media_utils.mediarecorder.VideoRecordAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.zhaoss.weixinrecorded.activity.RecordedActivity.REQUEST_CODE_VIDEO;

/***
 * Created by maoshoubei on 2020-03-16.
 */
public class MediaRecorderDemoActivity extends AppCompatActivity {
    private static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().toString() + "/zzht/MediaRecord/";

    private TextView tv_path;
    private MediaPlayer mMediaPlayer;
    private ImageView iv_photo;
    private Gallery mGallery;
    private List<String> mPathLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_recorder);

        AndPermission.with(this).permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestCode(0).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            }
        }).start();

        tv_path = findViewById(R.id.tv_path);
        iv_photo = findViewById(R.id.iv_photo);
        mGallery = findViewById(R.id.gallery1);
    }

    public void startRecord(View v) {
        LanSongFileUtil.setFileDir(FILE_PATH + System.currentTimeMillis() + "/");
        //LanSongFileUtil.DEFAULT_DIR = FILE_PATH;
        Intent intent = new Intent(this, RecordedActivity.class);
        startActivityForResult(intent, REQUEST_CODE_VIDEO);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_VIDEO) {
                iv_photo.setVisibility(View.GONE);
                int dataType = data.getIntExtra(RecordedActivity.INTENT_DATA_TYPE, RecordedActivity.RESULT_TYPE_VIDEO);
                if (dataType == RecordedActivity.RESULT_TYPE_VIDEO) {
                    String videoPath = data.getStringExtra(RecordedActivity.INTENT_PATH);
                    tv_path.setText("视频地址: " + videoPath);
                    mGallery.setVisibility(View.VISIBLE);
                    if (null != videoPath) {
                        mPathLists.add(videoPath);
                    }

                    VideoRecordAdapter adapter = new VideoRecordAdapter(MediaRecorderDemoActivity.this, mPathLists);
                    mGallery.setAdapter(adapter);
                    mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String videoPath = mPathLists.get(position);
                            Intent intent = new Intent(MediaRecorderDemoActivity.this, PlayVideoActivity.class);
                            intent.putExtra("INTENT_KEY_VIDEOPATH", videoPath);
                            MediaRecorderDemoActivity.this.startActivity(intent);
                        }
                    });

                } else if (dataType == RecordedActivity.RESULT_TYPE_PHOTO) {
                    String photoPath = data.getStringExtra(RecordedActivity.INTENT_PATH);
                    tv_path.setText("图片地址: " + photoPath);
                    iv_photo.setVisibility(View.VISIBLE);
                    mGallery.setVisibility(View.GONE);
                    iv_photo.setImageBitmap(BitmapFactory.decodeFile(photoPath));
                }
            }
        }
    }

}
