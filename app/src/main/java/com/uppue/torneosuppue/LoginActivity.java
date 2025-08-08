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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar EdgeToEdge (diseño de borde a borde)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);  // Asegúrate de usar activity_login

        // Configurar insets (márgenes para evitar superposición con barras del sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener referencias a las vistas
        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        progressBar = findViewById(R.id.progressBar);

        // Ocultar progress bar inicialmente
        progressBar.setVisibility(View.GONE);

        // Configurar listener para el botón de login
        loginButton.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            // Validaciones
            if (emailStr.isEmpty()) {
                email.setError("Email requerido");
                return;
            }

            if (passwordStr.isEmpty()) {
                password.setError("Contraseña requerida");
                return;
            }

            if (passwordStr.length() < 8) {  // Cambiado de 6 a 8 caracteres mínimo
                password.setError("La contraseña debe tener al menos 8 caracteres");
                return;
            }

            // Mostrar progreso y deshabilitar botón
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            // Autenticar con Firebase
            loginUser(emailStr, passwordStr);
        });

        // Configurar listener para el enlace de registro
        signupLink.setOnClickListener(v -> {
            // Navegar a SignUpActivity
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Verificar si el correo está verificado
                            if (user.isEmailVerified()) {
                                // Obtener rol del usuario
                                getUserRole(user.getUid());
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Por favor verifica tu correo electrónico antes de iniciar sesión",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        // Error en login
                        String errorMessage = "Error en autenticación";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("LOGIN", "Error en login: ", task.getException());
                    }
                });
    }

    private void getUserRole(String userId) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        navigateToMainApp(role);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Perfil de usuario no encontrado",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Error al obtener información del usuario",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToMainApp(String role) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userRole", role);
        startActivity(intent);
        finish(); // Cierra LoginActivity para que no pueda volver atrás
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Usuario ya autenticado, obtener rol
            getUserRole(currentUser.getUid());
        }
    }
}