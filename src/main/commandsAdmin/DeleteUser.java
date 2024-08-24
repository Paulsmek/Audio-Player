package main.commandsAdmin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.*;
import main.commandsArtist.Album;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;

import java.util.ArrayList;

@Getter @Setter
public class DeleteUser extends CommandSkell {

    public void DoDelete(ArrayNode outputs, Database database, LibraryInput library) {
        UserStats userToDelete = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (userToDelete != null) {
            for (UserStats user : database.getListaRezUsers()) {
                if (user != userToDelete) {
                    for (PodcastSave podcast : user.getSavedPodcasts()) {
                        PodcastInput thePodcast = user.getPodcastByName(podcast.getPodcastName(), library);
                        for (PodcastInput userPodcast : userToDelete.getPodcasts()) {
                            if (userPodcast == thePodcast) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }
                        }
                    }

                    Playband playband = user.getUserPlayband();
                    if (user.getLoadType() != null) {
                        if (user.getLoadType().equals(SelectType.Song)) {
                            SongInput theSong = user.getSongByName(playband.getPlayStats().getNowPlaying().getName(), library);
                            if (theSong.getArtist().equals(userToDelete.getName())) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }
                        } else if (user.getLoadType().equals(SelectType.Playlists)) {
                            UserStats userOfPlaylist = user.getOwnerOfPlaylist(playband.getLoadedPlaylist().getPlayListName(), database);
                            if (userToDelete.getName().equals(userOfPlaylist.getName())) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }

                            for (SongInput song : user.getUserPlayband().getLoadedPlaylist().getSongsInPlaylist()) {
                                if (song.getArtist().equals(userToDelete.getName())) {
                                    searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }

                        } else if (user.getLoadType().equals(SelectType.Podcast)) {
                            UserStats userOfPodcast = database.findByName(playband.getLoadedPodcast().getOwner());
                            if (userOfPodcast != null && userToDelete == userOfPodcast) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }
                        } else {
                            UserStats userOfAlbum = database.findByName(playband.getLoadedAlbum().getArtist());
                            if (userOfAlbum != null && userToDelete == userOfAlbum) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }

                            for (SongInput song : user.getUserPlayband().getLoadedAlbum().getSongs()) {
                                if (song.getArtist().equals(userToDelete.getName())) {
                                    searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }
                        }
                    }
                    for (Playlist playlist : user.getUserPlaylists()) {
                        for (SongInput song : playlist.getSongsInPlaylist()) {
                            if (song.getArtist().equals(userToDelete.getName())) {
                                searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                outputs.add(searchResult);
                                return;
                            }
                        }
                    }

                    for (Album album : userToDelete.getAlbums()) {
                        for (SongInput song : album.getSongs()) {
                            for (Playlist playlist : user.getUserPlaylists()) {
                                if (playlist.getSongsInPlaylist().contains(song)) {
                                    searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }
                        }
                    }
                    if (user.getNameOfUserOfPage() != null && user.getNameOfUserOfPage().equals(userToDelete.getName())) {
                        searchResult.put("message", userToDelete.getName() + " can't be deleted.");
                        outputs.add(searchResult);
                        return;
                    }

                }
            }
            DeleteItAll(userToDelete, database, library);
            searchResult.put("message", userToDelete.getName() + " was successfully deleted.");
            outputs.add(searchResult);
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
            outputs.add(searchResult);
        }

    }

    private void DeleteItAll(UserStats user, Database database, LibraryInput library) {
        ArrayList<SongInput> songs = library.getSongs();
        ArrayList<PodcastInput> podcasts = library.getPodcasts();
        for (Album album : user.getAlbums()) {
            for (SongInput song : album.getSongs()) {
                DeleteSongRefrences(song, database);
                songs.remove(song);
            }
        }
        for (PodcastInput podcast : user.getPodcasts()) {
            podcasts.remove(podcast);
        }
        for (Playlist playlist : user.getUserPlaylists()) {
            DeletePlaylistRefrences(playlist, database);
        }
        for (Playlist playlist : user.getFollowedPlaylists()) {
            playlist.setFollowers(playlist.getFollowers() - 1);
        }
        database.getListaRezUsers().remove(user);
    }

    private void DeleteSongRefrences(SongInput song, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            user.getLikedSongs().remove(song);
        }
    }
    private void DeletePlaylistRefrences(Playlist playlist, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            user.getFollowedPlaylists().remove(playlist);
        }
    }
}
