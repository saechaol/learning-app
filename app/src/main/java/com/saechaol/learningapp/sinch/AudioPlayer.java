package com.saechaol.learningapp.sinch;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.saechaol.learningapp.R;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Implements an audio stream player for the application
 */
public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();
    private Context context;
    private MediaPlayer mediaPlayer;
    private AudioTrack dialTone;
    private final static int SAMPLE_RATE = 16000;

    public AudioPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    public void playRingtone() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // check silent mode
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            try {
                mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackage() + "/" + R.raw.ringtone));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not set up media player object for ringtone");
                mediaPlayer = null;
                return;
            }
        }
    }

    public void stopRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playDialTone() {
        stopDialTone();
        try {
            dialTone = createDialTone(context);
            dialTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play dialtone", e);
        }
    }

    public void stopDialTone() {
        if (dialTone != null) {
            dialTone.stop();
            dialTone.release();
            dialTone = null;
        }
    }

    private static AudioTrack createDialTone(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getResources().openRawResourceFd(R.raw.dialtone);
        int length = (int) fileDescriptor.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);
        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fileDescriptor, byte[] data) throws IOException {
        FileInputStream inputStream = fileDescriptor.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1)
                break;
            bytesRead += res;
        }
    }

}
