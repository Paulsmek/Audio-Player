package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import main.commandsArtist.Album;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Getter @Setter
public class Shuffle extends CommandSkell {
    private int seed;

    public void ShuffleToOut(ArrayNode outputs, Database resultsUsers) {
        UserStats user = resultsUsers.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user.getLoadType() != null) {
            if (user.getLoadType() == SelectType.Playlists || user.getLoadType() == SelectType.Album) {
                if (!user.getUserPlayband().getPlayStats().isShuffle()) {
                    user.getUserPlayband().getPlayStats().setShuffle(!user.getUserPlayband().getPlayStats().isShuffle());
                    searchResult.put("message", "Shuffle function activated successfully.");
                    int lastIdx = user.getUserPlayband().getLastIndex();
                    int idx = user.getUserPlayband().getPlayStats().ForIndGetInd(lastIdx);
                    user.getUserPlayband().setLastIndex(idx);
                } else {
                    user.getUserPlayband().getPlayStats().setShuffle(!user.getUserPlayband().getPlayStats().isShuffle());
                    searchResult.put("message", "Shuffle function deactivated successfully.");
                }
            } else {
                searchResult.put("message", "The loaded source is not a playlist or an album.");
            }
        } else {
            searchResult.put("message", "Please load a source before using the shuffle function.");
        }
        outputs.add(searchResult);

    }

    public ArrayList<Integer> DoTheShuffleP(Database resultsUsers) {
        UserStats user = resultsUsers.findByName(this.getUser());
        Playlist playlist = user.getUserPlayband().getLoadedPlaylist();
        if (playlist != null) {
            ArrayList<Integer> originalOrder = new ArrayList<>();
            for (int i = 0; i < playlist.getSongsInPlaylist().size(); i++) {
                originalOrder.add(i);
            }

            Collections.shuffle(originalOrder, new Random(this.getSeed()));

            return originalOrder;
        }
        return new ArrayList<>();
    }

    public ArrayList<Integer> DoTheShuffleA(Database resultsUsers) {
        UserStats user = resultsUsers.findByName(this.getUser());
        Album album = user.getUserPlayband().getLoadedAlbum();
        if (album != null) {
            ArrayList<Integer> originalOrder = new ArrayList<>();
            for (int i = 0; i < album.getSongs().size(); i++) {
                originalOrder.add(i);
            }

            Collections.shuffle(originalOrder, new Random(this.getSeed()));

            return originalOrder;
        }
        return new ArrayList<>();
    }
}
