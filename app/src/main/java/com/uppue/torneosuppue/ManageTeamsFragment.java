package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTeamsFragment extends Fragment {
    private TeamsAdapter adapter;
    private ListView listView;
    private List<Team> teamList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_teams, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.teamsRecycler);


        // Inicializar adaptador con los 3 parámetros requeridos
        adapter = new TeamsAdapter(
                requireContext(),
                teamList,
                new TeamsAdapter.OnTeamStatusChangeListener() {
                    @Override
                    public void onStatusChange(Team team, boolean newStatus) {
                        toggleTeamStatus(team, newStatus);
                    }
                }
        );



        listView.setAdapter(adapter);
        loadTeams();
        return view;
    }

    private void loadTeams() {
        // Cargar equipos desde Firestore
        FirebaseFirestore.getInstance().collection("teams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teamList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Team team = document.toObject(Team.class);
                        team.setId(document.getId());
                        teamList.add(team);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void toggleTeamStatus(Team team, boolean newStatus) {
        // Actualizar Firestore
        FirebaseFirestore.getInstance()
                .collection("teams")
                .document(team.getId())
                .update("active", newStatus)
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}