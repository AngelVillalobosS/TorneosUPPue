// src/main/java/com/example/torneosupp/RecentMatchesFragment.java
package com.uppue.torneosuppue;

import android.os.Bundle;
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

public class RecentMatchesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_matches, container, false);

        // Configurar ListView
        ListView listView = view.findViewById(R.id.recent_matches_list_view);

        // Obtener partidos recientes
        List<Match> matches = getRecentMatches();

        // Configurar adaptador
        MatchesAdapter adapter = new MatchesAdapter(getActivity(), matches, true);
        listView.setAdapter(adapter);

        // Configurar clic en elemento
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Match selectedMatch = matches.get(position);
            MatchDetailFragment fragment = MatchDetailFragment.newInstance(selectedMatch);
            ((MainActivity) requireActivity()).showFragment(fragment);
        });

        return view;
    }

    private List<Match> getRecentMatches() {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match("Fútbol Soccer", "Ingeniería en Sistemas", "Administración",
                "3-1", "2023-10-15", true));
        matches.add(new Match("Básquetbol", "Derecho", "Contabilidad",
                "75-68", "2023-10-14", true));
        matches.add(new Match("Voleibol", "Mecatrónica", "Ingeniería Industrial",
                "2-1", "2023-10-13", true));
        matches.add(new Match("Béisbol", "Administración", "Derecho",
                "5-3", "2023-10-12", true));
        return matches;
    }
}