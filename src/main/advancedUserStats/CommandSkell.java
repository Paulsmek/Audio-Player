package main.advancedUserStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public  class CommandSkell {
    private String command;
    private String user;
    private int timestamp;


    public void DoOffline(ArrayNode outputs) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        if (this.getCommand().equals("printCurrentPage")) {
            searchResult.put("user", this.getUser());
            searchResult.put("command", this.getCommand());
            searchResult.put("timestamp", this.getTimestamp());
            searchResult.put("message", this.getUser() + " is offline.");
        }
        else {
            searchResult.put("command", this.getCommand());
            searchResult.put("user", this.getUser());
            searchResult.put("timestamp", this.getTimestamp());
            searchResult.put("message", this.getUser() + " is offline.");

            if (this.getCommand().equals("search")) {
                ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
                searchResult.set("results", searchResults);
            }
        }
        outputs.add(searchResult);
    }

    public void MoveData(CommandSkell comm) {
        this.setCommand(comm.getCommand());
        this.setUser(comm.getUser());
        this.setTimestamp(comm.getTimestamp());
    }


}
