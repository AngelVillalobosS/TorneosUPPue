package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Datos de perfil (simulación)
        TextView name = view.findViewById(R.id.profile_name);
        TextView email = view.findViewById(R.id.profile_email);
        TextView role = view.findViewById(R.id.profile_role);
        TextView team = view.findViewById(R.id.profile_team);
        TextView sport = view.findViewById(R.id.profile_sport);
        TextView teamRole = view.findViewById(R.id.profile_team_role);

        name.setText("Juan Pérez");
        email.setText("juan.perez@upp.edu.mx");
        role.setText("Capitán de equipo");
        team.setText("Ingeniería en Sistemas");
        sport.setText("Fútbol Soccer");
        teamRole.setText("Capitán");

        // Botones
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        Button changePasswordButton = view.findViewById(R.id.change_password_button);

        editProfileButton.setOnClickListener(v -> {
            // Navegar a la pantalla de edición de perfil
            // ((MainActivity) requireActivity()).showFragment(new EditProfileFragment());
            Toast.makeText(getContext(), "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show();
        });

        changePasswordButton.setOnClickListener(v -> {
            // Navegar a la pantalla de cambio de contraseña
            // ((MainActivity) requireActivity()).showFragment(new ChangePasswordFragment());
            Toast.makeText(getContext(), "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}