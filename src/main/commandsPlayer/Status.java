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
public class Status extends CommandSkell {

    public void addStatusToOut(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());
        Stats playStats = user.getUserPlayband().getPlayStats();

        if (playStats.getRemainedTime() < 1 || playStats.getNowPlaying().getTimeRemaining() < 1) {
            ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
            searchResult.put("command", this.getCommand());
            searchResult.put("user", this.getUser());
            searchResult.put("timestamp", this.getTimestamp());

            ObjectNode statsNode = JsonNodeFactory.instance.objectNode();
            statsNode.put("name", "");
            statsNode.put("remainedTime", 0);
            statsNode.put("repeat", "No Repeat");
            statsNode.put("shuffle", false);
            statsNode.put("paused", true);
            searchResult.set("stats", statsNode);
            outputs.add(searchResult);
        } else {
            ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
            searchResult.put("command", this.getCommand());
            searchResult.put("user", this.getUser());
            searchResult.put("timestamp", this.getTimestamp());

            ObjectNode statsNode = JsonNodeFactory.instance.objectNode();
            statsNode.put("name", playStats.getNowPlaying().getName());
            statsNode.put("remainedTime", playStats.getNowPlaying().getTimeRemaining());
            statsNode.put("repeat", playStats.getRepeat());
            statsNode.put("shuffle", playStats.isShuffle());
            statsNode.put("paused", playStats.isPaused());

            searchResult.set("stats", statsNode);
            outputs.add(searchResult);
        }
    }
}
