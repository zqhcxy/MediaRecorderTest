package com.github.zqhcxy.mediarecordertest;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 点击进行录音
 */
public class Recorder1Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView recorder1_time_tv;//计时
    private TextView recorder1_data_tv;//录音列表/ 取消录制
    private ImageView recorder1_recorder_iv;//录音与停止

    private MediaRecorder recorder;
    public  String PATH_NAME = Environment.getExternalStorageDirectory().toString() + "/zqhcxy/";
    private String audioName;

    private boolean isRecording = false;

    private  Handler mTime = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            recorder1_time_tv.setText("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder1);
        findView();
        initData();
    }

    private void findView() {
        recorder1_time_tv = (TextView) findViewById(R.id.recorder1_time_tv);
        recorder1_data_tv = (TextView) findViewById(R.id.recorder1_data_tv);
        recorder1_recorder_iv = (ImageView) findViewById(R.id.recorder1_recorder_iv);

        recorder1_data_tv.setOnClickListener(this);
        recorder1_recorder_iv.setOnClickListener(this);
    }

    private void initData() {
        PATH_NAME=getRootFilePath()+"zqhcxy/";
        if (isRecording) {
            recorder1_data_tv.setText("取消");
        } else {
            recorder1_data_tv.setText("数据");
        }
    }

    /**
     * 获取SD卡地址
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
     * 初始化录音设备
     */
    private void initialize() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            // 设置MediaRecorder的音频源为麦克风
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置MediaRecorder录制的音频格式
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置MediaRecorder录制音频的编码为amr.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            audioName = new Date().getTime() + ".amr";
            File tmp = new File(PATH_NAME + audioName);
            File parentFile = tmp.getParentFile();
            if (!parentFile.exists()) {
               parentFile.mkdirs();
            }
            // 设置录制好的音频文件保存路径
            recorder.setOutputFile(PATH_NAME + audioName);
            try {
                recorder.prepare();
                recorder.start();   // Recording is now started
//                mTime.postDelayed(new Thread(),100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                stopRecorder();
                File f = new File(PATH_NAME, audioName);
                if (f.exists()) {
                    f.delete();
                }
               Toast.makeText(Recorder1Activity.this, "录音发生错误", Toast.LENGTH_SHORT).show();

            }
        });
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.e("zqh", what + "-----" + extra);
            }
        });

    }

    private void stopRecorder() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();   // 重置，对象可以复用
            recorder.release(); // 释放资源，对象不再复用
            recorder = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recorder1_recorder_iv://录制与停止
                setRecordingState(isRecording);
                break;
            case R.id.recorder1_data_tv://录音列表或取消

                if (isRecording) {// 正在录音
                    setRecordingState(isRecording);
                    File f = new File(PATH_NAME, audioName);
                    if (f.exists()) {
                        f.delete();
                    }
                } else {

                }
                break;
        }
    }

    /**
     * 设置录音状态
     *
     * @param isRecor
     */
    private void setRecordingState(boolean isRecor) {
        if (isRecor) {
            stopRecorder();
            isRecording = false;
            recorder1_recorder_iv.setImageResource(R.mipmap.ic_play_white);
            recorder1_data_tv.setText("数据");
        } else {
            initialize();
            isRecording = true;
            recorder1_recorder_iv.setImageResource(R.mipmap.ic_stop);
            recorder1_data_tv.setText("取消");
        }
    }
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isRecording){
            stopRecorder();
        }
    }
}
