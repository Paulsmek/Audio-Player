package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;

@Getter @Setter
public class AddRemoveInPlaylist extends CommandSkell {
    private int playlistId;


    public void addRemove(ArrayNode outputs, Database database, LibraryInput library) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        Stats playStats = user.getUserPlayband().getPlayStats();
        if (!user.getUserPlaylists().isEmpty()) {
            if (playStats.getNowPlaying().getName() != null) {
                if (user.getLoadType() == SelectType.Song) {
                    if (user.getUserPlaylists().size() >= this.getPlaylistId()) {
                        Playlist playlist = user.getUserPlaylists().get(this.getPlaylistId() - 1);
                        if (playlist.SongExistsInPlaylist(playStats.getNowPlaying().getName())) {
                            playlist.getSongsInPlaylist().remove(user.getSongByName(playStats.getNowPlaying().getName(), library));
                            searchResult.put("message", "Successfully removed from playlist.");
                        } else {
                            playlist.getSongsInPlaylist().add(user.getSongByName(playStats.getNowPlaying().getName(), library));
                            searchResult.put("message", "Successfully added to playlist.");
                        }
                    } else {
                        searchResult.put("message", "The specified playlist does not exist.");
                    }
                }
                else if (user.getLoadType() == SelectType.Album) {
                    if (user.getUserPlaylists().size() >= this.getPlaylistId()) {
                        Playlist playlist = user.getUserPlaylists().get(this.getPlaylistId() - 1);
                        if (playlist.SongExistsInPlaylist(playStats.getNowPlaying().getName())) {
                            playlist.getSongsInPlaylist().remove(user.getSongByName(playStats.getNowPlaying().getName(), library));
                            searchResult.put("message", "Successfully removed from playlist.");
                        } else {
                            playlist.getSongsInPlaylist().add(user.getSongByName(playStats.getNowPlaying().getName(), library));
                            searchResult.put("message", "Successfully added to playlist.");
                        }
                    } else {
                        searchResult.put("message", "The specified playlist does not exist.");
                    }
                }
                else {
                    searchResult.put("message", "The loaded source is not a song.");
                }

            } else {
                searchResult.put("message", "Please load a source before adding to or removing from the playlist.");
            }
        } else {
            searchResult.put("message", "The specified playlist does not exist.");
        }

        outputs.add(searchResult);
    }
}
