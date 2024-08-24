package main.commandsPlayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class ShowPlaylists extends CommandSkell {

    public void showPlaylists(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (Playlist playlist : user.getUserPlaylists()) {
            ObjectNode playlistResult = JsonNodeFactory.instance.objectNode();
            playlistResult.put("name", playlist.getPlayListName());

            ArrayNode songsName = JsonNodeFactory.instance.arrayNode();
            for (SongInput song : playlist.getSongsInPlaylist()) {
                songsName.add(song.getName());
            }
            playlistResult.set("songs", songsName);
            playlistResult.put("visibility", playlist.getVisibility());
            playlistResult.put("followers", playlist.getFollowers());
            searchResults.add(playlistResult);
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }
}
