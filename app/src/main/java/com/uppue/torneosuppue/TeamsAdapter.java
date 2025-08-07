package com.uppue.torneosuppue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.uppue.torneosuppue.TeamsAdapter.OnTeamStatusChangeListener;

import java.util.List;

public class TeamsAdapter extends ArrayAdapter<Team> {

    // Interfaz para manejar cambios de estado
    public interface OnTeamStatusChangeListener {
        void onStatusChange(Team team, boolean newStatus);
    }

    private final Context context;
    private final List<Team> teams;
    private final OnTeamStatusChangeListener listener;

    // Constructor modificado para incluir el listener
    public TeamsAdapter(Context context, List<Team> teams, OnTeamStatusChangeListener listener) {
        super(context, R.layout.item_team, teams);
        this.context = context;
        this.teams = teams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_team, parent, false);

            holder = new ViewHolder();
            holder.teamLogo = convertView.findViewById(R.id.team_logo);
            holder.teamSport = convertView.findViewById(R.id.team_sport);
            holder.teamName = convertView.findViewById(R.id.team_name);
            holder.switchActive = convertView.findViewById(R.id.switchActive); // Agregar esto

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Team team = teams.get(position);
        holder.teamLogo.setImageResource(team.getLogoResId());
        holder.teamSport.setText(team.getSport());
        holder.teamName.setText(team.getName());

        // Configurar el switch
        holder.switchActive.setChecked(team.isActive());
        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChange(team, isChecked);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView teamLogo;
        TextView teamSport;
        TextView teamName;
        SwitchMaterial switchActive; // Nuevo componente
    }
}