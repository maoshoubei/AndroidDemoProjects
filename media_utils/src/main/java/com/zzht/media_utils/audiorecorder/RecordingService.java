package com.zzht.media_utils.audiorecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";
    private String mFileName = null;
    private String mFilePath = null;
    private MediaRecorder mRecorder = null;

    private DBHelper mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private RecordConfig mConfig;


    private OnTimerChangedListener onTimerChangedListener = null;

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    private MyBinder mybinder = new MyBinder();

    public class MyBinder extends Binder {
        public RecordingService getService() {
            return RecordingService.this;
        }
    }

    public void setConfig(RecordConfig config) {
        mConfig = config;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);

        void updateVolume(double rate);

        void onRecordSaved(String path);
    }

    public void setOnTimerChangedListener(OnTimerChangedListener onTimerChangedListener) {
        this.onTimerChangedListener = onTimerChangedListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (mConfig.isHighQuality()) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        mStartingTimeMillis = System.currentTimeMillis();
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

            startTimer();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    public void setFileNameAndPath() {
        File f;
        do {
            mFileName = mConfig.getAudioName() + "_" + (mDatabase.getCount() + 1) + mConfig.getAudioType();
            mFilePath = mConfig.getAudioFilePath();
            f = new File(mFilePath);
            if (!f.exists()) {
                f.mkdir();
            }

            mFilePath += "/" + mFileName;


        } while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        Log.d(LOG_TAG, "stopRecording");
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
    }

    public void saveRecord(boolean isNeedSave) {
        try {
            if (isNeedSave) {
                mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
                mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);
                if (onTimerChangedListener != null) {
                    onTimerChangedListener.onRecordSaved(mFilePath);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null) {
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                    try {
                        int maxAmplitude = mRecorder.getMaxAmplitude();
                        onTimerChangedListener.updateVolume(maxAmplitude);
                    } catch (Exception e) {
                        Log.e("RecordService", e.getMessage());
                    }
                }
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }
}
