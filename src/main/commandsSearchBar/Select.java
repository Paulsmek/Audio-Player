package main.commandsSearchBar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class Select extends CommandSkell {
    private int index;

    public void select(Database database, ArrayNode outputs, int nr) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (nr == 1) {
            if (database.findByName(this.getUser()).getSearchResults().size() >= this.getIndex()) {
                UserStats user = database.findByName(this.getUser());
                String out = database.findByName(this.getUser()).getSearchResults().get(this.getIndex() - 1).toString();
                String aut = null;
                if(database.findByName(this.getUser()).getSearchResults().size() >= this.getIndex() && user.getSearchType().equals(SelectType.Song)) {
                    aut = database.findByName(this.getUser()).getArtistOfSongs().get(this.getIndex() - 1).toString();
                }
                String Title = out;
                if (out.length() > 3) Title = out.substring(1, out.length() - 1);
                if (user.getSearchType().equals(SelectType.Artist)) {
                    user.setNameOfUserOfPage(Title);
                    user.setPage("ArtistPage");
                    searchResult.put("message", "Successfully selected " + Title + "'s page.");
                }
                else if (user.getSearchType().equals(SelectType.Host)) {
                    user.setNameOfUserOfPage(Title);
                    user.setPage("HostPage");
                    searchResult.put("message", "Successfully selected " + Title + "'s page.");
                }
                else {
                    searchResult.put("message", "Successfully selected " + Title + ".");
                }
                database.findByName(this.getUser()).setSelectedName(Title);
                if (aut != null) {
                    database.findByName(this.getUser()).setSelectedArtist(aut);
                }
                database.findByName(this.getUser()).setSelectType(database.findByName(this.getUser()).getSearchType());

            } else searchResult.put("message", "The selected ID is too high.");
        } else {
            searchResult.put("message", "Please conduct a search before making a selection.");
        }
        outputs.add(searchResult);
    }
}
