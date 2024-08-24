package main.commandsArtist;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;

@Getter @Setter
public class RemoveAlbum extends CommandSkell {
    private String name;
    public void DoRemove(ArrayNode outputs, Database database, LibraryInput library) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        UserStats userOfAlbum = database.findByName(this.getUser());
        if(userOfAlbum != null) {
            if (userOfAlbum.getUserType().equals("artist")) {
                Album albumToDelete = userOfAlbum.getAlbumByNameLocal(this.getName());
                if (albumToDelete != null) {
                    for (UserStats user : database.getListaRezUsers()) {
                        if (user != userOfAlbum && user.getLoadType() != null) {
                            if (user.getLoadType().equals(SelectType.Song)) {
                                String songNowName = user.getUserPlayband().getPlayStats().getNowPlaying().getName();
                                String songAlbum = user.getSongByName(songNowName, library).getAlbum();
                                if (songAlbum.equals(albumToDelete.getName())) {
                                    searchResult.put("message", userOfAlbum.getName() + " can't delete this album.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }
                            else if (user.getLoadType().equals(SelectType.Album)) {
                                if (albumToDelete == user.getUserPlayband().getLoadedAlbum()) {
                                    searchResult.put("message", userOfAlbum.getName() + " can't delete this album.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }
                            else if (user.getLoadType().equals(SelectType.Playlists)) {
                                for (SongInput song : user.getUserPlayband().getLoadedPlaylist().getSongsInPlaylist()) {
                                    String songAlbum = song.getAlbum();
                                    if (songAlbum.equals(albumToDelete.getName())) {
                                        searchResult.put("message", userOfAlbum.getName() + " can't delete this album.");
                                        outputs.add(searchResult);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    DeleteIt(albumToDelete, database, library);
                    searchResult.put("message", userOfAlbum.getName() + " deleted the album successfully.");
                    outputs.add(searchResult);

                } else {
                    searchResult.put("message", this.getUser() + " doesn't have an album with the given name.");
                    outputs.add(searchResult);
                }
            } else {
                searchResult.put("message", this.getUser() + " is not an artist.");
                outputs.add(searchResult);
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
            outputs.add(searchResult);
        }
    }

    private void DeleteIt(Album album, Database database, LibraryInput library) {
        for (SongInput song : album.getSongs()) {
            for (UserStats user : database.getListaRezUsers()) {
                for (Playlist playlist : user.getUserPlaylists()) {
                    playlist.getSongsInPlaylist().remove(song);
                }
                user.getLikedSongs().remove(song);
            }
        }
        UserStats user = database.findByName(this.getUser());
        user.getAlbums().remove(album);
    }
}
