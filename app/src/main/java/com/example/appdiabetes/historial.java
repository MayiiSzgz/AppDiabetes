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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class historial extends AppCompatActivity {

    private ListView listViewHistorial;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ArrayAdapter<String> adapter;
    private List<String> historialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

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
            CollectionReference historialRef = db.collection("Glucemia")
                    .document(user.getUid())
                    .collection("Historial");

            Query query = historialRef.orderBy("fechaHora", Query.Direction.DESCENDING);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    historialList.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        double valor = document.getDouble("valor");
                        String unidad = document.getString("unidad");
                        Date fechaHora = document.getDate("fechaHora");
                        String estado = document.getString("estado");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss", Locale.getDefault());
                        String fechaHoraString = dateFormat.format(fechaHora);

                        String registro = "Valor: " + valor + " " + unidad + "\nFecha y hora: " + fechaHoraString + "\nEstado: " + estado;
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
