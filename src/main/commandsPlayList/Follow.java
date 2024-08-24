package main.commandsPlayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class Follow extends CommandSkell {

    public void doFollow(ArrayNode outputs, Database database, LibraryInput library) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user.getSelectType() != null) {
            if (user.getSelectType() == SelectType.Playlists) {
                if (user.PlaylistExists(user.getSelectedName())) {
                    searchResult.put("message", "You cannot follow or unfollow your own playlist.");
                }
                else {
                    if (user.PlaylistIsFollowed(user.getSelectedName())) {
                        Playlist deletePlaylist = user.getPlaylistByNameGlobal(user.getSelectedName(), database);
                        user.getFollowedPlaylists().remove(deletePlaylist);
                        deletePlaylist.setFollowers(deletePlaylist.getFollowers() - 1);
                        searchResult.put("message", "Playlist unfollowed successfully.");
                    }
                    else {
                        Playlist addPlaylist = user.getPlaylistByNameGlobal(user.getSelectedName(), database);
                        user.getFollowedPlaylists().add(addPlaylist);
                        addPlaylist.setFollowers(addPlaylist.getFollowers() + 1);
                        searchResult.put("message", "Playlist followed successfully.");
                    }

                }
            }
            else {
                searchResult.put("message", "The selected source is not a playlist.");
            }
        }
        else {
            searchResult.put("message", "Please select a source before following or unfollowing.");
        }

        outputs.add(searchResult);
    }
}
