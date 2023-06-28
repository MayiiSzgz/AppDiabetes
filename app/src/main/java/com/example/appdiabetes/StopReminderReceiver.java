package com.example.appdiabetes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class StopReminderReceiver extends BroadcastReceiver {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setVibrator(Vibrator vibrator) {
        this.vibrator = vibrator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Detener la reproducción del sonido y la vibración
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
