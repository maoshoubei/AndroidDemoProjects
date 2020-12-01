package com.zzht.media_utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzht.media_utils.audiorecorder.AudioRecordActivity;
import com.zzht.media_utils.audiorecorder.DBHelper;
import com.zzht.media_utils.audiorecorder.FileViewerAdapter;
import com.zzht.media_utils.audiorecorder.RecordConfig;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.zzht.media_utils.audiorecorder.AudioRecordActivity.REQUEST_CODE_AUDIO;

/***
 * Created by maoshoubei on 2020-03-10.
 * 录音 示例activity
 */
public class AudioRecorderDemoActivity extends FragmentActivity {
    private static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().toString() + "/zzht/AudioRecord";

    private FileViewerAdapter mFileViewerAdapter;
    private Button addRecord;
    private DBHelper mDBHelper;
    private RecordConfig mConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_AUDIO) {
                String audioPath = data.getStringExtra(AudioRecordActivity.INTENT_AUDIO_PATH);
                Toast.makeText(AudioRecorderDemoActivity.this, "录音文件保存地址为：" + audioPath, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        addRecord = (Button) findViewById(R.id.btn_add_audio_record);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mConfig = initRecordConfig();
        mDBHelper = new DBHelper(this);
        mFileViewerAdapter = new FileViewerAdapter(this, llm, mDBHelper, mConfig);
        checkLocalAudioFile();
        mRecyclerView.setAdapter(mFileViewerAdapter);
        setListener();
    }

    private void setListener() {
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioRecorderDemoActivity.this, AudioRecordActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AUDIO);
            }
        });
    }

    /**
     * 初始化 录音配置信息
     *
     * @return
     */
    private RecordConfig initRecordConfig() {
        RecordConfig config = new RecordConfig();
        config.setAudioFilePath(FILE_PATH);
        config.setAudioName("my_audio_record");
        config.setAudioType(".wav");
        config.setHighQuality(true);

        return config;
    }

    /**
     * 检查本地目标文件夹中的录音文件和数据库文件数量是否匹配
     */
    private void checkLocalAudioFile() {
        File folder = new File(FILE_PATH);
        if (!folder.exists()) {
            return;
        }
        File[] files = folder.listFiles();
        if (null == files || files.length == 0) {
            mFileViewerAdapter.removeAll();
            return;
        }

        int count = files.length;
        for (int j = 0; j < mFileViewerAdapter.getItemCount(); j++) {
            boolean isFind = false;
            for (int i = 0; i < count; i++) {
                File file = files[i];
                String filePath = file.getAbsolutePath();
                String itemPath = mFileViewerAdapter.getItem(j).getFilePath();
                if (filePath.equals(itemPath)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                mFileViewerAdapter.removeOutOfApp(mFileViewerAdapter.getItem(j).getFilePath());
            }
        }
    }
}
