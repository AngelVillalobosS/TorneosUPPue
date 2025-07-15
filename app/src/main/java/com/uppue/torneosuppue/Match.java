package com.uppue.torneosuppue;


import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable {
    private String sport;
    private String team1;
    private String team2;
    private String score;
    private String date;
    private boolean completed;

    public Match(String sport, String team1, String team2, String score, String date, boolean completed) {
        this.sport = sport;
        this.team1 = team1;
        this.team2 = team2;
        this.score = score;
        this.date = date;
        this.completed = completed;
    }

    protected Match(Parcel in) {
        sport = in.readString();
        team1 = in.readString();
        team2 = in.readString();
        score = in.readString();
        date = in.readString();
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

    public String getSport() { return sport; }
    public String getTeam1() { return team1; }
    public String getTeam2() { return team2; }
    public String getScore() { return score; }
    public String getDate() { return date; }
    public boolean isCompleted() { return completed; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sport);
        dest.writeString(team1);
        dest.writeString(team2);
        dest.writeString(score);
        dest.writeString(date);
        dest.writeByte((byte) (completed ? 1 : 0));
    }
}
