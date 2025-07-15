// src/main/java/com/example/torneosupp/LoginFragment.java
package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText email = view.findViewById(R.id.email_input);
        EditText password = view.findViewById(R.id.password_input);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView signupLink = view.findViewById(R.id.signup_link);

        loginButton.setOnClickListener(v -> {
            // Validar credenciales (simulación)
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();

            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                // Mostrar error
            } else {
                // Navegar a la pantalla principal
                ((MainActivity) requireActivity()).showFragment(new TeamsFragment());
            }
        });

        signupLink.setOnClickListener(v -> {
            // Navegar a SignUpFragment
            ((MainActivity) requireActivity()).showFragment(new SignUpFragment());
        });

        return view;
    }
}