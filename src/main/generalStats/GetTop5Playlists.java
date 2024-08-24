package main.generalStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Getter @Setter
public class GetTop5Playlists extends CommandSkell {


    public void doTop5Playlists(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayList<Playlist> topPlaylist = new ArrayList<>();
        for (UserStats user : database.getListaRezUsers()) {
            for (Playlist playlist : user.getUserPlaylists()) {
                if (playlist.getVisibility().equals("public")) {
                    topPlaylist.add(playlist);
                }
            }
        }
        Collections.sort(topPlaylist, Comparator.comparing(Playlist::getTimestamp));
        topPlaylist.sort(Comparator.comparingInt(Playlist::getFollowers).reversed());
        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        int i = 0;
        while (i < 5 && i < topPlaylist.size()) {
            searchResults.add(topPlaylist.get(i).getPlayListName());
            i++;
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }
}
