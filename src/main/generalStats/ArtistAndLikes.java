package main.generalStats;

import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;

@Getter @Setter
public class ArtistAndLikes {
    private UserStats artist;
    private String name;
    private int likes;

}
