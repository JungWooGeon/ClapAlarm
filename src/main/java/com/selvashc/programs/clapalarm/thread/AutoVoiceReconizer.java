package com.selvashc.programs.clapalarm.thread;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;

public class AutoVoiceReconizer {

    public static final int VOICE_RECORDING_FINISHED = 2;

    private RecordAudio recordTask = null;

    private int frequency = 11025;
    private int outfrequency = frequency * 2;

    private Handler handler;

    private int fullCnt = 0;
    private int zeroCnt = 0;
    private int cnt = 0;

    private boolean voiceReconize = false;

    private int settingValue;
    private int settingStopTime;
    private int sensitivity = 1;
    private int cntLimit;

    public AutoVoiceReconizer(Handler handler) {
        this.handler = handler;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void startLevelCheck() {
        settingValue = 2000 / sensitivity;
        settingStopTime = 2000 - (100 * sensitivity);
        cntLimit = 15 - sensitivity;

        voiceReconize = false;
        cnt = 0;
        zeroCnt = 0;
        recordTask = new RecordAudio();
        recordTask.execute();
    }

    public void stopLevelCheck() {
        if (recordTask != null)
            recordTask.cancel(true);
        voiceReconize = true;
    }

    @SuppressLint("StaticFieldLeak")
    private class RecordAudio extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
            int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
            int bufferReadResult;
            int level;
            try {
                int bufferSize = AudioRecord.getMinBufferSize(outfrequency, channelConfiguration, audioEncoding);
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, outfrequency, channelConfiguration, audioEncoding, bufferSize);
                short[] buffer = new short[bufferSize];
                audioRecord.startRecording();
                int total;

                while (!voiceReconize) {
                    bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    total = 0;
                    for (int i = 0; i < bufferReadResult; i++) {
                        total += Math.abs(buffer[i]);
                    }

                    if (fullCnt == 20) {
                        cnt = 0;
                    }
                    if (zeroCnt == 10) {
                        handler.sendMessage(handler.obtainMessage(VOICE_RECORDING_FINISHED));
                        zeroCnt = 0;
                    }

                    // level 은 볼륨
                    level = (total / bufferReadResult);
                    if (level > settingValue) {
                        cnt++;
                        fullCnt = 0;
                        zeroCnt = 0;
                    } else if (level == 0) {
                        zeroCnt++;
                        fullCnt++;
                    } else {
                        fullCnt++;
                        zeroCnt = 0;
                    }

                    if (cnt > cntLimit) {
                        cnt = 0;
                        voiceReconize = true;
                        Thread.sleep(settingStopTime);
                        handler.sendMessage(handler.obtainMessage(VOICE_RECORDING_FINISHED));
                    }
                }
                audioRecord.stop();
            } catch (IllegalArgumentException | IllegalStateException | InterruptedException e) {
                e.getCause();
                Thread.currentThread().interrupt();
            }

            return null;
        }
    }
}
