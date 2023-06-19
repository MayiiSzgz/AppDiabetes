package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);



    }


    public void irIniciar(View view)
    {
        Intent i = new Intent(this,IniciarSesionActivity.class);
        startActivity(i);
        finish();

    }
    public void irRegistrarse(View view)
    {
        Intent i = new Intent(this,RegistrarseActivity.class);
        startActivity(i);
        finish();

    }


}