package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class pantallaCargaActivity extends AppCompatActivity {

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


        final int Duracion = 2500;

        new Handler().postDelayed(() -> {
            Intent i = new Intent(pantallaCargaActivity.this, MainActivity.class);
            startActivity(i);

        },Duracion);
    }
}