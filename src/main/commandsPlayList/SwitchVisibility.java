package main.commandsPlayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class SwitchVisibility extends CommandSkell {
    private int Id;

    public void doSwitch(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (this.Id > user.getUserPlaylists().size()) {
            searchResult.put("message", "The specified playlist ID is too high.");
        }
        else {
            Playlist playlist = user.getUserPlaylists().get(Id - 1);
            if (playlist.getVisibility().equals("public")) {
                playlist.setVisibility("private");
                searchResult.put("message", "Visibility status updated successfully to private.");
            } else {
                playlist.setVisibility("public");
                searchResult.put("message", "Visibility status updated successfully to public.");
            }
        }
        outputs.add(searchResult);
    }
}
