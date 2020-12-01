package com.zzht.media_utils.mediarecorder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.zzht.media_utils.R;


public class PlayVideoActivity extends Activity {
    private static final String TAG = PlayVideoActivity.class.getSimpleName();
    public static final String INTENT_KEY_VIDEOPATH = "INTENT_KEY_VIDEOPATH";// the path can be a local path or a web URL.

    private MediaPlayer mediaPlayer;
    private SurfaceView svVideo;
    private SurfaceHolder shVideo;
    private Button btnPlay;
    private Button btnPause;
    private ProgressBar pbBuffer;
    private String videoPath;
    private int position;
    private boolean isPreparing;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_paly);
        initDataSource();
        initUI();
        initListener();
    }

    private void initUI() {
        svVideo = (SurfaceView) findViewById(R.id.sv_video);
        shVideo = svVideo.getHolder();
        shVideo.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);
        pbBuffer = (ProgressBar) findViewById(R.id.pb_buffer);
        btnPlay.setVisibility(View.GONE);
    }

    private void initDataSource() {
        videoPath = getIntent().getStringExtra(INTENT_KEY_VIDEOPATH);
    }

    private void initListener() {
        btnPlay.setOnClickListener(new MyPlayOnClickListener());
        btnPause.setOnClickListener(new MyPauseOnClickListener());
        shVideo.addCallback(new MySHolderCallBack());
        svVideo.setOnClickListener(new MySurfaceViewOnClickListener());
    }

    private void preparePlayVideo() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
        mediaPlayer.setOnErrorListener(new MyOnErrorListener());
        mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setDisplay(shVideo);
            mediaPlayer.prepareAsync();
            isPreparing = true;
            pbBuffer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    private void releaseMediaPlayer() {

        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
            pbBuffer.setVisibility(View.GONE);
            releaseMediaPlayer();
        }

        return super.onKeyDown(keyCode, event);
    }

    private class MyPlayOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (position == 0) {
                btnPlay.setVisibility(View.GONE);
                preparePlayVideo();
            } else if (position != 0) {
                btnPlay.setVisibility(View.GONE);
                mediaPlayer.start();
                isPlaying = true;
            }
        }
    }

    private class MyPauseOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            btnPause.setVisibility(View.GONE);
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    private class MySurfaceViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (isPreparing) {
                return;
            }
            if (isPlaying && (btnPause.getVisibility() != View.VISIBLE)) {
                btnPause.setVisibility(View.VISIBLE);
            } else if (!isPlaying && (btnPlay.getVisibility() != View.VISIBLE)) {
                btnPlay.setVisibility(View.VISIBLE);
            } else if (isPlaying && (btnPause.getVisibility() == View.VISIBLE)) {
                btnPause.setVisibility(View.GONE);
            } else {
                // no other logic
            }
        }
    }

    private class MySHolderCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (position == 0) {
                btnPlay.setVisibility(View.GONE);
                preparePlayVideo();// when open the activity, start to play video right now.
            } else if (position > 0) {
                mediaPlayer.seekTo(position);
                btnPlay.performClick();
            } else {
                //TODO:no logic to do.
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // nothing to do
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (null != mediaPlayer) {
                position = mediaPlayer.getCurrentPosition();
            }
            releaseMediaPlayer();
        }
    }

    private class MyOnCompletionListener implements OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
            finish();
        }
    }

    private class MyOnPreparedListener implements OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            // TODO Auto-generated method stub
            isPreparing = false;
            pbBuffer.setVisibility(View.GONE);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    private class MyOnErrorListener implements OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            releaseMediaPlayer();
            finish();
            return true;
        }
    }
}
