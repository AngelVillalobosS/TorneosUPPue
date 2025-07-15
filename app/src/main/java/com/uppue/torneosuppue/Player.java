package com.uppue.torneosuppue;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private String name;
    private String position;
    private String number;

    public Player(String name, String position, String number) {
        this.name = name;
        this.position = position;
        this.number = number;
    }

    protected Player(Parcel in) {
        name = in.readString();
        position = in.readString();
        number = in.readString();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(position);
        dest.writeString(number);
    }

    // Getters
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getNumber() { return number; }
}