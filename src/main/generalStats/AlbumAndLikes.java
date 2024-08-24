package main.generalStats;

import lombok.Getter;
import lombok.Setter;
import main.commandsArtist.Album;

@Getter @Setter
public class AlbumAndLikes {
    private Album album;
    private String name;
    private int likes = 0;
}
