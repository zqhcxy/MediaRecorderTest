MediaRecorder
============
#1、介绍：<br>
    已经集成了录音、编码、压缩等，支持少量的录音音频格式，大概有.aac（api=16）、.amr、.3gp。<br>
    优点：大部分 以集成，直接调用接口即可，代码量小。<br>
   缺点：无法实现音频处理；输出的音频格式不是很多，例如没有输出Mp3格式。<br>
    (待测试)20s录音，amr格式，33k大小。<br>
#2、音频格式比较：<br>
    WAV格式：录制质量高，但压缩率小，文件大。<br>
    AAC格式：相对于mp3，AAC格式的音频音质更佳，文件更小；有损压缩；一般苹果或Android SDK4.1.2(API 16)及以上版本支持播放。<br>
    AMR格式:压缩比比较大，相对其他格式的压缩质量比较差，多用于人生，通话录音。<br>
#3、主要代码:<br>
>```java
            recorder = new MediaRecorder();
            // 设置MediaRecorder的音频源为麦克风
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置MediaRecorder录制的音频格式
             /* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
                 *THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
                 */
           recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            /* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置MediaRecorder录制音频的通道数
            recorder.setAudioChannels(2);
            //设置MediaRecorder录制音频的采样率
            recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
>```