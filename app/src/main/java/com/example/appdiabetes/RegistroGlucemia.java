package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistroGlucemia extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;

    private EditText valorEditText;
    private RadioGroup unidadRadioGroup;
    private EditText fechaHoraEditText;
    private EditText estadoEditText;
    private Button enviarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_glucemia);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        valorEditText = findViewById(R.id.editTextNumber);
        String unidad = ((RadioButton) findViewById(unidadRadioGroup.getCheckedRadioButtonId())).getText().toString();
        final String finalUnidad = unidad;

        fechaHoraEditText = findViewById(R.id.fechaHora);
        estadoEditText = findViewById(R.id.estado);
        enviarButton = findViewById(R.id.enviar);

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el valor numérico de la glucemia
                double valor = Double.parseDouble(valorEditText.getText().toString());

                // Obtener la unidad de medida correspondiente
                String unidad = "";
                if (unidadRadioGroup.getCheckedRadioButtonId() == R.id.mg) {
                    unidad = "mg/dL";
                } else if (unidadRadioGroup.getCheckedRadioButtonId() == R.id.mmol) {
                    unidad = "mmol/L";
                }

                // Obtener la fecha y la hora de la medición
                Date fechaHora = new Date(fechaHoraEditText.getText().toString());

                // Obtener el estado de la persona en el momento de la medición
                String estado = estadoEditText.getText().toString();

                // Crear un nuevo registro de glucemia
                Map<String, Object> glucemia = new HashMap<>();
                glucemia.put("valor", valor);
                glucemia.put("unidad", unidad);
                glucemia.put("fechaHora", fechaHora);
                glucemia.put("fechaHoraActual", new Date());
                glucemia.put("estado", estado);

                // Obtener la referencia al documento del usuario actual
                DocumentReference docRef = db.collection("Glucemia").document(user.getUid());

                // Agregar el nuevo registro de glucemia a la colección correspondiente
                docRef.set(glucemia)
                        .addOnSuccessListener(documentReference -> {
                            // Analizar el nivel de glucemia y mostrar el resultado en un mensaje
                            Glucemia glucemiaActual = new Glucemia(valor, finalUnidad, fechaHora, estado);
                            String analisis = glucemiaActual.analizarNivelGlucemia();
                            Toast.makeText(getApplicationContext(), "Nivel de glucemia: " + analisis, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Error al agregar registro de glucemia", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
