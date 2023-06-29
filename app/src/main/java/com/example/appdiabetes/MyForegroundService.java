package com.example.appdiabetes;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.List;

public class MyForegroundService extends Service {
    private static final int ALARM_REQUEST_CODE = 1;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private FirebaseFirestore db;
    private FirebaseUser user;

    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                obtenerSiguienteDosis();
                handler.postDelayed(this, 30 * 60 * 1000); // Verificar cada 30 minutos
            }
        };
    }

    private boolean isFirstUpdate = true; // Agregar esta variable de bandera

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification("Prueba", "")); // Pasar una cadena vacía o una cadena predeterminada como siguienteDosis
        handler.post(runnable); // Comenzar a verificar las dosis

        // Agregar la función de escucha para recibir actualizaciones en tiempo real de los datos relevantes
        db.collection("Medicamentos")
                .document(user.getUid())
                .collection("Historial")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Manejar el error
                        return;
                    }

                    if (value != null) {
                        // Se recibieron actualizaciones en tiempo real de los datos relevantes
                        if (isFirstUpdate) {
                            isFirstUpdate = false;
                        } else {
                            obtenerSiguienteDosis(); // Actualizar las notificaciones y alarmas
                        }
                    }
                });

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Detener la verificación de las dosis
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void obtenerSiguienteDosis() {
        if (user != null) {
            Query query = db.collection("Medicamentos")
                    .document(user.getUid())
                    .collection("Historial")
                    .orderBy("siguienteDosis", Query.Direction.ASCENDING);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long halfHourInMillis = 30 * 60 * 1000;
                    long nextDoseTime = Long.MAX_VALUE; // Inicializar con un valor máximo para obtener la siguiente dosis más cercana

                    for (DocumentSnapshot document : documents) {
                        String siguienteDosis = document.getString("siguienteDosis");
                        String nombreMedicamento = document.getString("nombre");
                        int hour = convertTimeStringToHour(siguienteDosis);
                        int minute = convertTimeStringToMinute(siguienteDosis);

                        Calendar nextDoseCalendar = Calendar.getInstance();
                        nextDoseCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        nextDoseCalendar.set(Calendar.MINUTE, minute);
                        nextDoseCalendar.set(Calendar.SECOND, 0);
                        nextDoseCalendar.set(Calendar.MILLISECOND, 0);

                        // Verificar si la siguiente dosis ocurre después de la medianoche
                        if (hour < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                            nextDoseCalendar.add(Calendar.DAY_OF_YEAR, 1);
                        }

                        long nextDoseTimeInMillis = nextDoseCalendar.getTimeInMillis();
                        long timeDifference = nextDoseTimeInMillis - currentTime;

                        if (timeDifference <= halfHourInMillis && timeDifference > 0) {
                            showNotification(siguienteDosis, nombreMedicamento);
                        }
                        if (nextDoseTimeInMillis > currentTime && nextDoseTimeInMillis < nextDoseTime) {
                            nextDoseTime = nextDoseTimeInMillis;
                        }
                    }

                    if (nextDoseTime < Long.MAX_VALUE) {
                        scheduleAlarm(nextDoseTime); // Establecer alarma para la siguiente dosis más cercana
                    }
                }
            });
        }
    }


    private void scheduleAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private int convertTimeStringToHour(String timeString) {
        try {
            String[] parts = timeString.split(":");
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int convertTimeStringToMinute(String timeString) {
        try {
            String[] parts = timeString.split(":");
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showNotification(String siguienteDosis, String nombreMedicamento) {
        createNotificationChannel();
        Notification notification = buildNotification(nombreMedicamento, siguienteDosis);
        NotificationManager manager = getSystemService(NotificationManager.class);
        int notificationId = generateNotificationId();
        manager.notify(notificationId, notification);
    }

    private int generateNotificationId() {
        return (int) System.currentTimeMillis();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Foreground Service Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String nombreMedicamento, String siguienteDosis) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(nombreMedicamento)
                .setContentText(siguienteDosis)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();
    }
}
