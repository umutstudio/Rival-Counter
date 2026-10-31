package dev.rivalcounter.client;

public final class RivalRecord {
    public final String name;
    public int kills;
    public int deaths;

    public RivalRecord(String name, int kills, int deaths) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
    }

    public int encounters() {
        return kills + deaths;
    }
}
