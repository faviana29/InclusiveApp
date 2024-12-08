package com.example.comunicacion;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, birthdateTextView, genderTextView, emailTextView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Referencias a los TextViews
        nameTextView = findViewById(R.id.nameTextView);
        birthdateTextView = findViewById(R.id.birthdateTextView);
        genderTextView = findViewById(R.id.genderTextView);
        emailTextView = findViewById(R.id.emailTextView);

        // Inicializa FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cargar datos del usuario
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Obtener los datos desde Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String birthdate = documentSnapshot.getString("birthdate");
                            String gender = documentSnapshot.getString("gender");
                            String email = documentSnapshot.getString("email");

                            // Asignar datos a los TextViews
                            nameTextView.setText(name);
                            birthdateTextView.setText(birthdate);
                            genderTextView.setText(gender);
                            emailTextView.setText(email);
                        } else {
                            Log.d("ProfileActivity", "No se encontraron datos para el usuario");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ProfileActivity", "Error al obtener datos del usuario", e));
        }
    }
}
