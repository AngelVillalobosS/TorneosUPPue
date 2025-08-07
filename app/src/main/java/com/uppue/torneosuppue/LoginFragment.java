package com.uppue.torneosuppue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText email = view.findViewById(R.id.email_input);
        EditText password = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        TextView signupLink = view.findViewById(R.id.signup_link);
        progressBar = view.findViewById(R.id.progressBar);

        // Ocultar progress bar inicialmente
        progressBar.setVisibility(View.GONE);

        loginButton.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            if (emailStr.isEmpty()) {
                email.setError("Email requerido");
                return;
            }

            if (passwordStr.isEmpty()) {
                password.setError("Contraseña requerida");
                return;
            }

            if (passwordStr.length() < 6) {
                password.setError("La contraseña debe tener al menos 8 caracteres");
                return;
            }

            // Mostrar progreso
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            // Autenticar con Firebase
            loginUser(emailStr, passwordStr);
        });

        signupLink.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).showFragment(new SignUpFragment());
        });

        return view;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // OMITIDA la verificación de email

                            // Obtener rol del usuario
                            getUserRole(user.getUid());
                        }
                    } else {
                        // Error en login
                        String errorMessage = "Error en autenticación";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
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

                        // Navegar al fragmento principal
                        navigateToMainApp(role);
                    } else {
                        Toast.makeText(getContext(),
                                "Perfil de usuario no encontrado",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error al obtener información del usuario",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToMainApp(String role) {
        // Obtener referencia a MainActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // Actualizar rol en MainActivity
            mainActivity.userRole = role;

            // Configurar menú de navegación
            mainActivity.setupNavigationMenu();

            // Navegar a la pantalla principal
            mainActivity.showFragment(new TeamsFragment());

            // Marcar el ítem del menú como seleccionado
            mainActivity.navigationView.setCheckedItem(R.id.nav_teams);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Verificar si el usuario ya está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Usuario ya autenticado, obtener rol
            getUserRole(currentUser.getUid());
        }
    }
}