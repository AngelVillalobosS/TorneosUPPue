package com.uppue.torneosuppue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up); // Cambiado a activity_sign_up

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText name = findViewById(R.id.name_input);
        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);
        EditText confirmPassword = findViewById(R.id.confirm_password_input);
        signupButton = findViewById(R.id.signup_button);
        TextView loginLink = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        signupButton.setOnClickListener(v -> {
            String nameStr = name.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            String confirmPasswordStr = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(nameStr)) {
                name.setError("Nombre requerido");
                return;
            }
            if (!nameStr.matches("^[A-ZÁÉÍÓÚÑ][a-záéíóúñA-ZÁÉÍÓÚÑ ]*$")) {
                name.setError("Solo letras y espacios. Debe iniciar con mayúscula");
                return;
            }
            if (TextUtils.isEmpty(emailStr)) {
                email.setError("Email requerido");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                email.setError("Correo electrónico no válido");
                return;
            }
            if (TextUtils.isEmpty(passwordStr)) {
                password.setError("Contraseña requerida");
                return;
            }
            if (!passwordStr.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                password.setError("Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número");
                return;
            }
            if (!passwordStr.equals(confirmPasswordStr)) {
                confirmPassword.setError("Las contraseñas no coinciden");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);
            registerUser(nameStr, emailStr, passwordStr);
        });

        loginLink.setOnClickListener(v -> {
            // Navegar a LoginActivity
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        sendVerificationEmail(user, name);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    signupButton.setEnabled(true);

                    String errorMessage = "Error en registro";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("SIGNUP", "Error en registro: ", task.getException());
                }
            });
    }

    private void sendVerificationEmail(FirebaseUser user, String name) {
        user.sendEmailVerification()
            .addOnCompleteListener(emailTask -> {
                if (emailTask.isSuccessful()) {
                    createUserProfile(user.getUid(), name, user.getEmail());
                } else {
                    progressBar.setVisibility(View.GONE);
                    signupButton.setEnabled(true);

                    String errorMessage = "Error al enviar email de verificación";
                    if (emailTask.getException() != null) {
                        errorMessage = emailTask.getException().getMessage();
                    }
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void createUserProfile(String userId, String name, String email) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);
        userProfile.put("role", "player");
        userProfile.put("createdAt", System.currentTimeMillis());

        db.collection("users")
            .document(userId)
            .set(userProfile)
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                signupButton.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(
                        SignUpActivity.this,
                        "Registro exitoso. Por favor verifica tu email antes de iniciar sesión.",
                        Toast.LENGTH_LONG
                    ).show();

                    // Volver a LoginActivity
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String errorMessage = "Error al crear perfil de usuario";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
    }
}