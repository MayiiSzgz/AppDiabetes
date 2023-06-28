    package com.example.appdiabetes;

    import android.app.Notification;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.media.AudioAttributes;
    import android.media.AudioManager;
    import android.media.MediaPlayer;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Vibrator;

    import androidx.core.app.NotificationCompat;

    import java.io.IOException;
    import java.util.Objects;

    public class AlarmReceiver extends BroadcastReceiver {

        private MediaPlayer mediaPlayer;
        private Vibrator vibrator;

        @Override
        public void onReceive(Context context, Intent intent) {
            // Reproducir sonido
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/raw/alarm_sound"));
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioAttributes(getAudioAttributes());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Vibra el dispositivo
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 1000};
                vibrator.vibrate(pattern, 0);
            }

            // Mostrar notificación de alarma en ejecución
            showAlarmNotification(context);
        }

        private AudioAttributes getAudioAttributes() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
            } else {
                return new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build();
            }
        }

        private void showAlarmNotification(Context context) {
            String channelId = "alarm_channel";
            String channelName = "Alarm Channel";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("¡Alarma!")
                    .setContentText("Es hora de tomar tu medicamento")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setShowBadge(true);
                Objects.requireNonNull(context.getSystemService(NotificationManager.class)).createNotificationChannel(channel);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }
