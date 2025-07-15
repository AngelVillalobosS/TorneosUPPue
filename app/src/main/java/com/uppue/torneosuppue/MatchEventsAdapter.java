package com.uppue.torneosuppue;
// src/main/java/com/example/torneosupp/MatchEventsAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchEventsAdapter extends RecyclerView.Adapter<MatchEventsAdapter.MatchEventViewHolder> {

    private List<MatchEvent> events;

    public MatchEventsAdapter(List<MatchEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public MatchEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_event, parent, false);
        return new MatchEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchEventViewHolder holder, int position) {
        MatchEvent event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class MatchEventViewHolder extends RecyclerView.ViewHolder {
        private TextView eventType;
        private TextView eventPlayer;
        private TextView eventTeam;
        private TextView eventTime;

        public MatchEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventType = itemView.findViewById(R.id.event_type);
            eventPlayer = itemView.findViewById(R.id.event_player);
            eventTeam = itemView.findViewById(R.id.event_team);
            eventTime = itemView.findViewById(R.id.event_time);
        }

        public void bind(MatchEvent event) {
            eventType.setText(event.getType());
            eventPlayer.setText(event.getPlayer());
            eventTeam.setText("Equipo " + event.getTeam());
            eventTime.setText(event.getTime());
        }
    }
}