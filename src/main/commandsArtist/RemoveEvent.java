package main.commandsArtist;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class RemoveEvent extends CommandSkell {
    private String name;

    public void DoRemove(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        UserStats user = database.findByName(this.getUser());

        if(user != null) {
            if (user.getUserType().equals("artist")) {
                Event eventToDelete = user.getEventByName(this.getName());
                if (eventToDelete != null) {
                    user.getEvents().remove(eventToDelete);
                    searchResult.put("message", user.getName() + " deleted the event successfully.");
                    outputs.add(searchResult);

                } else {
                    searchResult.put("message", this.getUser() + " doesn't have an event with the given name.");
                    outputs.add(searchResult);
                }
            } else {
                searchResult.put("message", this.getUser() + " is not an artist.");
                outputs.add(searchResult);
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
            outputs.add(searchResult);
        }
    }
}
