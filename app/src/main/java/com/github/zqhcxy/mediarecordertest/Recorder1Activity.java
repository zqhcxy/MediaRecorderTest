package com.github.zqhcxy.mediarecordertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zqhcxy.mediarecordertest.Util.MyRecorder;

/**
 * 点击进行录音
 */
public class Recorder1Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView recorder1_time_tv;//计时
    private TextView recorder1_data_tv;//录音列表/ 取消录制
    private ImageView recorder1_recorder_iv;//录音与停止
    private TextView recorder1_cancle_tv;//取消发送，删除音频

    private MyRecorder myRecorder;// 录音对象

    private boolean isRecording = false;// 是否在录音中


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
        recorder1_cancle_tv = (TextView) findViewById(R.id.recorder1_cancle_tv);
        recorder1_recorder_iv = (ImageView) findViewById(R.id.recorder1_recorder_iv);

        recorder1_data_tv.setVisibility(View.GONE);
        recorder1_cancle_tv.setVisibility(View.GONE);
        recorder1_data_tv.setOnClickListener(this);
        recorder1_recorder_iv.setOnClickListener(this);
        recorder1_cancle_tv.setOnClickListener(this);
    }

    private void initData() {
        myRecorder = new MyRecorder(this, 0);
        myRecorder.setOnErrorListener(new MyRecorder.RecordingState() {
            @Override
            public void onRecorderError() {
                setRecordingState(true);
                recorder1_data_tv.setVisibility(View.GONE);
            }

            @Override
            public void onRecorderTime(String value, int datatime) {
                recorder1_time_tv.setText(value);
            }
        });
        if (isRecording) {
            recorder1_data_tv.setText("取消");
        } else {
            recorder1_data_tv.setText("发送");
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
                    myRecorder.recorderCancle();
                    setRecordingState(true);
                    recorder1_data_tv.setVisibility(View.GONE);
                    recorder1_cancle_tv.setVisibility(View.GONE);
                } else {
                    Toast.makeText(Recorder1Activity.this, "文件地址：" + myRecorder.audioPath, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.recorder1_cancle_tv:// 取消发送
                myRecorder.recorderCancle();
                finish();
                break;
        }
    }

    /**
     * 设置录音状态
     *
     * @param isRecor
     */
    private void setRecordingState(boolean isRecor) {
        recorder1_data_tv.setVisibility(View.VISIBLE);
        if (isRecor) {
            myRecorder.stopRecorder();
            isRecording = false;
            recorder1_recorder_iv.setImageResource(R.mipmap.ic_play_white);
            recorder1_data_tv.setText("发送");
            recorder1_cancle_tv.setVisibility(View.VISIBLE);
        } else {
            myRecorder.initialize();
            isRecording = true;
            recorder1_recorder_iv.setImageResource(R.mipmap.ic_stop);
            recorder1_data_tv.setText("取消");
            recorder1_cancle_tv.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording && myRecorder != null) {
            myRecorder.stopRecorder();
        }
    }
}
