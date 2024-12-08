package com.example.comunicacion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailInput, passwordInput, nameInput, birthdateInput;
    private RadioGroup genderGroup;
    private Button emailRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth y Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias de vista
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput = findViewById(R.id.nameInput);
        birthdateInput = findViewById(R.id.birthdateInput);
        genderGroup = findViewById(R.id.genderGroup);
        Button googleSignInButton = findViewById(R.id.googleSignInButton);
        emailRegisterButton = findViewById(R.id.emailRegisterButton);

        // Botón para registro con Google
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Botón para registro con correo y contraseña
        emailRegisterButton.setOnClickListener(v -> registerWithEmail());
    }

    private void registerWithEmail() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Ingrese un correo válido");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Continuar con el formulario adicional
                showAdditionalForm();
            } else {
                Toast.makeText(this, "Error al registrar con correo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Continuar con el formulario adicional
                showAdditionalForm();
            } else {
                Toast.makeText(this, "Error en autenticación con Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAdditionalForm() {
        // Validar datos del formulario adicional
        String name = nameInput.getText().toString().trim();
        String birthdate = birthdateInput.getText().toString().trim();
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        String gender = selectedGenderId == R.id.genderMale ? "Masculino" : "Femenino";

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Ingrese su nombre");
            return;
        }
        if (TextUtils.isEmpty(birthdate)) {
            birthdateInput.setError("Ingrese su fecha de nacimiento");
            return;
        }
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Seleccione un género", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos listos para enviar a la base de datos
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Aquí puedes guardar los datos en la base de datos
            // Redirigir a la actividad principal
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
