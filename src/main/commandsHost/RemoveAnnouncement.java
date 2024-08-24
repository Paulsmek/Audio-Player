package main.commandsHost;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class RemoveAnnouncement extends CommandSkell {
    private String name;
    public void DoRemove(ArrayNode outputs, Database database)  {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user == null) {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
        }
        else {
            if (user.getUserType().equals("host")) {
                if (user.hasAnnouncement(this.getName())) {
                    user.getAnnouncements().remove(user.getAnnouncementByName(this.getName()));
                    searchResult.put("message", this.getUser() + " has successfully deleted the announcement.");
                }
                else {
                    searchResult.put("message", this.getUser() + " has no announcement with the given name.");
                }

            }
            else {
                searchResult.put("message", this.getUser() + " is not a host.");
            }
        }
        outputs.add(searchResult);
    }
}
