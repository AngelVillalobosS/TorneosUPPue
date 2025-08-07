package com.uppue.torneosuppue;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Parcelable {
    private String id;
    private String name;
    private boolean active;
    private List<Player> players;  // Cambiado de List<String> a List<Player>
    private String sport;
    private int logoResId;

    // Constructor vacío para Firestore
    public Team() {
        players = new ArrayList<>();
    }

    // Constructor completo
    public Team(String name, boolean active, List<Player> players, String sport, int logoResId) {
        this.name = name;
        this.active = active;
        this.players = players != null ? players : new ArrayList<>();
        this.sport = sport;
        this.logoResId = logoResId;
    }

    // Constructor Parcel
    protected Team(Parcel in) {
        id = in.readString();
        name = in.readString();
        active = in.readByte() != 0;
        players = in.createTypedArrayList(Player.CREATOR);  // Cambiado para leer lista de Player
        sport = in.readString();
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeTypedList(players);  // Cambiado para escribir lista de Player
        dest.writeString(sport);
        dest.writeInt(logoResId);
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Player> getPlayers() { return players; }  // Devuelve lista de Player
    public void setPlayers(List<Player> players) {        // Recibe lista de Player
        this.players = players != null ? players : new ArrayList<>();
    }

    // Método para obtener solo los nombres de los jugadores (si lo necesitas)
    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public int getLogoResId() { return logoResId; }
    public void setLogoResId(int logoResId) { this.logoResId = logoResId; }
}