package com.uppue.torneosuppue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teams, container, false);

        // Configurar ListView
        ListView listView = view.findViewById(R.id.teams_list_view);

        // Obtener lista de equipos
        List<Team> teams = getTeams();

        // Configurar adaptador
        TeamsAdapter adapter = new TeamsAdapter(getActivity(), teams);
        listView.setAdapter(adapter);

        // Configurar clic en elemento
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team selectedTeam = teams.get(position);
                TeamDetailFragment fragment = TeamDetailFragment.newInstance(selectedTeam);

                // Navegar usando la MainActivity
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(fragment);
                }
            }
        });

        // En TeamsFragment
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("NAVIGATION", "Clic en equipo: " + position);
        Team selectedTeam = teams.get(position);
        Log.d("NAVIGATION", "Equipo seleccionado: " + selectedTeam.getName());

        TeamDetailFragment fragment = TeamDetailFragment.newInstance(selectedTeam);
        Log.d("NAVIGATION", "Fragment creado");

        if (getActivity() instanceof MainActivity) {
            Log.d("NAVIGATION", "Llamando a showFragment");
            ((MainActivity) getActivity()).showFragment(fragment);
        }
    }
});

        return view;
    }

    private List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("Fútbol Soccer", "Ingeniería en Sistemas", R.drawable.ic_soccer));
        teams.add(new Team("Básquetbol", "Administración", R.drawable.ic_basketball));
        teams.add(new Team("Voleibol", "Derecho", R.drawable.ic_volleyball));
        teams.add(new Team("Béisbol", "Contabilidad", R.drawable.ic_baseball));
        teams.add(new Team("Fútbol Rápido", "Mecatrónica", R.drawable.ic_futsal));
        return teams;
    }

}