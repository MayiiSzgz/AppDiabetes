package com.example.appdiabetes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextView apellidoTextView;
    private TextView correoTextView;
    private TextView telefonoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        correoTextView = findViewById(R.id.correo);
        telefonoTextView = findViewById(R.id.telefono);

        //al presionar el boton de gluc imprime un
        //toast con el mensaje de que se presiono el boton
        Button btnGlucemia = findViewById(R.id.gluc);
        btnGlucemia.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroGlucemia.class);
            startActivity(intent);
            finish(); // Termina la actividad actual para que al volver a ella se muestre el InicioActivity en vez de la RegistroGlucemia
        });
        //al presionar el boton historial abre la clase historial
        Button btnHistorial = findViewById(R.id.historial);
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, historial.class);
            startActivity(intent);
            finish(); // Termina la actividad actual para que al volver a ella se muestre el InicioActivity en vez de la Historial
        });
        //al presiona el boton de registro_medicamentos abre la clase RegistroMedicamentos
        Button btnMedicamentos = findViewById(R.id.registro_medicamentos);
        btnMedicamentos.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistroMedicamentos.class);
            startActivity(intent);
            finish(); // Termina la actividad actual para que al volver a ella se muestre el InicioActivity en vez de la RegistroMedicamentos
        });



        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Si existe el documento del usuario, se obtienen sus datos y se muestran en la ventana principal
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    nombreTextView.setText(usuario.getNombre());
                    apellidoTextView.setText(usuario.getApellidos());
                    correoTextView.setText(usuario.getCorreoElectronico());
                    telefonoTextView.setText(usuario.getTelefono());
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
        // Agrega el siguiente código para el botón de cierre de sesión
        Button btnCerrarSesion = findViewById(R.id.cerrar_sesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(InicioActivity.this, IniciarSesionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
