package com.example.appdiabetes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InicioActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView nombreTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        // Iniciar el servicio en primer plano
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        nombreTextView = findViewById(R.id.nombre);

        //al presionar el boton de gluc imprime un
        //toast con el mensaje de que se presiono el boton
        ImageView btnGlucemia = findViewById(R.id.gluc);
        btnGlucemia.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroGlucemia.class);
            startActivity(intent);
            });

        //al presiona el boton de registro_medicamentos abre la clase RegistroMedicamentos
        ImageView btnMedicamentos = findViewById(R.id.registro_medicamentos);
        btnMedicamentos.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroMedicamentos.class);
            startActivity(intent);
       });

        //al presiona el boton de ejercicio abre la clase EjerciciosActivity
        ImageView btnEjercicio = findViewById(R.id.btnEjercicios);
        btnEjercicio.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, EjerciciosActivity.class);
            startActivity(intent);
        });

        //al presiona el boton de alarma abre la clase AlarmasActivity
        ImageView btnRecetas = findViewById(R.id.btnRecetas);
        btnRecetas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RecetasActivity.class);
            startActivity(intent);
        });

        //al presiona el boton de alarma abre la clase Alarmas
        ImageView btnAlarmas = findViewById(R.id.btnAlarma);
        btnAlarmas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, AlarmasActivity.class);
            startActivity(intent);
        });

        //al presiona el boton de graficas abre la clase Estadisticas
        ImageView btnGraficas = findViewById(R.id.btnEstadisticas);
        btnGraficas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, EstadisticasActivity.class);
            startActivity(intent);
        });





        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Si existe el documento del usuario, se obtienen sus datos y se muestran en la ventana principal
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    nombreTextView.setText(usuario.getNombre());

                } else {
                    // Si el documento del usuario no existe, se muestra un mensaje de error
                    Toast.makeText(getApplicationContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Si falla la llamada a Firestore, se muestra un mensaje de error
                Toast.makeText(getApplicationContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
        

        ImageView btnPerfil = findViewById(R.id.perfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PerfilActivity.class);
            startActivity(intent);
          });
    }
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // Cierra todas las actividades y sale de la aplicación
    }
}
