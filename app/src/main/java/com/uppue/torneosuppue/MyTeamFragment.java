package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTeamFragment extends Fragment {

    private TextView teamName;
    private RecyclerView playersRecycler;
    private Player adapter;
    private View progressContainer;
    private ProgressBar progressBar;
    private TextView teamStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_team, container, false);
        
        teamName = view.findViewById(R.id.teamName);
        playersRecycler = view.findViewById(R.id.playersRecycler);
        teamStatus = view.findViewById(R.id.teamStatus);
        
        // Configurar ProgressBar
        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Configurar RecyclerView
        playersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlayerAdapter(new ArrayList<>());
        playersRecycler.setAdapter(adapter);
        
        // Cargar datos del equipo
        loadTeamData();
        
        return view;
    }

    private void showProgress(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadTeamData() {
        showProgress(true);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance().collection("users")
            .document(user.getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String teamId = document.getString("teamId");
                        if (teamId != null) {
                            loadTeamDetails(teamId);
                        } else {
                            showProgress(false);
                            Toast.makeText(getContext(), "No estás asignado a un equipo", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    showProgress(false);
                    Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadTeamDetails(String teamId) {
        FirebaseFirestore.getInstance().collection("teams")
            .document(teamId)
            .get()
            .addOnCompleteListener(task -> {
                showProgress(false);
                
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Team team = document.toObject(Team.class);
                        if (team != null) {
                            teamName.setText(team.getName());
                            
                            // Actualizar estado
                            if (team.isActive()) {
                                teamStatus.setText("Activo");
                                teamStatus.setBackgroundResource(R.drawable.bg_status_active);
                            } else {
                                teamStatus.setText("Inactivo");
                                teamStatus.setBackgroundResource(R.drawable.bg_status_inactive);
                            }
                            
                            // Cargar jugadores
                            adapter.setPlayers(team.getPlayers());
                        }
                    }
                }
            });
    }
}