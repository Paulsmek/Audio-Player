package main.generalStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class GetAllUsers extends CommandSkell {

    public void PrintUsers(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("user")) {
                searchResults.add(user.getName());
            }
        }

        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("artist")) {
                searchResults.add(user.getName());
            }
        }

        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("host")) {
                searchResults.add(user.getName());
            }
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);
    }
}
