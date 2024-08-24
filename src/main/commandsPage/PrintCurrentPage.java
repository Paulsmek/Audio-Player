package main.commandsPage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.CommandSkell;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.commandsArtist.Album;
import main.commandsArtist.Event;
import main.commandsArtist.Merch;
import main.commandsHost.Announcement;
import main.commandsPlayList.Playlist;
import main.generalStats.SongAndLikes;

import java.util.*;

@Getter @Setter
public class PrintCurrentPage extends CommandSkell {

    public void PrintPage(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("user", this.getUser());
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        UserStats user = database.findByName(this.getUser());
        if (user.getPage().equals("Home")) {
            ArrayNode likedSongs = JsonNodeFactory.instance.arrayNode();
            ArrayNode followedPlaylists = JsonNodeFactory.instance.arrayNode();
            for (Playlist playlist : user.getFollowedPlaylists()) {
                followedPlaylists.add(playlist.getPlayListName());
            }
            ArrayList<SongInput> s = user.getLikedSongs();
            ArrayList<SongAndLikes> ss= new ArrayList<>();
            for (SongInput song : s) {
                int likes = user.getLikesOfSong(song, database);
                SongAndLikes sz = new SongAndLikes();
                sz.setLikes(likes);
                sz.setSong(song);
                ss.add(sz);
            }
            Collections.sort(ss, Comparator.comparingInt(SongAndLikes::getLikes).reversed());
            int c = 0;
            for (SongAndLikes song : ss) {
                likedSongs.add(song.getSong().getName());
                c++;
                if(c == 5) {
                    break;
                }
            }

            StringBuilder formattedLikedSongs = new StringBuilder();
            likedSongs.elements().forEachRemaining(node -> {
                formattedLikedSongs.append(node.textValue());
                formattedLikedSongs.append(", ");
            });
            if (formattedLikedSongs.length() > 0) {
                formattedLikedSongs.setLength(formattedLikedSongs.length() - 2);
            }

            StringBuilder formattedPlaylists = new StringBuilder();
            followedPlaylists.elements().forEachRemaining(node -> {
                formattedPlaylists.append(node.textValue());
                formattedPlaylists.append(", ");
            });

            if (formattedPlaylists.length() > 0) {
                formattedPlaylists.setLength(formattedPlaylists.length() - 2);
            }
            searchResult.put("message", "Liked songs:\n\t[" + formattedLikedSongs + "]\n\nFollowed playlists:\n\t[" + formattedPlaylists + "]");
        }
        else if (user.getPage().equals("LikedContent")) {
            ArrayNode likedSongs = JsonNodeFactory.instance.arrayNode();
            ArrayNode followedPlaylists = JsonNodeFactory.instance.arrayNode();
            for (Playlist playlist : user.getFollowedPlaylists()) {
                followedPlaylists.add(playlist.getPlayListName() + " - " + user.getOwnerOfPlaylist(playlist.getPlayListName(), database).getName());
            }
            for (SongInput song : user.getLikedSongs()) {
                likedSongs.add(song.getName() + " - " + song.getArtist());
            }
            StringBuilder formattedLikedSongs = new StringBuilder();
            likedSongs.elements().forEachRemaining(node -> {
                    formattedLikedSongs.append(node.textValue());
                    formattedLikedSongs.append(", ");
            });
            if (formattedLikedSongs.length() > 0) {
                formattedLikedSongs.setLength(formattedLikedSongs.length() - 2);
            }

            StringBuilder formattedPlaylists = new StringBuilder();
            followedPlaylists.elements().forEachRemaining(node -> {
                    formattedPlaylists.append(node.textValue());
                    formattedPlaylists.append(", ");
            });
            if (formattedPlaylists.length() > 0) {
                formattedPlaylists.setLength(formattedPlaylists.length() - 2);
            }

            searchResult.put("message", "Liked songs:\n\t[" + formattedLikedSongs + "]\n\nFollowed playlists:\n\t[" + formattedPlaylists + "]");
        }
        else if(user.getPage().equals("ArtistPage")){
            ArrayNode albums = JsonNodeFactory.instance.arrayNode();
            ArrayNode merch = JsonNodeFactory.instance.arrayNode();
            ArrayNode events = JsonNodeFactory.instance.arrayNode();
            UserStats artistUser = database.findByName(user.getNameOfUserOfPage());
            if (artistUser != null) {
                for (Album album : artistUser.getAlbums()) {
                    albums.add(album.getName());
                }
                for (Event event : artistUser.getEvents()) {
                    events.add(event.getName() + " - " + event.getDate() + ":\n\t" + event.getDescription());
                }
                for (Merch merch1 : artistUser.getMerch()) {
                    merch.add(merch1.getName()+ " - " + merch1.getPrice() + ":\n\t" + merch1.getDescription());
                }

                StringBuilder formattedAlbums = new StringBuilder();
                albums.elements().forEachRemaining(node -> {
                            formattedAlbums.append(node.textValue());
                            formattedAlbums.append(", ");
                        });
                if (formattedAlbums.length() > 0) {
                    formattedAlbums.setLength(formattedAlbums.length() - 2);
                }

                StringBuilder formattedEvents = new StringBuilder();
                events.elements().forEachRemaining(node -> {
                        formattedEvents.append(node.textValue());
                        formattedEvents.append(", ");
                        });
                if (formattedEvents.length() > 0) {
                    formattedEvents.setLength(formattedEvents.length() - 2);
                }

                StringBuilder formattedMerch = new StringBuilder();
                merch.elements().forEachRemaining(node -> {
                            formattedMerch.append(node.textValue());
                            formattedMerch.append(", ");
                        });
                if (formattedMerch.length() > 0) {
                    formattedMerch.setLength(formattedMerch.length() - 2);
                }

                searchResult.put("message", "Albums:\n\t[" + formattedAlbums + "]\n\nMerch:\n\t[" + formattedMerch + "]\n\nEvents:\n\t[" +
                        formattedEvents + "]");
            }
        }
        else if(user.getPage().equals("HostPage")) {
            ArrayNode podcasts = JsonNodeFactory.instance.arrayNode();
            ArrayNode announcements = JsonNodeFactory.instance.arrayNode();
            UserStats hostUser = database.findByName(user.getNameOfUserOfPage());
            if (hostUser != null) {
                StringBuilder formattedPodcasts = new StringBuilder();

                for (PodcastInput podcast : hostUser.getPodcasts()) {
                    ObjectNode podcastNode = JsonNodeFactory.instance.objectNode();
                    podcastNode.put("name", podcast.getName());
                    ArrayNode episodes = JsonNodeFactory.instance.arrayNode();
                    for (EpisodeInput episode : podcast.getEpisodes()) {
                        ObjectNode episodeNode = JsonNodeFactory.instance.objectNode();
                        episodeNode.put("name", episode.getName());
                        episodeNode.put("description", episode.getDescription());
                        episodes.add(episodeNode);
                    }

                    podcastNode.set("episodes", episodes);
                    podcasts.add(podcastNode);

                    formattedPodcasts.append(podcastNode.get("name").textValue()).append(":\n\t[");
                    ArrayNode episodesNode = (ArrayNode) podcastNode.get("episodes");
                    episodesNode.elements().forEachRemaining(episodeNode ->
                            formattedPodcasts.append(episodeNode.get("name").textValue())
                                    .append(" - ")
                                    .append(episodeNode.get("description").textValue())
                                    .append(", "));
                    if (episodesNode.size() > 0) {
                        formattedPodcasts.setLength(formattedPodcasts.length() - 2); // Remove the last ", "
                    }
                    formattedPodcasts.append("]\n, ");
                }

                if (podcasts.size() > 0) {
                    formattedPodcasts.setLength(formattedPodcasts.length() - 2); // Remove the last ", "
                }

                for (Announcement ann : hostUser.getAnnouncements()) {
                    announcements.add(ann.getName() + ":\n\t" + ann.getDescription());
                }

                StringBuilder formattedAnn = new StringBuilder();
                announcements.elements().forEachRemaining(node -> {
                    formattedAnn.append(node.textValue());
                    formattedAnn.append(", ");
                });
                if (formattedAnn.length() > 0) {
                    formattedAnn.setLength(formattedAnn.length() - 2);
                }

                searchResult.put("message", "Podcasts:\n\t[" + formattedPodcasts + "]\n\nAnnouncements:\n\t[" + formattedAnn + "\n]");
            }
        }

        outputs.add(searchResult);
    }
}
