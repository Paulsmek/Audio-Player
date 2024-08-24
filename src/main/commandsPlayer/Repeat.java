package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class Repeat extends CommandSkell {

    public void DoRepeat(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user.getLoadType() != null && user.getUserPlayband().getPlayStats().getNowPlaying().getTimeRemaining() > 0) {
            if (user.getLoadType() == SelectType.Playlists) {
                if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                    user.getUserPlayband().getPlayStats().setRepeat("Repeat All");
                    searchResult.put("message", "Repeat mode changed to repeat all.");
                } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat All")) {
                    user.getUserPlayband().getPlayStats().setRepeat("Repeat Current Song");
                    searchResult.put("message", "Repeat mode changed to repeat current song.");
                } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Current Song")) {
                    user.getUserPlayband().getPlayStats().setRepeat("No Repeat");
                    searchResult.put("message", "Repeat mode changed to no repeat.");
                }

            } else {
                if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                    user.getUserPlayband().getPlayStats().setRepeat("Repeat Once");
                    searchResult.put("message", "Repeat mode changed to repeat once.");
                } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Once")) {
                    user.getUserPlayband().getPlayStats().setRepeat("Repeat Infinite");
                    searchResult.put("message", "Repeat mode changed to repeat infinite.");
                } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Infinite")) {
                    user.getUserPlayband().getPlayStats().setRepeat("No Repeat");
                    searchResult.put("message", "Repeat mode changed to no repeat.");
                }
            }
        } else {
            searchResult.put("message", "Please load a source before setting the repeat status.");
        }

        outputs.add(searchResult);
    }
}
