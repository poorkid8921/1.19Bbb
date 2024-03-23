package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalLeaderBoardHolder {
    private int deaths;
    private int kills;
    private int playtime;
    private int deathPlace;
    private int killPlace;
    private int playTimePlace;
    private String holder;

    private FinalLeaderBoardHolder(int deaths,
                                   int kills,
                                   int playtime,
                                   int deathPlace,
                                   int killPlace,
                                   int playTimePlace,
                                   String holder) {
        this.deaths = deaths;
        this.kills = kills;
        this.playtime = playtime;
        this.deathPlace = deathPlace;
        this.killPlace = killPlace;
        this.playTimePlace = playTimePlace;
        this.holder = holder;
    }
}
