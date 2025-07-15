// src/main/java/com/example/torneosupp/MatchesAdapter.java
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

public class MatchesAdapter extends ArrayAdapter<Match> {
    
    private final Context context;
    private final List<Match> matches;
    private final boolean isRecent;

    public MatchesAdapter(Context context, List<Match> matches, boolean isRecent) {
        super(context, R.layout.item_match, matches);
        this.context = context;
        this.matches = matches;
        this.isRecent = isRecent;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_match, parent, false);
        }

        TextView sport = convertView.findViewById(R.id.match_sport);
        TextView date = convertView.findViewById(R.id.match_date);
        TextView team1 = convertView.findViewById(R.id.team1_name);
        TextView team2 = convertView.findViewById(R.id.team2_name);
        TextView scoreOrTime = convertView.findViewById(R.id.match_score_or_time);

        Match match = matches.get(position);
        sport.setText(match.getSport());
        date.setText(match.getDate());
        team1.setText(match.getTeam1());
        team2.setText(match.getTeam2());

        if (isRecent) {
            scoreOrTime.setText(match.getScore());
        } else {
            // Para partidos próximos, mostramos solo la hora
            String[] dateParts = match.getDate().split(" ");
            String time = dateParts.length > 1 ? dateParts[1] : match.getDate();
            scoreOrTime.setText(time);
        }

        return convertView;
    }
}