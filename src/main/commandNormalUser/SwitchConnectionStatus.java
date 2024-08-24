package main.commandNormalUser;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class SwitchConnectionStatus extends CommandSkell {
    public void SwitchCon(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (user != null) {
            //add exception
            if (user.getUserType().equals("user")) {
                if (!user.isOnlineStatus()) {
                    user.getUserPlayband().getPlayStats().setStartingTime(this.getTimestamp());
                    user.getUserPlayband().getPlayStats().getNowPlaying().setTimeStart(this.getTimestamp());
                }
                user.setOnlineStatus(!user.isOnlineStatus());
                searchResult.put("message", this.getUser() + " has changed status successfully.");
            }
            else {
                searchResult.put("message", this.getUser() + " is not a normal user.");
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
        }
        outputs.add(searchResult);
    }
}
