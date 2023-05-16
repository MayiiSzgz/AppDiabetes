package com.example.appdiabetes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class historial extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private HistorialGlucemiaAdapter adapter;
    private List<Glucemia> glucemiaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        glucemiaList = new ArrayList<>();
        adapter = new HistorialGlucemiaAdapter(glucemiaList);
        recyclerView.setAdapter(adapter);

        CollectionReference historialCollectionRef = db.collection("Glucemia").document(user.getUid()).collection("Historial");
        historialCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Glucemia glucemia = documentSnapshot.toObject(Glucemia.class);
                        glucemiaList.add(glucemia);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                });
    }
}
