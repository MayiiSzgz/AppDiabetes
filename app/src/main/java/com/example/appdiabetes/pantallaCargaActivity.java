package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class pantallaCargaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        final int Duracion = 2500;

        new Handler().postDelayed(() -> {
            Intent i = new Intent(pantallaCargaActivity.this, MainActivity.class);
            startActivity(i);

        },Duracion);
    }
}