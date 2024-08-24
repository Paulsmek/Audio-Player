package main.generalStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

public class GetOnlineUsers extends CommandSkell {
    public void Online(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (UserStats user : database.getListaRezUsers()) {
            if (user.isOnlineStatus()) {
                searchResults.add(user.getName());
            }
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);
    }
}
