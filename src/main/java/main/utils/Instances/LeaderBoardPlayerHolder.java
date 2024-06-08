package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderBoardPlayerHolder {
    private int kills;
    private int deaths;
    private long playtime;

    private int kills_place;
    private int deaths_place;
    private int playtime_place;

    public LeaderBoardPlayerHolder(int kills, int deaths, long playtime,
                                   int kills_place, int deaths_place, int playtime_place) {
        this.kills = kills;
        this.deaths = deaths;
        this.playtime = playtime;

        this.kills_place = kills_place;
        this.deaths_place = deaths_place;
        this.playtime_place = playtime_place;
    }
}
