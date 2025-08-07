package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {

    private ListView listView;
    private TeamsAdapter adapter;
    private List<Team> teamList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_teams, container, false);
        listView = view.findViewById(R.id.teams_list_view); // Asegúrate de tener este ID en tu XML

        // Lista inicial de equipos (puede venir de Firestore u otra fuente)
        teamList = new ArrayList<>();
        teamList.add(new Team("Ingeniería en Sistemas", true, new ArrayList<>(), "Fútbol", R.drawable.logo_sistemas));
        teamList.add(new Team("Administración", false, new ArrayList<>(), "Básquetbol", R.drawable.logo_admin));
        // Agrega más equipos si es necesario

        // Crear el adaptador y asignarlo al ListView
        adapter = new TeamsAdapter(requireContext(), teamList, new TeamsAdapter.OnTeamStatusChangeListener() {
            @Override
            public void onStatusChange(Team team, boolean newStatus) {
                team.setActive(newStatus);
                // Aquí podrías guardar el nuevo estado en Firestore si lo deseas
            }
        });

        listView.setAdapter(adapter);

        return view;
    }
}
