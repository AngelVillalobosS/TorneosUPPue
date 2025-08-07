package com.uppue.torneosuppue;

import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable {
    private String id;
    private String teamId;
    private String opponent;
    private String date;
    private String result;
    private boolean reported;
    private String sport;
    private String team1;
    private String team2;
    private String score;
    private boolean completed;

    // Constructor vacío para Firestore
    public Match() {}

    // Constructor completo
    public Match(String id, String teamId, String opponent, String date,
                 String result, boolean reported, String sport,
                 String team1, String team2, String score, boolean completed) {
        this.id = id;
        this.teamId = teamId;
        this.opponent = opponent;
        this.date = date;
        this.result = result;
        this.reported = reported;
        this.sport = sport;
        this.team1 = team1;
        this.team2 = team2;
        this.score = score;
        this.completed = completed;
    }

    // Constructor desde Parcel
    protected Match(Parcel in) {
        id = in.readString();
        teamId = in.readString();
        opponent = in.readString();
        date = in.readString();
        result = in.readString();
        reported = in.readByte() != 0;
        sport = in.readString();
        team1 = in.readString();
        team2 = in.readString();
        score = in.readString();
        completed = in.readByte() != 0;
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(teamId);
        dest.writeString(opponent);
        dest.writeString(date);
        dest.writeString(result);
        dest.writeByte((byte) (reported ? 1 : 0));
        dest.writeString(sport);
        dest.writeString(team1);
        dest.writeString(team2);
        dest.writeString(score);
        dest.writeByte((byte) (completed ? 1 : 0));
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getOpponent() { return opponent; }
    public void setOpponent(String opponent) { this.opponent = opponent; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public boolean isReported() { return reported; }
    public void setReported(boolean reported) { this.reported = reported; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getTeam1() { return team1; }
    public void setTeam1(String team1) { this.team1 = team1; }

    public String getTeam2() { return team2; }
    public void setTeam2(String team2) { this.team2 = team2; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return date + " vs " + opponent;
    }
}