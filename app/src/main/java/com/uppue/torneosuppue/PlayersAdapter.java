// src/main/java/com/example/torneosupp/PlayersAdapter.java
package com.uppue.torneosuppue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayersAdapter extends ArrayAdapter<Player> {

    private final Context context;
    private final List<Player> players;

    public PlayersAdapter(Context context, List<Player> players) {
        super(context, R.layout.item_player, players);
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_player, parent, false);

            holder = new ViewHolder();
            holder.playerName = convertView.findViewById(R.id.player_name);
            holder.playerPosition = convertView.findViewById(R.id.player_position);
            holder.playerNumber = convertView.findViewById(R.id.player_number);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Player player = players.get(position);
        holder.playerName.setText(player.getName());
        holder.playerPosition.setText(player.getPosition());
        holder.playerNumber.setText("#" + player.getNumber());

        return convertView;
    }

    static class ViewHolder {
        TextView playerName;
        TextView playerPosition;
        TextView playerNumber;
    }
}