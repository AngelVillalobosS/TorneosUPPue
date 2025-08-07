package com.uppue.torneosuppue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    private final List<Player> players;

    public PlayersAdapter(List<Player> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.playerName.setText(player.getName());
        holder.playerPosition.setText(player.getPosition());
        holder.playerNumber.setText("#" + player.getNumber());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        TextView playerPosition;
        TextView playerNumber;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            playerPosition = itemView.findViewById(R.id.player_position);
            playerNumber = itemView.findViewById(R.id.player_number);
        }
    }

    // Método para actualizar los datos
    public void updatePlayers(List<Player> newPlayers) {
        players.clear();
        players.addAll(newPlayers);
        notifyDataSetChanged();
    }

    // Método setPlayers como alias de updatePlayers
    public void setPlayers(List<Player> newPlayers) {
        updatePlayers(newPlayers);
    }
}
