package com.uppue.torneosuppue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportResultFragment extends Fragment {

    private Spinner matchSpinner;
    private Spinner resultSpinner;
    private Button submitButton;
    private View progressContainer;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_result, container, false);
        
        matchSpinner = view.findViewById(R.id.matchSpinner);
        resultSpinner = view.findViewById(R.id.resultSpinner);
        submitButton = view.findViewById(R.id.submitButton);
        
        // Configurar ProgressBar
        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Configurar spinner de resultados
        ArrayAdapter<CharSequence> resultAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.result_options,
                android.R.layout.simple_spinner_item
        );
        resultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resultSpinner.setAdapter(resultAdapter);
        
        // Cargar partidos del equipo
        loadTeamMatches();
        
        // Configurar botón de envío
        submitButton.setOnClickListener(v -> submitResult());
        
        return view;
    }

    private void showProgress(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadTeamMatches() {
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
                            loadMatches(teamId);
                        }
                    }
                } else {
                    showProgress(false);
                }
            });
    }

    private void loadMatches(String teamId) {
        FirebaseFirestore.getInstance().collection("matches")
            .whereEqualTo("teamId", teamId)
            .whereEqualTo("reported", false) // Solo partidos no reportados
            .get()
            .addOnCompleteListener(task -> {
                showProgress(false);
                
                if (task.isSuccessful()) {
                    List<Match> matches = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Match match = doc.toObject(Match.class);
                        match.setId(doc.getId());
                        matches.add(match);
                    }
                    
                    // Configurar spinner de partidos
                    ArrayAdapter<Match> matchAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            matches
                    );
                    matchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    matchSpinner.setAdapter(matchAdapter);
                } else {
                    Toast.makeText(getContext(), "Error al cargar partidos", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void submitResult() {
        Match selectedMatch = (Match) matchSpinner.getSelectedItem();
        String result = resultSpinner.getSelectedItem().toString();
        
        if (selectedMatch == null) {
            Toast.makeText(getContext(), "Selecciona un partido", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showProgress(true);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("result", result);
        updates.put("reported", true);
        
        FirebaseFirestore.getInstance().collection("matches")
            .document(selectedMatch.getId())
            .update(updates)
            .addOnCompleteListener(task -> {
                showProgress(false);
                
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Resultado reportado con éxito", Toast.LENGTH_SHORT).show();
                    // Recargar partidos o volver atrás
                    loadTeamMatches();
                } else {
                    Toast.makeText(getContext(), "Error al reportar resultado", Toast.LENGTH_SHORT).show();
                }
            });
    }
}