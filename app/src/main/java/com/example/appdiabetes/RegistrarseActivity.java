package com.example.appdiabetes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrarseActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;

    private EditText correo;
    private EditText contrasena;
    private EditText contrasenaConfirmacion;
    private EditText nombre;
    private EditText apellidos;
    private EditText telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        mAuth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.idCorreoR);
        contrasena = findViewById(R.id.contrasenaR);
        contrasenaConfirmacion = findViewById(R.id.contrasenaConfirmacionR);
        nombre = findViewById(R.id.idNombre);
        apellidos = findViewById(R.id.idApellidos);
        telefono = findViewById(R.id.idTelefono);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void registrarUsuario(View view)
    {
        if(contrasena.getText().toString().equals(contrasenaConfirmacion.getText().toString()))
        {
            mAuth.createUserWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Usuario creado", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Se crea un objeto Usuario con los datos del formulario
                                Usuario usuario = new Usuario(
                                        nombre.getText().toString(),
                                        apellidos.getText().toString(),
                                        telefono.getText().toString(),
                                        correo.getText().toString()

                                );
                                // Se guarda el objeto Usuario en Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(user.getUid()).set(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Usuario guardado", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al guardar el usuario", Toast.LENGTH_SHORT).show());

                                // Se inicia la actividad InicioActivity


                                Intent i = new Intent(getApplicationContext(), InicioActivity.class);
                                startActivity(i);
                                //updateUI(user);git
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

        }
        else
        {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT);
        }


    }
}