package com.uppue.torneosuppue;

import android.os.Parcel;
import android.os.Parcelable;

public class Team implements Parcelable {
    private String sport;
    private String name;
    private int logoResId;

    public Team(String sport, String name, int logoResId) {
        this.sport = sport;
        this.name = name;
        this.logoResId = logoResId;
    }

    protected Team(Parcel in) {
        sport = in.readString();
        name = in.readString();
        logoResId = in.readInt();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sport);
        dest.writeString(name);
        dest.writeInt(logoResId);
    }

    // Getters
    public String getSport() { return sport; }
    public String getName() { return name; }
    public int getLogoResId() { return logoResId; }
}