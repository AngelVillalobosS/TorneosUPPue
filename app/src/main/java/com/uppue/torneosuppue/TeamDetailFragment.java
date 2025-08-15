package com.uppue.torneosuppue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        RecyclerView recyclerView = view.findViewById(R.id.players_recycler_view);

        // Usar GridLayoutManager para mostrar 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_TEAM)) {
            Team team = args.getParcelable(ARG_TEAM);

            if (team != null) {
                TextView teamName = view.findViewById(R.id.team_name);
                TextView teamSport = view.findViewById(R.id.team_sport);
                teamName.setText(team.getName());
                teamSport.setText(team.getSport());

                List<Player> players = getPlayers(team);
                PlayersAdapter adapter = new PlayersAdapter(players);
                recyclerView.setAdapter(adapter);
            }
        } else {
            Toast.makeText(getContext(), "Error al cargar el equipo", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFragment(new TeamsFragment());
            }
        }

        return view;
    }

    private List<Player> getPlayers(Team team) {
        // Mismo contenido que antes...
    }
}