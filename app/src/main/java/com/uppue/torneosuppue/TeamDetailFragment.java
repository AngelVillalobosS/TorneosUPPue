package com.uppue.torneosuppue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailFragment extends Fragment {

    private static final String ARG_TEAM = "team";

    public static TeamDetailFragment newInstance(Team team) {
        TeamDetailFragment fragment = new TeamDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEAM, team);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("NAVIGATION", "TeamDetailFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_team_detail, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_TEAM)) {
            Team team = args.getParcelable(ARG_TEAM);

            if (team != null) {
                // Configurar datos del equipo
                TextView teamName = view.findViewById(R.id.team_name);
                TextView teamSport = view.findViewById(R.id.team_sport);
                teamName.setText(team.getName());
                teamSport.setText(team.getSport());

                // Configurar ListView de jugadores
                ListView listView = view.findViewById(R.id.players_list_view);

                // Obtener jugadores del equipo
                List<Player> players = getPlayers(team);

                // Configurar adaptador
                PlayersAdapter adapter = new PlayersAdapter(getActivity(), players);
                listView.setAdapter(adapter);
            }
        } else {
            // Manejar caso de argumentos faltantes
            Toast.makeText(getContext(), "Error al cargar el equipo", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFragment(new TeamsFragment());
            }
        }

        return view;
    }

    private List<Player> getPlayers(Team team) {
        List<Player> players = new ArrayList<>();
        if (team.getSport().equals("Fútbol Soccer")) {
            players.add(new Player("Juan Pérez", "Delantero", "10"));
            players.add(new Player("Carlos López", "Portero", "1"));
            players.add(new Player("Miguel Ángel", "Defensa", "4"));
            players.add(new Player("Roberto Martínez", "Medio", "8"));
            players.add(new Player("Luis García", "Delantero", "9"));
        } else if (team.getSport().equals("Básquetbol")) {
            players.add(new Player("Antonio Rodríguez", "Base", "7"));
            players.add(new Player("Fernando Gómez", "Alero", "11"));
            players.add(new Player("Ricardo Sánchez", "Pívot", "15"));
        } else {
            players.add(new Player("Jugador 1", "Posición", "1"));
            players.add(new Player("Jugador 2", "Posición", "2"));
            players.add(new Player("Jugador 3", "Posición", "3"));
        }
        return players;
    }
}