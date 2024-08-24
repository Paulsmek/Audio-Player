package main.commandsArtist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class AddAlbum extends CommandSkell {
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<SongInput> songs = new ArrayList<>();

    public void DoAddAlbum(ArrayNode outputs, Database database, LibraryInput library, JsonNode commandNode) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user != null) {
            if (user.getUserType().equals("artist")) {
                if (!user.hasAlbum(this.getName())) {
                    JsonNode songsInCommand0 = commandNode.path("songs");
                    Set<String> uniqueSongNames = new HashSet<>();
                    if (songsInCommand0 != null && songsInCommand0.isArray()) {
                        for (JsonNode node : songsInCommand0) {
                            String songName = node.get("name").asText();
                            if (!uniqueSongNames.add(songName)) {
                                searchResult.put("message", this.getUser() + " has the same song at least twice in this album.");
                                outputs.add(searchResult);
                                return;
                            }
                        }
                    }
                    Album album = new Album();
                    album.setName(this.getName());
                    album.setDescription(this.getDescription());
                    album.setReleaseYear(this.getReleaseYear());
                    album.setArtist(this.getUser());

                    JsonNode songsInCommand = commandNode.path("songs");
                    if (songsInCommand != null && songsInCommand.isArray()) {
                        for (JsonNode node : songsInCommand) {
                            SongInput song = new SongInput();
                            song.setTags(new ArrayList<>());
                            if (node.get("name") != null) song.setName(node.get("name").asText());
                            if (node.get("duration") != null) song.setDuration(node.get("duration").asInt());
                            if (node.get("album") != null) song.setAlbum(node.get("album").asText());
                            JsonNode tags = node.get("tags");
                            if (tags != null) {
                                for (JsonNode tag : tags) {
                                    String oneTag = tag.toString();
                                    String theTag = oneTag.substring(1, oneTag.length() - 1);
                                    song.getTags().add(theTag);
                                }
                            }
                            if (node.get("lyrics") != null) song.setLyrics(node.get("lyrics").asText());
                            if (node.get("genre") != null) song.setGenre(node.get("genre").asText());
                            if (node.get("releaseYear") != null) song.setReleaseYear(node.get("releaseYear").asInt());
                            if (node.get("artist") != null) song.setArtist(node.get("artist").asText());

                            library.getSongs().add(song);
                            songs.add(song);
                        }
                    }
                    album.setSongs(songs);
                    user.getAlbums().add(album);
                    searchResult.put("message", this.getUser() + " has added new album successfully.");
                } else {
                    searchResult.put("message", this.getUser() + " has another album with the same name.");
                }
            }
            else {
                searchResult.put("message", this.getUser() + " is not an artist.");
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
        }
        outputs.add(searchResult);
    }

}
