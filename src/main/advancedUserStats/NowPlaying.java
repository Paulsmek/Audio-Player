package main.advancedUserStats;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NowPlaying {
    private String name;
    private String songArtist;
    private int timeRemaining;
    private int timeStart;

}
