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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button loginButton;
    private EditText emailInput, passwordInput;
    private TextView signupLink;
    private boolean isFetchingRole = false;
    private boolean shouldCheckUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas directamente
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);
        progressBar = findViewById(R.id.progressBar);

        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldCheckUser) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d("DEBUG", "onResume() iniciado");
                Log.d("DEBUG", "Usuario actual: " + (currentUser != null ? currentUser.getUid() : "null"));

                fetchUserRole(currentUser.getUid());
            }
            // Bloquea futuros chequeos automáticos
            shouldCheckUser = false;
        }
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
            fetchUserRole(user.getUid());
        } else {
            showProgress(false);
            Toast.makeText(LoginActivity.this,
                    "Error al obtener usuario",
                    Toast.LENGTH_SHORT).show();
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
        if (isFetchingRole) return;

        isFetchingRole = true;
        showProgress(true);

        // Usa el enfoque directo con listeners de Firestore
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    isFetchingRole = false;
                    showProgress(false);

                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            Log.d("DEBUG", "Rol obtenido: " + role);
                            navigateToMainApp(role);
                        } else {
                            Log.e("DEBUG", "Documento de usuario no encontrado");
                            mAuth.signOut();
                        }
                    } else {
                        handleRoleNotFound();
                        handleFirestoreError(task.getException());
                    }
                });
    }

    private void handleRoleNotFound() {
        // Cerrar sesión y permitir nuevo chequeo
        mAuth.signOut();
        shouldCheckUser = true; // Permite volver a chequear
        Toast.makeText(this, "Perfil de usuario no encontrado", Toast.LENGTH_SHORT).show();
    }

    private void handleFirestoreError(Exception exception) {
        // Cerrar sesión y permitir nuevo chequeo
        mAuth.signOut();
        shouldCheckUser = true; // Permite volver a chequear
        Toast.makeText(this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainApp(String role) {
        if (role == null || role.isEmpty()) {
            Toast.makeText(this, "No se pudo obtener el rol del usuario", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            return;
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_ROLE", role);
        startActivity(intent);
        finish();
    }

    private void navigateToSignUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }


}