package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class PlayPause extends CommandSkell {

    public void addPlayPauseToOut(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        Stats playStats = user.getUserPlayband().getPlayStats();
        if (user.getLoadType() != null) {
            if (!playStats.isPaused()) {
                searchResult.put("message", "Playback paused successfully.");
                playStats.setPaused(true);
            } else {
                searchResult.put("message", "Playback resumed successfully.");
                playStats.setStartingTime(this.getTimestamp());
                playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                playStats.setPaused(false);
            }
        } else searchResult.put("message", "Please load a source before attempting to pause or resume playback.");

        outputs.add(searchResult);
    }
}
