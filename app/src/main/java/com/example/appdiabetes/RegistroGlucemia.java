package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RegistroGlucemia extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;

    private EditText valorEditText;
    private RadioGroup unidadRadioGroup;
    private TextView fechaHora;
    private EditText estadoEditText;
    private Button enviarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_glucemia);

        // Ocultar la barra de navegación y habilitar el modo inmersivo
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        valorEditText = findViewById(R.id.editTextNumber);
        unidadRadioGroup = findViewById(R.id.unidad); // Agregado: obtener la referencia al RadioGroup
        fechaHora = findViewById(R.id.fechaHora);
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
                String fechaHoraString = fechaHora.getText().toString();
                Date fechaHora = null;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss 'UTC-6'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC-6"));
                    fechaHora = dateFormat.parse(fechaHoraString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Obtener el estado de la persona en el momento de la medición
                String estado = estadoEditText.getText().toString();

                // Verificar que se haya podido obtener la fecha y la hora correctamente
                if (fechaHora != null) {
                    // Crear un nuevo registro de glucemia
                    Map<String, Object> glucemia = new HashMap<>();
                    glucemia.put("valor", valor);
                    glucemia.put("unidad", unidad);
                    glucemia.put("fechaHora", fechaHora);
                    glucemia.put("fechaHoraActual", new Date());
                    glucemia.put("estado", estado);

                    // Obtener la referencia al documento del usuario actual
                    DocumentReference docRef = db.collection("Glucemia").document(user.getUid());

                    // Obtener la referencia a la colección "Historial" dentro de la colección "Glucemia" del usuario actual
                    CollectionReference historialCollectionRef = db.collection("Glucemia").document(user.getUid()).collection("Historial");

                    // Agregar el nuevo registro de glucemia al historial del usuario correspondiente
                    String finalUnidad = unidad;
                    Date finalFechaHora = fechaHora;
                    historialCollectionRef.add(glucemia)
                            .addOnSuccessListener(documentReference -> {
                                // Analizar el nivel de glucemia y mostrar el resultado en un mensaje
                                Glucemia glucemiaActual = new Glucemia(valor, finalUnidad, finalFechaHora, estado);
                                String analisis = glucemiaActual.analizarNivelGlucemia();
                                Toast.makeText(getApplicationContext(), "Nivel de glucemia: " + analisis, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al agregar registro de glucemia", Toast.LENGTH_SHORT).show();
                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Fecha y hora inválidas", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //al presionar el boton historial abre la clase historial
        Button btnHistorial = findViewById(R.id.historial);
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroGlucemia.this, historial.class);
            startActivity(intent);
        });
    }
    public void showDateTimePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                Calendar selectedDateTime = Calendar.getInstance();
                selectedDateTime.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss 'UTC-6'");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC-6"));
                String fechaHoraString = dateFormat.format(selectedDateTime.getTime());

                TextView fechaHoraEditText = findViewById(R.id.fechaHora);
                fechaHoraEditText.setText(fechaHoraString);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }


}
