
# media_utils：录音、录像工具模块

#### ​该模块主要是一个音频和视频的录制工具模块，主要包含以下功能：
##### a.音频的录制：主要通过对第三方库[AndroidAudioRecorder](https://github.com/adrielcafe/AndroidAudioRecorder)  的二次封装来实现音频的录制、存储、播放等功能。（支持音频录制的格式、音频存储路径、音频录制质量等参数的设置);
##### b.视频的录制：主要通过对第三方库[VideoRecorder](https://github.com/junerver/VideoRecorder) ,对视频录制等进行二次封装，来实现对视频的录制、存储、播放等功能；


## 1. 更新日志：

### 0.0.1（2020-3-16初版）

完成基础版本

## 2. 使用说明

1.在build.grade文件中添加该模块的依赖：api project(':media_utils')

2.使用示例：
##### （1）音频的录制：  (完整的示例代码详见： AudioRecorderDemoActivity类)

  a. 启动音频录制：

       AndroidAudioRecorder.with(this)
                      // Required 必须设置的参数
                      .setFilePath(AUDIO_FILE_PATH) //录音文件存储的路径
                      .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                      .setRequestCode(REQUEST_RECORD_AUDIO)
      
                      // Optional 可选择设置的参数
                      .setSource(AudioSource.MIC)
                      .setChannel(AudioChannel.STEREO)
                      .setSampleRate(AudioSampleRate.HZ_48000)
                      .setAutoStart(false)
                      .setKeepDisplayOn(true)
      
                      // Start recording
                      .record();

 b.音频录制成功回调：

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_RECORD_AUDIO) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
                }
            }
        }


#####  （2）视频录制：(完整的示例代码详见： MediaRecorderDemoActivity)
a.启动视频的录制：

 	 startActivityForResult(Intent(this@VideoRecorderDemoActivity, VideoRecordActivity::class.java), REQUEST_VIDEO)
说明：VideoRecorderDemoActivity：视频录制实例Activity,VideoRecordActivity: 视频录制的入口Activity。


b.视频录制成功返回视频存储路径：

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == REQUEST_VIDEO) {
                        String path = data.getStringExtra("path")
                        String imgPath = data.getStringExtra("imagePath")
                        int type = data.getIntExtra("type",-1)
                        if (type == TYPE_VIDEO) {
                            // "视频地址：\n\r$path \n\r缩略图地址：\n\r$imgPath"
                        } else if (type == TYPE_IMAGE) {
                           //"图片地址：\n\r$imgPath"
                        }
                    }
                }
    }


## 3. 相关的依赖及文档支持
依赖库如下：

    implementation 'com.melnykov:floatingactionbutton:1.3.0' //音频录制页面浮动按钮
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation files('libs/lansongcommon_hx.jar') //视频录制依赖jar
    implementation 'io.reactivex.rxjava2:rxjava:2.2.0'
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    implementation 'com.yanzhenjie:permission:1.1.2' //动态权限申请依赖库