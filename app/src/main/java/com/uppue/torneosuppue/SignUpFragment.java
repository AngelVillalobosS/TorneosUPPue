package com.uppue.torneosuppue;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button signupButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText name = view.findViewById(R.id.name_input);
        EditText email = view.findViewById(R.id.email_input);
        EditText password = view.findViewById(R.id.password_input);
        EditText confirmPassword = view.findViewById(R.id.confirm_password_input);
        Button signupButton = view.findViewById(R.id.signup_button);
        TextView loginLink = view.findViewById(R.id.login_link);
        progressBar = view.findViewById(R.id.progressBar);

        // Ocultar progress bar inicialmente
        progressBar.setVisibility(View.GONE);

        signupButton.setOnClickListener(v -> {
            String nameStr = name.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            String confirmPasswordStr = confirmPassword.getText().toString().trim();

             // Validar nombre (solo letras, espacios, acentos, capitalizado)
            if (TextUtils.isEmpty(nameStr)) {
                name.setError("Nombre requerido");
                return;
            }
            if (!nameStr.matches("^[A-ZÁÉÍÓÚÑ][a-záéíóúñA-ZÁÉÍÓÚÑ ]*$")) {
                name.setError("Solo letras y espacios. Debe iniciar con mayúscula");
                return;
            }
                // Validar correo electrónico (formato básico)
            if (TextUtils.isEmpty(emailStr)) {
                email.setError("Email requerido");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                email.setError("Correo electrónico no válido");
                return;
            }
                // Validar contraseña (mínimo 8, al menos una mayúscula, una minúscula y un número)
            if (TextUtils.isEmpty(passwordStr)) {
                password.setError("Contraseña requerida");
                return;
            }
            if (!passwordStr.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                password.setError("Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número");
                return;
            }
                // Confirmar contraseña
            if (!passwordStr.equals(confirmPasswordStr)) {
                confirmPassword.setError("Las contraseñas no coinciden");
                return;
            }
                // Mostrar progreso
            progressBar.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);

             // Registrar usuario
            registerUser(nameStr, emailStr, passwordStr);

        });

        loginLink.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).showFragment(new LoginFragment());
        });

        return view;
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    // Registro exitoso
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Enviar email de verificación
                        sendVerificationEmail(user, name);
                    }
                } else {
                    // Error en registro
                    progressBar.setVisibility(View.GONE);
                    signupButton.setEnabled(true);

                    String errorMessage = "Error en registro";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("SIGNUP", "Error en registro: ", task.getException());
                }
            });
    }

    private void sendVerificationEmail(FirebaseUser user, String name) {
        user.sendEmailVerification()
            .addOnCompleteListener(emailTask -> {
                if (emailTask.isSuccessful()) {
                    // Crear perfil de usuario en Firestore
                    createUserProfile(user.getUid(), name, user.getEmail());
                } else {
                    progressBar.setVisibility(View.GONE);
                    signupButton.setEnabled(true);

                    String errorMessage = "Error al enviar email de verificación";
                    if (emailTask.getException() != null) {
                        errorMessage = emailTask.getException().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void createUserProfile(String userId, String name, String email) {
        // Crear objeto de usuario
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);
        userProfile.put("role", "player"); // Rol por defecto
        userProfile.put("createdAt", System.currentTimeMillis());

        // Guardar en Firestore
        db.collection("users")
            .document(userId)
            .set(userProfile)
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                signupButton.setEnabled(true);

                if (task.isSuccessful()) {
                    // Éxito al crear perfil
                    Toast.makeText(
                        getContext(),
                        "Registro exitoso. Por favor verifica tu email antes de iniciar sesión.",
                        Toast.LENGTH_LONG
                    ).show();

                    // Volver al login
                    ((MainActivity) requireActivity()).showFragment(new LoginFragment());
                } else {
                    // Error al crear perfil
                    String errorMessage = "Error al crear perfil de usuario";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
    }
}