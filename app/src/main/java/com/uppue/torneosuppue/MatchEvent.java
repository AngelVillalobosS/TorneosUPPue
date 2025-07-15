package com.uppue.torneosuppue;

import android.os.Parcel;
import android.os.Parcelable;

public class MatchEvent implements Parcelable {
    private String type;
    private String player;
    private String team;
    private String time;

    public MatchEvent(String type, String player, String team, String time) {
        this.type = type;
        this.player = player;
        this.team = team;
        this.time = time;
    }

    protected MatchEvent(Parcel in) {
        type = in.readString();
        player = in.readString();
        team = in.readString();
        time = in.readString();
    }

    public static final Creator<MatchEvent> CREATOR = new Creator<MatchEvent>() {
        @Override
        public MatchEvent createFromParcel(Parcel in) {
            return new MatchEvent(in);
        }

        @Override
        public MatchEvent[] newArray(int size) {
            return new MatchEvent[size];
        }
    };

    public String getType() { return type; }
    public String getPlayer() { return player; }
    public String getTeam() { return team; }
    public String getTime() { return time; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(player);
        dest.writeString(team);
        dest.writeString(time);
    }
}