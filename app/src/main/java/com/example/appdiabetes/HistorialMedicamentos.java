package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialMedicamentos extends AppCompatActivity {

    private ListView listViewHistorial;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ArrayAdapter<String> adapter;
    private List<String> historialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_medicamentos);
        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        listViewHistorial = findViewById(R.id.listViewHistorial);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        historialList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historialList);
        listViewHistorial.setAdapter(adapter);

        obtenerHistorial();
    }

    private void obtenerHistorial() {
        if (user != null) {
            CollectionReference historialRef = db.collection("Medicamentos")
                    .document(user.getUid())
                    .collection("Historial");

            Query query = historialRef.orderBy("fechaHora", Query.Direction.DESCENDING);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    historialList.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        String nombre = document.getString("nombre");
                        String dosis = document.getString("dosis");
                        String frecuencia = document.getString("frecuencia");
                        String siguienteDosis = document.getString("siguienteDosis");
                        String duracion = document.getString("duracion");
                        String comentario = document.getString("comentario");

                        String registro = "Nombre: " + nombre +
                                "\nDosis: " + dosis +
                                "\nFrecuencia: " + frecuencia +
                                "\nSiguiente Dosis: " + siguienteDosis +
                                "\nDuración: " + duracion +
                                "\nComentario: " + comentario;

                        historialList.add(registro);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Error al obtener el historial", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se encontró un usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
