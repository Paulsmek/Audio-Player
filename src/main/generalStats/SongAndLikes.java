package main.generalStats;

import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SongAndLikes {
    private SongInput song;
    private int likes;
}
