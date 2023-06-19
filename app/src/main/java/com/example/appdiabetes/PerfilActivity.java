package com.example.appdiabetes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView nombreTextView;
    private TextView apellidoTextView;
    private TextView correoTextView;
    private TextView telefonoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        correoTextView = findViewById(R.id.correo);
        telefonoTextView = findViewById(R.id.telefono);

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
    }
}