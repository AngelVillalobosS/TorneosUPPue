package com.uppue.torneosuppue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button loginButton;
    private EditText emailInput, passwordInput;
    private TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar diseño EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configurar insets para la vista principal
        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Authentication y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar AuthStateListener para manejar cambios en la autenticación
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                // Usuario ya autenticado y verificado, obtener rol
                fetchUserRole(user.getUid());
            }
        };

        // Obtener referencias a las vistas
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);
        progressBar = findViewById(R.id.progressBar);

        // Configurar listeners
        setupListeners();
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signupLink.setOnClickListener(v -> navigateToSignUp());
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showProgress(true);
        authenticateUser(email, password);
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            emailInput.setError("Email requerido");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Correo electrónico no válido");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Contraseña requerida");
            isValid = false;
        } else if (password.length() < 8) {
            passwordInput.setError("Debe tener al menos 8 caracteres");
            isValid = false;
        }

        return isValid;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        emailInput.setEnabled(!show);
        passwordInput.setEnabled(!show);
    }

    private void authenticateUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleLoginSuccess();
                    } else {
                        handleLoginFailure(task.getException());
                    }
                });
    }

    private void handleLoginSuccess() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                fetchUserRole(user.getUid());
            } else {
                showProgress(false);
                Toast.makeText(LoginActivity.this,
                        "Por favor verifica tu correo electrónico antes de iniciar sesión",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleLoginFailure(Exception exception) {
        showProgress(false);

        String errorMessage = "Error en autenticación";
        if (exception != null) {
            if (exception instanceof FirebaseAuthInvalidUserException) {
                errorMessage = "No existe cuenta con este correo electrónico";
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                errorMessage = "Credenciales inválidas. Verifica tu correo y contraseña";
            } else {
                errorMessage = exception.getMessage();
            }
            Log.e("LOGIN", "Error en login: " + errorMessage, exception);
        }

        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void fetchUserRole(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if (role != null && !role.isEmpty()) {
                                navigateToMainApp(role);
                            } else {
                                handleRoleNotFound();
                            }
                        } else {
                            handleRoleNotFound();
                        }
                    } else {
                        handleFirestoreError(task.getException());
                    }
                });
    }

    private void handleRoleNotFound() {
        showProgress(false);
        Toast.makeText(LoginActivity.this,
                "Perfil de usuario no encontrado",
                Toast.LENGTH_SHORT).show();
    }

    private void handleFirestoreError(Exception exception) {
        showProgress(false);
        String error = "Error al obtener información del usuario";
        if (exception != null) {
            error += ": " + exception.getMessage();
            Log.e("FIRESTORE", "Error en Firestore", exception);
        }
        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainApp(String role) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userRole", role);
        startActivity(intent);
        finish(); // Cierra esta actividad para evitar volver atrás
    }

    private void navigateToSignUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Registrar el AuthStateListener
        mAuth.addAuthStateListener(authStateListener);

        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            fetchUserRole(currentUser.getUid());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remover el AuthStateListener cuando la actividad no está visible
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}