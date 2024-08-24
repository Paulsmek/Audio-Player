package main.commandsPlayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

import java.util.ArrayList;

@Getter @Setter
public class Playlist extends CommandSkell {
    private String playListName;
    private ArrayList<SongInput> songsInPlaylist = new ArrayList<>();
    private String visibility = "public";
    private int followers = 0;

    public void addPlaylistToOut(ArrayNode outputs, Database database, int nr) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (nr == 1) {
            searchResult.put("message", "Playlist created successfully.");

        } else {
            searchResult.put("message", "A playlist with the same name already exists.");
        }
        outputs.add(searchResult);
    }

    public boolean SongExistsInPlaylist(String name) {
        if (!this.songsInPlaylist.isEmpty()) {
            for (SongInput song : this.songsInPlaylist) {
                if (song.getName().equals(name)) return true;
            }
        }
        return false;
    }
}
