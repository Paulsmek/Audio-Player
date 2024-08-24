package main.userStatistics;

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
public class ShowPrefferedSongs extends CommandSkell {

    public void showSongs(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (SongInput song : user.getLikedSongs()) {
            searchResults.add(song.getName());
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }
}
