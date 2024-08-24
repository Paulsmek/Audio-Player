package main.commandsAdmin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.commandsArtist.Album;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class ShowAlbums extends CommandSkell {

    public void Show(ArrayNode outputs, Database database){
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (Album album : database.findByName(this.getUser()).getAlbums()) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", album.getName());
            ArrayNode songs = JsonNodeFactory.instance.arrayNode();
            for (SongInput song : album.getSongs()) {
                songs.add(song.getName());
            }
            node.set("songs", songs);
            searchResults.add(node);
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);
    }
}
