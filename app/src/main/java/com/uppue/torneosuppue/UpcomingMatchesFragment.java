// src/main/java/com/example/torneosupp/UpcomingMatchesFragment.java
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

public class UpcomingMatchesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_matches, container, false);

        // Configurar ListView
        ListView listView = view.findViewById(R.id.upcoming_matches_list_view);

        // Obtener partidos próximos
        List<Match> matches = getUpcomingMatches();

        // Configurar adaptador
        MatchesAdapter adapter = new MatchesAdapter(getActivity(), matches, false);
        listView.setAdapter(adapter);

        return view;
    }

    private List<Match> getUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match("Fútbol Soccer", "Ingeniería en Sistemas", "Derecho",
                null, "2023-10-20 10:00", false));
        matches.add(new Match("Básquetbol", "Administración", "Mecatrónica",
                null, "2023-10-21 12:00", false));
        matches.add(new Match("Voleibol", "Contabilidad", "Ingeniería Industrial",
                null, "2023-10-22 15:00", false));
        matches.add(new Match("Béisbol", "Derecho", "Ingeniería en Sistemas",
                null, "2023-10-23 16:00", false));
        return matches;
    }
}