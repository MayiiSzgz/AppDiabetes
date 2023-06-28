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
                    .orderBy("fechaHora", Query.Direction.DESCENDING)
                    .limit(1);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if (!documents.isEmpty()) {
                        DocumentSnapshot document = documents.get(0);
                        String siguienteDosis = document.getString("siguienteDosis");

                        // Mostrar la siguiente dosis en la barra de notificaciones
                        showNotification(siguienteDosis);
                    }
                }
            });
        }
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

