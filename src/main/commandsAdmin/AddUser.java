package main.commandsAdmin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class AddUser extends CommandSkell {
    private String type;
    private int age;
    private String city;

    public void DoAdd(ArrayNode outputs, Database database, LibraryInput library) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (database.findByName(this.getUser()) != null) {
            searchResult.put("message", "The username " + this.getUser() + " is already taken.");
        }
        else {
            UserStats user = new UserStats();
            user.setUserType(this.getType());
            if (user.getUserType().equals("user")) {
                user.setOnlineStatus(true);
            }
            else {
                user.setOnlineStatus(false);
            }
            user.setName(this.getUser());
            user.setAge(this.getAge());
            user.setCity(this.getCity());
            database.getListaRezUsers().add(user);

            UserInput user2 = new UserInput();
            user2.setAge(this.getAge());
            user2.setCity(this.getCity());
            user2.setUsername(this.getUser());
            library.getUsers().add(user2);

            searchResult.put("message", "The username " + this.getUser() + " has been added successfully.");


        }
        outputs.add(searchResult);
    }
}
