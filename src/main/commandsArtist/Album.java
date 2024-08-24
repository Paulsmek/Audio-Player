package main.commandsArtist;

import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter@Setter
public class Album {
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private String artist;
}
