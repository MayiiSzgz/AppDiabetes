package com.example.appdiabetes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContrasena extends AppCompatActivity {

    private EditText editTextCorreo;
    private Button btnRestablecer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasena);

        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        mAuth = FirebaseAuth.getInstance();

        editTextCorreo = findViewById(R.id.editTextCorreo);
        btnRestablecer = findViewById(R.id.btnRestablecer);

        btnRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarConfirmacionRestablecer();
            }
        });
    }

    private void mostrarConfirmacionRestablecer() {
        String correo = editTextCorreo.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restablecer Contraseña");
        builder.setMessage("¿Está seguro de que desea restablecer su contraseña?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restablecerContrasena(correo);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    private void restablecerContrasena(String correo) {
        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RestablecerContrasena.this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                        // Regresar a la actividad IniciarSesionActivity
                        Intent intent = new Intent(RestablecerContrasena.this, IniciarSesionActivity.class);
                        startActivity(intent);
                        finish(); // Opcional: finalizar la actividad actual para que el usuario no pueda volver a ella presionando el botón "Atrás"
                    } else {
                        Toast.makeText(RestablecerContrasena.this, "No se pudo enviar el correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
