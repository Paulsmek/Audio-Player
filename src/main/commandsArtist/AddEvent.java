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
public class AddEvent extends CommandSkell {
    private String name;
    private String date;
    private String description;

    public void DoAddEvent(ArrayNode outputs, Database database)  {
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
                if (this.IsValid(this.getDate())) {
                    if (!user.hasEvent(this.getName())) {
                        Event event = new Event();
                        event.setDate(this.getDate());
                        event.setDescription(this.getDescription());
                        event.setName(this.getName());
                        user.getEvents().add(event);
                        searchResult.put("message", this.getUser() + " has added new event successfully.");
                    }
                    else {
                        searchResult.put("message", this.getUser() + " has another event with the same name.");
                    }
                }
                else {
                    searchResult.put("message", "Event for " +this.getUser() + " does not have a valid date.");
                }
            }
            else {
                searchResult.put("message", this.getUser() + " is not an artist.");
            }
        }
        outputs.add(searchResult);
    }

    private boolean IsValid(String date) {
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int year = Integer.parseInt(date.substring(6));

        if (day > 31) return false;
        if (month == 2 && day > 28) return false;
        if (month > 12) return false;
        if (year < 1900 || year > 2023) return false;
        return true;
    }
}
