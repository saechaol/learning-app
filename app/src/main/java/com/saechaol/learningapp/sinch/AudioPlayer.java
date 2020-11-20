package com.saechaol.learningapp.sinch;


import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.saechaol.learningapp.R;

import java.io.IOException;

/**
 * Implements an audio stream player for the application
 */
public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();
    private Context context;
    private MediaPlayer mediaPlayer;
    private AudioTrack progressTone;
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

    public void playProgressTone() {
        stopProgressTone();
        try {
            progressTone = createProgressTone(context);
            progressTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play dialtone", e);
        }
    }

    public void stopProgressTone() {
        if (progressTone != null) {
            progressTone.stop();
            progressTone.release();
            progressTone = null;
        }
    }

}
