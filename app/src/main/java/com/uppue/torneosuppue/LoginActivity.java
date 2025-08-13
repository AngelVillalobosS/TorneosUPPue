package com.uppue.torneosuppue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // ... [código de variables sin cambios]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicialización temprana de Firebase
        FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Posponer la carga pesada
        new Handler(Looper.getMainLooper()).post(() -> {
            emailInput = findViewById(R.id.email_input);
            passwordInput = findViewById(R.id.password_input);
            loginButton = findViewById(R.id.login_button);
            signupLink = findViewById(R.id.signup_link);
            progressBar = findViewById(R.id.progressBar);

            setupListeners();
        });
    }

    // ... [resto del código sin cambios]

    private void fetchUserRole(String userId) {
        if (isFetchingRole) return;

        isFetchingRole = true;
        showProgress(true);

        // Ejecutar en un hilo separado para mejor rendimiento
        new Thread(() -> {
            try {
                DocumentSnapshot document = db.collection("users")
                        .document(userId)
                        .get()
                        .get();

                runOnUiThread(() -> {
                    isFetchingRole = false;
                    showProgress(false);

                    if (document.exists()) {
                        String role = document.getString("role");
                        if (role != null && !role.isEmpty()) {
                            navigateToMainApp(role);
                            return;
                        }
                    }
                    handleRoleNotFound();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isFetchingRole = false;
                    showProgress(false);
                    handleFirestoreError(e);
                });
            }
        }).start();
    }

    // ... [resto del código sin cambios]
}