package com.example.appdiabetes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
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

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obtener la siguiente dosis desde Firebase
        obtenerSiguienteDosis();

        // Llamar a startForeground() para indicar que el servicio está en primer plano
        startForeground(NOTIFICATION_ID, buildNotification("Prueba")); // Pasar una cadena vacía o una cadena predeterminada como siguienteDosis

        // Devolver START_STICKY para que el servicio se reinicie automáticamente si se finaliza
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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

                    for (DocumentSnapshot document : documents) {
                        String siguienteDosis = document.getString("siguienteDosis");
                        long nextDoseTime = convertTimeStringToTimestamp(siguienteDosis);

                        long timeDifference = nextDoseTime - currentTime;

                        if (timeDifference <= halfHourInMillis && timeDifference > 0) {
                            showNotification(siguienteDosis);
                        }
                    }
                }
            });
        }
    }




    private long convertTimeStringToTimestamp(String timeString) {
        try {
            // Obtener la hora y los minutos de la cadena de texto
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Obtener el calendario actual y establecer la hora y los minutos
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Obtener el valor numérico en milisegundos desde la medianoche
            return calendar.getTimeInMillis();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Devuelve 0 si hay algún error al convertir la cadena de texto
    }

    private void showNotification(String siguienteDosis) {
        // Crear una notificación con la siguiente dosis
        createNotificationChannel();
        Notification notification = buildNotification(siguienteDosis);
        NotificationManager manager = getSystemService(NotificationManager.class);
        int notificationId = generateNotificationId(); // Generar un ID único para cada notificación
        manager.notify(notificationId, notification);
    }

    private int generateNotificationId() {
        // Generar un ID único para cada notificación basado en la hora actual
        return (int) System.currentTimeMillis();
    }


    private void createNotificationChannel() {
        // Configurar un canal de notificación para Android 8.0 y versiones superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Foreground Service Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String siguienteDosis) {
        // Construir la notificación con la siguiente dosis como contenido
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Próxima Dosis")
                .setContentText(siguienteDosis)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();
    }
}