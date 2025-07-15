// src/main/java/com/example/torneosupp/MatchDetailFragment.java
package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchDetailFragment extends Fragment {

    private static final String ARG_MATCH = "match";

    public static MatchDetailFragment newInstance(Match match) {
        MatchDetailFragment fragment = new MatchDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MATCH, match);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_detail, container, false);

        if (getArguments() != null) {
            Match match = getArguments().getParcelable(ARG_MATCH);

            if (match != null) {
                // Configurar datos del partido
                TextView sport = view.findViewById(R.id.match_sport);
                TextView date = view.findViewById(R.id.match_date);
                TextView team1 = view.findViewById(R.id.team1_name);
                TextView team2 = view.findViewById(R.id.team2_name);
                TextView score = view.findViewById(R.id.match_score);

                sport.setText(match.getSport());
                date.setText(match.getDate());
                team1.setText(match.getTeam1());
                team2.setText(match.getTeam2());
                score.setText(match.getScore());

                // Configurar RecyclerView de eventos
                RecyclerView recyclerView = view.findViewById(R.id.events_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                // Obtener eventos del partido (datos de prueba)
                List<MatchEvent> events = getMatchEvents(match);

                // Configurar adaptador
                MatchEventsAdapter adapter = new MatchEventsAdapter(events);
                recyclerView.setAdapter(adapter);
            }
        }

        return view;
    }

    private List<MatchEvent> getMatchEvents(Match match) {
        List<MatchEvent> events = new ArrayList<>();
        if (match.getSport().equals("Fútbol Soccer")) {
            events.add(new MatchEvent("Gol", "Juan Pérez", "1", "25'"));
            events.add(new MatchEvent("Gol", "Carlos López", "1", "42'"));
            events.add(new MatchEvent("Falta", "Miguel Ángel", "1", "38'"));
            events.add(new MatchEvent("Gol", "Roberto Martínez", "2", "55'"));
            events.add(new MatchEvent("Gol", "Luis García", "1", "78'"));
            events.add(new MatchEvent("Tarjeta amarilla", "Fernando Gómez", "2", "65'"));
        } else if (match.getSport().equals("Básquetbol")) {
            events.add(new MatchEvent("Canasta de 2 puntos", "Antonio Rodríguez", "1", "Q1 05:32"));
            events.add(new MatchEvent("Canasta de 3 puntos", "Fernando Gómez", "2", "Q2 02:15"));
            events.add(new MatchEvent("Falta personal", "Ricardo Sánchez", "1", "Q3 08:45"));
        }
        return events;
    }
}