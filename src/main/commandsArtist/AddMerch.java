package main.commandsArtist;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class AddMerch extends CommandSkell {
    private String name;
    private String description;
    private int price;

    public void DoAddMerch(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user == null) {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
        }
        else {
            if (user.getUserType().equals("artist")) {
                if (this.IsValid(this.getPrice())) {
                    if (!user.hasMerch(this.getName())) {
                        Merch merch = new Merch();
                        merch.setName(this.getName());
                        merch.setDescription(this.getDescription());
                        merch.setPrice(this.getPrice());
                        user.getMerch().add(merch);
                        searchResult.put("message", this.getUser() + " has added new merchandise successfully.");
                    }
                    else {
                        searchResult.put("message", this.getUser() + " has merchandise with the same name.");
                    }
                }
                else {
                    searchResult.put("message", "Price for merchandise can not be negative.");
                }
            }
            else {
                searchResult.put("message", this.getUser() + " is not an artist.");
            }
        }
        outputs.add(searchResult);
    }

    private boolean IsValid(int price) {
        if (price < 0) return false;
        return true;
    }

}
