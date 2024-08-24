package main.commandsPage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.CommandSkell;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;

@Getter @Setter
public class ChangePage extends CommandSkell {
    private String nextPage;

    public void DoChange(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        UserStats user = database.findByName(this.getUser());

        if (user != null) {
            if (nextPage.equals("Home")) {
                user.setPage("Home");
                user.setNameOfUserOfPage(null);
                searchResult.put("message", user.getName() + " accessed " + nextPage + " successfully.");
            }
            else if (nextPage.equals("LikedContent")) {
                user.setPage("LikedContent");
                user.setNameOfUserOfPage(null);
                searchResult.put("message", user.getName() + " accessed " + nextPage + " successfully.");
            }
            else {
                searchResult.put("message", user.getName() + " is trying to access a non-existent page.");
            }
        }
        outputs.add(searchResult);
    }
}
