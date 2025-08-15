package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TeamsAdapter adapter;
    private List<Team> teamList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_teams, container, false);
        recyclerView = view.findViewById(R.id.teams_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        teamList = new ArrayList<>();
        teamList.add(new Team("Ingeniería en Sistemas", true, new ArrayList<>(), "Fútbol", R.drawable.logo_sistemas));
        teamList.add(new Team("Administración", false, new ArrayList<>(), "Básquetbol", R.drawable.logo_admin));

        adapter = new TeamsAdapter(requireContext(), teamList,
                new TeamsAdapter.OnTeamStatusChangeListener() {
                    @Override
                    public void onStatusChange(Team team, boolean newStatus) {
                        team.setActive(newStatus);
                    }
                },
                new TeamsAdapter.OnTeamClickListener() {
                    @Override
                    public void onTeamClick(Team team) {
                        // Navegar al detalle del equipo
                        TeamDetailFragment teamDetailFragment = TeamDetailFragment.newInstance(team);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, teamDetailFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        return view;
    }
}