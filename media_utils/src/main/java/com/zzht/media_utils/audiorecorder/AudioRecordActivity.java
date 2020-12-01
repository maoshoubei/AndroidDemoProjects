package com.zzht.media_utils.audiorecorder;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.zzht.media_utils.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/***
 * Created by maoshoubei on 2020-04-07.
 */
public class AudioRecordActivity extends Activity {
    private static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().toString() + "/zzht/AudioRecord";
    public static final int REQUEST_CODE_AUDIO = 0x11;
    public static final String INTENT_AUDIO_PATH = "intent_audio_path";
    private static final String TAG = "AudioRecordActivity";
    private boolean mStartRecording = false;
    private VoiceLineView mVoiceLineView;
    private FloatingActionButton mRecordButton;
    private Chronometer mChronometer;
    private ServiceConnection connection = null;
    private Intent intent;
    private RecordingService recordingService;
    private RecordConfig mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        initView();
        setLayoutParams();
        if (!(ActivityCompat.checkSelfPermission(AudioRecordActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //没有权限，申请权限
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
            //申请权限，其中RC_PERMISSION是权限申请码，用来标志权限申请的
            ActivityCompat.requestPermissions(AudioRecordActivity.this, permissions, 0X111);
        } else {
            //拥有权限
        }
        initAudioConfig();
        initServiceConn();
        setListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0X111 && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "权限申请成功");
        } else {
            Log.e("TAG", "权限申请失败");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mStartRecording) {
            mStartRecording = false;
            stopRecord(false);
        }
    }

    private void setLayoutParams() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    private void initView(){
        mRecordButton = (FloatingActionButton) findViewById(R.id.btnRecord);
        mVoiceLineView = (VoiceLineView) findViewById(R.id.voiceLineView);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        mRecordButton.setColorNormal(getResources().getColor(R.color.primary));
        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
    }

    private void initAudioConfig() {
        mConfig = new RecordConfig();
        mConfig.setAudioFilePath(FILE_PATH);
        mConfig.setAudioName("my_audio_record");
        mConfig.setAudioType(".wav");
        mConfig.setHighQuality(true);
    }

    private void setListener() {
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartRecording = !mStartRecording;
                onRecord(mStartRecording);
            }
        });
    }

    private void initServiceConn() {
        intent = new Intent(this, RecordingService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                recordingService = ((RecordingService.MyBinder) service).getService();
                recordingService.setConfig(mConfig);
                recordingService.setOnTimerChangedListener(new MyOnTimerChangedListener());
                recordingService.startRecording();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }


    private void onRecord(boolean start) {
        if (start) {
            startRecord();
        } else {
            stopRecord(true);
        }
    }

    private void startRecord() {
        mRecordButton.setImageResource(R.drawable.ic_media_stop);
        Log.d(TAG, "startRecord");
        Toast.makeText(this, R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
        File folder = new File(mConfig.getAudioFilePath());
        if (!folder.exists()) {
            folder.mkdir();
        }
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopRecord(boolean isNeedSave) {
        mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());

        recordingService.saveRecord(isNeedSave);
        unbindService(connection);
    }

    private class MyOnTimerChangedListener implements RecordingService.OnTimerChangedListener {

        @Override
        public void onTimerChanged(int seconds) {

        }

        @Override
        public void updateVolume(double rate) {
            double ratio = (double) rate / 100;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            mVoiceLineView.setVolume((int) (db));
        }

        @Override
        public void onRecordSaved(String path) {

            Intent intent = new Intent();
            intent.putExtra(INTENT_AUDIO_PATH, path);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
