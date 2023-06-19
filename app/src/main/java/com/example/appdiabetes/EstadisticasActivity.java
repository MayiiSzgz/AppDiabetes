package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasActivity extends AppCompatActivity {
    private LineChart lineChart;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        lineChart = findViewById(R.id.lineChart);
        // Configuración básica del gráfico
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);

        // Obtener los datos de los registros de glucosa desde Firestore
        obtenerDatosGlucosa();

    }

    private void obtenerDatosGlucosa() {
        // Verifica si el usuario está autenticado
        user = mAuth.getCurrentUser();
        if (user != null) {
            // Obtén una referencia a la colección "Glucosa" del usuario actual
            CollectionReference glucosaRef = FirebaseFirestore.getInstance()
                    .collection("Glucemia")
                    .document(user.getUid())
                    .collection("Historial");

            // Realiza la consulta para obtener los registros de glucosa ordenados por fechaHora
            glucosaRef.orderBy("fechaHora", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Entry> entries = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtén los datos relevantes del registro de glucosa
                                float nivelGlucosa = document.getDouble("nivelGlucosa").floatValue();
                                String fechaHora = document.getString("fechaHora");

                                // Crea una entrada para la gráfica con el nivel de glucosa y la posición en el tiempo
                                entries.add(new Entry(entries.size(), nivelGlucosa));
                            }

                            // Crea un conjunto de datos con las entradas
                            LineDataSet dataSet = new LineDataSet(entries, "Nivel de Glucosa");

                            // Personaliza el conjunto de datos según tus preferencias
                            dataSet.setColor(Color.BLUE);
                            dataSet.setCircleColor(Color.BLUE);
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(4f);
                            dataSet.setDrawValues(false);

                            // Crea un conjunto de datos para el gráfico y asigna el conjunto de datos
                            LineData lineData = new LineData(dataSet);

                            // Asigna los datos al gráfico y actualiza la visualización
                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        } else {
                            Toast.makeText(this, "Error al obtener los datos de glucosa", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}