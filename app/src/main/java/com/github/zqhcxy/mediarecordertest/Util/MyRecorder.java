package com.github.zqhcxy.mediarecordertest.Util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 录音功能工具类
 * <p/>
 * Created by zqhcxy on 2016/2/19.
 */
public class MyRecorder {
    private Context mContext;
    private MediaRecorder recorder;
    public String PATH_NAME;//录音保存目录
    private String audioName;//录音文件的名字
    public String audioPath;//录音的地址
    private static int SAMPLE_RATE_IN_HZ = 16000; // 采样率
    private int MAX_LENGTH = 300 * 1000;//限制录音时间。为0就不限制

    private int voiceLength = 0;

    private RecordingState recordingState;// 录音监听接口
    private Runnable runnable;
    private Handler mTimeHandler = new Handler();

    public MyRecorder(Context context, int limitTime) {
        mContext = context;
        PATH_NAME = getRootFilePath() + "zqhcxy/";
        MAX_LENGTH = limitTime;
    }

    /**
     * 初始化录音设备并开始录音
     */
    public void initialize() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            // 设置MediaRecorder的音频源为麦克风
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置MediaRecorder录制的音频格式
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置MediaRecorder录制音频的编码为amr.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置MediaRecorder录制音频的通道数
            recorder.setAudioChannels(2);
            //设置MediaRecorder录制音频的采样率
            recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);


            audioName = new Date().getTime() + ".amr";
            audioPath = PATH_NAME + audioName;
            File tmp = new File(audioPath);
            File parentFile = tmp.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            // 设置录制好的音频文件保存路径
            recorder.setOutputFile(PATH_NAME + audioName);
            try {
                recorder.prepare();
                recorder.start();   // Recording is now started
                voiceLength = 0;
                tiTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                recordingState.onRecorderError();
                recorderCancle();
                Toast.makeText(mContext, "录音发生错误", Toast.LENGTH_SHORT).show();

            }
        });
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.e("zqh", what + "-----" + extra);
            }
        });

    }

    /**
     * 停止录音
     */
    public void stopRecorder() {
        if (recorder != null) {
            voiceLength = 0;
            recorder.stop();
            recorder.reset();   // 重置，对象可以复用
            recorder.release(); // 释放资源，对象不再复用
            recorder = null;
            recordingState.onRecorderTime("00:00", 0);
        }
        if (mTimeHandler != null && runnable != null) {
            mTimeHandler.removeCallbacks(runnable);
            runnable = null;
        }
    }


    /**
     * 录音失败或取消录音，就删除录音文件
     */
    public void recorderCancle() {
        stopRecorder();
        File f = new File(PATH_NAME, audioName);
        if (f.exists()) {
            f.delete();
        }
    }


    /**
     * 录音失败监听
     *
     * @param recordingState
     */
    public void setOnErrorListener(RecordingState recordingState) {
        this.recordingState = recordingState;
    }


    /**
     * 获取SD卡地址
     *
     * @return
     */
    public static String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";// filePath:/sdcard/
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:/data/data/
        }
    }

    /**
     * 判断是否有sd卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }


    private void tiTime() {
        runnable = new Runnable() {
            @Override
            public void run() {
                voiceLength += 100;
//                int voiceValue = recorder.getMaxAmplitude();
                if (MAX_LENGTH != 0 && voiceLength > MAX_LENGTH) {//超过录音时间
                    stopRecorder();
                } else {
                    recordingState.onRecorderTime(TimeUtils.convertMilliSecondToMinute2(voiceLength), voiceLength);
                    mTimeHandler.postDelayed(this, 100);
                }
            }
        };
        mTimeHandler.postDelayed(runnable, 100);
    }

    /**
     * 录音状态接口
     */
    public interface RecordingState {
        /**
         * 录音失败
         */
        public void onRecorderError();

        /**
         * 录音时间
         *
         * @param value    转换后的时间
         * @param datatime 未转换的时间
         */
        public void onRecorderTime(String value, int datatime);
    }

}
