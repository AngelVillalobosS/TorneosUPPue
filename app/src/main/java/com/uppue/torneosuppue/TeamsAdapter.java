package com.uppue.torneosuppue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamViewHolder> {

    public interface OnTeamStatusChangeListener {
        void onStatusChange(Team team, boolean newStatus);
    }

    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    private final Context context;
    private final List<Team> teams;
    private final OnTeamStatusChangeListener statusListener;
    private final OnTeamClickListener clickListener;

    public TeamsAdapter(Context context, List<Team> teams,
                        OnTeamStatusChangeListener statusListener,
                        OnTeamClickListener clickListener) {
        this.context = context;
        this.teams = teams;
        this.statusListener = statusListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_card, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);

        holder.teamLogo.setImageResource(team.getLogoResId());
        holder.teamSport.setText(team.getSport());
        holder.teamName.setText(team.getName());
        holder.switchActive.setChecked(team.isActive());

        // Configurar el switch
        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (statusListener != null) {
                statusListener.onStatusChange(team, isChecked);
            }
        });

        // Configurar el clic en la card
        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTeamClick(team);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView teamLogo;
        TextView teamSport;
        TextView teamName;
        SwitchMaterial switchActive;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.team_card);
            teamLogo = itemView.findViewById(R.id.team_logo);
            teamSport = itemView.findViewById(R.id.team_sport);
            teamName = itemView.findViewById(R.id.team_name);
            switchActive = itemView.findViewById(R.id.switchActive);
        }
    }
}