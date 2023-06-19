package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class pantallaCargaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        mAuth = FirebaseAuth.getInstance();

        final int Duracion = 2500;

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Intent i = new Intent(pantallaCargaActivity.this, InicioActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(pantallaCargaActivity.this, MainActivity.class);
                startActivity(i);
            }

        }, Duracion);
    }
}
