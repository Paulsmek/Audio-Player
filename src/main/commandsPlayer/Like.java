package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class Like extends CommandSkell {

    public void LikeUnlike(ArrayNode outputs, Database database, LibraryInput library) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        Stats playStats = user.getUserPlayband().getPlayStats();
        if (playStats.getNowPlaying().getName() != null) {
            if (user.getLoadType() == SelectType.Song) {
                if (user.SongExists(playStats.getNowPlaying().getName())) {
                    user.getLikedSongs().remove(user.getSongByName(playStats.getNowPlaying().getName(), library));
                    searchResult.put("message", "Unlike registered successfully.");
                } else {
                    user.getLikedSongs().add(user.getSongByName(playStats.getNowPlaying().getName(), library));
                    searchResult.put("message", "Like registered successfully.");
                }
            } else if (user.getLoadType() == SelectType.Playlists || user.getLoadType() == SelectType.Album) {
                SongInput song = user.getSongByNameArtist(user.getUserPlayband().getPlayStats().getNowPlaying().getName(), library,
                        user.getUserPlayband().getPlayStats().getNowPlaying().getSongArtist());
                if (user.SongExists(song.getName())) {
                    user.getLikedSongs().remove(song);
                    searchResult.put("message", "Unlike registered successfully.");
                } else {
                    user.getLikedSongs().add(song);
                    searchResult.put("message", "Like registered successfully.");
                }
            } else {
                searchResult.put("message", "Loaded source is not a song.");
            }
        } else {
            searchResult.put("message", "Please load a source before liking or unliking.");
        }

        outputs.add(searchResult);
    }
}
