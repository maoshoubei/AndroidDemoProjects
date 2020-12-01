package com.zzht.media_utils.audiorecorder;

/***  
 * Created by maoshoubei on 2020-04-07.
 */
public class RecordConfig {
    /* 录音保存的路径
     */
    private String audioFilePath;
    /**
     * 录音保存的文件名称
     */
    private String audioName;
    /**
     * 录音保存的文件类型 .mp4、.mp3、wav等
     */
    private String audioType;
    /**
     * 是否是高质量的采集
     */
    private boolean isHighQuality;

    /**
     * 录音配置的构造方法
     */
    public RecordConfig() {

    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public boolean isHighQuality() {
        return isHighQuality;
    }

    public void setHighQuality(boolean highQuality) {
        isHighQuality = highQuality;
    }
}
