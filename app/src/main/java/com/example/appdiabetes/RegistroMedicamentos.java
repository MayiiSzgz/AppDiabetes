package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class RegistroMedicamentos extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextDosis;
    private Spinner spinnerFrecuencia;
    private EditText editTextSiguienteDosis;
    private EditText editTextDuracion;
    private EditText editTextComentario;
    private Button buttonGuardar;

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_medicamentos);

        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDosis = findViewById(R.id.editTextDosis);
        spinnerFrecuencia = findViewById(R.id.spinnerFrecuencia);
        editTextSiguienteDosis = findViewById(R.id.editTextSiguienteDosis);
        editTextDuracion = findViewById(R.id.editTextDuracion);
        editTextComentario = findViewById(R.id.editTextComentario);
        buttonGuardar = findViewById(R.id.buttonGuardar);

        // Configurar el spinner de frecuencia
        ArrayAdapter<CharSequence> frecuenciaAdapter = ArrayAdapter.createFromResource(this,
                R.array.frecuencia_options, android.R.layout.simple_spinner_item);
        frecuenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(frecuenciaAdapter);

        editTextSiguienteDosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(editTextSiguienteDosis);
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMedicamento();
            }
        });

        //al presiona el boton de ver_medicamentos abre la clase HistorialMedicamentos
        Button btnVerMedicamentos = findViewById(R.id.ver_medicamentos);
        btnVerMedicamentos.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroMedicamentos.this, HistorialMedicamentos.class);
            startActivity(intent);
        });
    }

    private void showTimePickerDialog(final EditText editText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String hora = String.format("%02d:%02d", hourOfDay, minute);
            editText.setText(hora);
        }, 0, 0, false);
        timePickerDialog.show();
    }

    private void guardarMedicamento() {
        String nombre = editTextNombre.getText().toString().trim();
        String dosis = editTextDosis.getText().toString().trim();
        String frecuencia = spinnerFrecuencia.getSelectedItem().toString().trim();
        String siguienteDosis = editTextSiguienteDosis.getText().toString().trim();
        String duracion = editTextDuracion.getText().toString().trim();
        String comentario = editTextComentario.getText().toString().trim();

        if (nombre.isEmpty() || dosis.isEmpty() || frecuencia.isEmpty() || siguienteDosis.isEmpty() || duracion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> medicamento = new HashMap<>();
            medicamento.put("nombre", nombre);
            medicamento.put("dosis", dosis);
            medicamento.put("frecuencia", frecuencia);
            medicamento.put("siguienteDosis", siguienteDosis);
            medicamento.put("duracion", duracion);
            medicamento.put("comentario", comentario);
            medicamento.put("fechaHora", FieldValue.serverTimestamp()); // Agregar marca de tiempo del servidor

            CollectionReference medicamentosRef = db.collection("Medicamentos").document(user.getUid()).collection("Historial");
            medicamentosRef.add(medicamento)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(RegistroMedicamentos.this, "Medicamento guardado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegistroMedicamentos.this, "Error al guardar el medicamento", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}

