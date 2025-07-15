package com.uppue.torneosuppue;
// src/main/java/com/example/torneosupp/SignUpFragment.java

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

public class SignUpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        EditText name = view.findViewById(R.id.name_input);
        EditText email = view.findViewById(R.id.email_input);
        EditText password = view.findViewById(R.id.password_input);
        EditText confirmPassword = view.findViewById(R.id.confirm_password_input);
        Button signupButton = view.findViewById(R.id.signup_button);
        TextView loginLink = view.findViewById(R.id.login_link);

        signupButton.setOnClickListener(v -> {
            // Validar campos (simulación)
            String nameStr = name.getText().toString();
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            String confirmPasswordStr = confirmPassword.getText().toString();

            if (nameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() ||
                !passwordStr.equals(confirmPasswordStr)) {
                // Mostrar error
            } else {
                // Navegar a la pantalla principal
                ((MainActivity) requireActivity()).showFragment(new TeamsFragment());
            }
        });

        loginLink.setOnClickListener(v -> {
            // Navegar a LoginFragment
            ((MainActivity) requireActivity()).showFragment(new LoginFragment());
        });

        return view;
    }
}