package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private RecyclerView teamsRecycler;
    private TeamsAdapter adapter;
    private List<Team> teamList = new ArrayList<>();
    private View progressContainer;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_teams, container, false);
        
        // Configurar RecyclerView
        teamsRecycler = view.findViewById(R.id.teamsRecycler);
        teamsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Configurar ProgressBar
        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Inicializar adaptador
        adapter = new TeamAdapter(teamList, this::toggleTeamStatus);
        teamsRecycler.setAdapter(adapter);
        
        // Cargar equipos
        loadTeams();
        
        return view;
    }

    private void showProgress(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadTeams() {
        showProgress(true);
        
        FirebaseFirestore.getInstance().collection("teams")
            .get()
            .addOnCompleteListener(task -> {
                showProgress(false);
                
                if (task.isSuccessful()) {
                    teamList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Team team = doc.toObject(Team.class);
                        team.setId(doc.getId());
                        teamList.add(team);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar equipos", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void toggleTeamStatus(Team team) {
        showProgress(true);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", !team.isActive());
        
        FirebaseFirestore.getInstance().collection("teams")
            .document(team.getId())
            .update(updates)
            .addOnCompleteListener(task -> {
                showProgress(false);
                
                if (task.isSuccessful()) {
                    team.setActive(!team.isActive());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar equipo", Toast.LENGTH_SHORT).show();
                }
            });
    }
}