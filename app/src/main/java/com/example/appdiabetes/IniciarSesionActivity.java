package com.example.appdiabetes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IniciarSesionActivity extends AppCompatActivity {
    private EditText correo;
    private EditText contrasena;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        correo = findViewById(R.id.idCorreoIS);
        contrasena = findViewById(R.id.contrasenaIS);

        mAuth = FirebaseAuth.getInstance();
        // Agrega el siguiente código para abrir la ventana de restablecimiento de contraseña
        TextView txtOlvidoContrasena = findViewById(R.id.olvidoContrasena);
        txtOlvidoContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IniciarSesionActivity.this, RestablecerContrasena.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // El usuario ya ha iniciado sesión, redirigir a InicioActivity
            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
            finish(); // Opcional: finalizar la actividad actual para que el usuario no pueda volver a ella presionando el botón "Atrás"
        }
    }

    //ir a irARegistrarse
    public void irARegistrarse(View view)
    {
        Intent i = new Intent(this,RegistrarseActivity.class);
        startActivity(i);
    }


    public void iniciarSesion(View view)
    {
        mAuth.signInWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Inicio de sesión correcto",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),InicioActivity.class);
                            startActivity(i);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Inicio de sesión incorrecto",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }
}