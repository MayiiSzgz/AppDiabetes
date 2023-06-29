package com.example.appdiabetes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
                    .orderBy("siguienteDosis", Query.Direction.ASCENDING)
                    .limit(2);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if (documents.size() >= 2) {
                        DocumentSnapshot firstDose = documents.get(0);
                        DocumentSnapshot secondDose = documents.get(1);

                        long currentTime = Calendar.getInstance().getTimeInMillis();

                        // Obtener los valores de siguienteDosis como cadenas de texto
                        String firstDoseTimeStr = firstDose.getString("siguienteDosis");
                        String secondDoseTimeStr = secondDose.getString("siguienteDosis");

                        // Convertir las cadenas de texto a valores numéricos
                        long firstDoseTime = convertTimeStringToTimestamp(firstDoseTimeStr);
                        long secondDoseTime = convertTimeStringToTimestamp(secondDoseTimeStr);

                        // Comparar los tiempos de las dosis con la hora actual
                        if (firstDoseTime >= currentTime) {
                            // La primera dosis es la más cercana a la hora actual
                            long timeDifference = firstDoseTime - currentTime;
                            if (timeDifference <= 30 * 60 * 1000) {
                                // La siguiente dosis está a menos de 30 minutos de distancia
                                String siguienteDosis = firstDose.getString("siguienteDosis");
                                showNotification(siguienteDosis);
                            }
                        } else if (secondDoseTime >= currentTime) {
                            // La segunda dosis es la más cercana a la hora actual
                            long timeDifference = secondDoseTime - currentTime;
                            if (timeDifference <= 30 * 60 * 1000) {
                                // La siguiente dosis está a menos de 30 minutos de distancia
                                String siguienteDosis = secondDose.getString("siguienteDosis");
                                showNotification(siguienteDosis);
                            }
                        }
                    } else if (documents.size() == 1) {
                        // Solo hay una dosis programada
                        DocumentSnapshot document = documents.get(0);
                        String siguienteDosis = document.getString("siguienteDosis");
                        showNotification(siguienteDosis);
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
        startForeground(NOTIFICATION_ID, notification);
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
