package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.commandsArtist.Album;
import main.advancedUserStats.CommandSkell;
import main.commandsPlayList.Playlist;
import main.advancedUserStats.PodcastSave;
import main.advancedUserStats.Database;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;

@Getter @Setter
public class Load extends CommandSkell {

    public void load(ArrayNode outputs, Database database, int nr, LibraryInput library) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        UserStats user = database.findByName(this.getUser());
        Stats stats = user.getUserPlayband().getPlayStats();
        if (nr == 2) {
            searchResult.put("message", "Please select a source before attempting to load.");
        } else {
            switch (user.getSelectType()) {
                case Song:
                    stats.getNowPlaying().setName(user.getSelectedName());
                    stats.getNowPlaying().setSongArtist(user.getSelectedArtist());
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                    SongInput theSong = user.getSongByName(user.getSelectedName(), library);
                    if (theSong != null) stats.setRemainedTime(theSong.getDuration());
                    stats.setStartingTime(this.getTimestamp());

                    stats.getNowPlaying().setTimeRemaining(theSong.getDuration());
                    stats.getNowPlaying().setTimeStart(this.getTimestamp());

                    user.setLoadType(user.getSelectType());
                    break;

                case Playlists:
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                    Playlist thePlaylist = user.getPlaylistByName(user.getSelectedName(), database);

                    if (thePlaylist != null) {
                        SongInput song = thePlaylist.getSongsInPlaylist().get(0);
                        stats.getNowPlaying().setName(song.getName());
                        stats.getNowPlaying().setSongArtist(song.getArtist());
                        stats.getNowPlaying().setTimeRemaining(song.getDuration());
                        stats.getNowPlaying().setTimeStart(this.getTimestamp());
                        user.getUserPlayband().setLastIndex(0);

                        int time1 = user.getPlaylistDuration(thePlaylist);
                        if (thePlaylist != null) stats.setRemainedTime(time1);
                        stats.setStartingTime(this.getTimestamp());
                        user.getUserPlayband().setLoadedPlaylist(thePlaylist);

                        user.setLoadType(user.getSelectType());
                    }
                    break;

                case Album:
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                    Album theAlbum = user.getAlbumByName(user.getSelectedName(), database);
                    if (theAlbum != null) {
                        SongInput song = theAlbum.getSongs().get(0);
                        stats.getNowPlaying().setName(song.getName());
                        stats.getNowPlaying().setSongArtist(song.getArtist());
                        stats.getNowPlaying().setTimeRemaining(song.getDuration());
                        stats.getNowPlaying().setTimeStart(this.getTimestamp());
                        user.getUserPlayband().setLastIndex(0);

                        int time1 = user.getAlbumDuration(theAlbum);
                        if (theAlbum != null) stats.setRemainedTime(time1);
                        stats.setStartingTime(this.getTimestamp());
                        user.getUserPlayband().setLoadedAlbum(theAlbum);

                        user.setLoadType(user.getSelectType());
                    }
                    break;

                case Podcast:
                    if (!user.IsPodcastSaved(user.getSelectedName())) {
                        stats.setPaused(false);
                        stats.setShuffle(false);
                        stats.setRepeat("No Repeat");
                        PodcastInput thePodcast = user.getPodcastByName(user.getSelectedName(), library);
                        EpisodeInput episode = thePodcast.getEpisodes().get(0);

                        stats.getNowPlaying().setName(episode.getName());
                        stats.getNowPlaying().setTimeRemaining(episode.getDuration());
                        stats.getNowPlaying().setTimeStart(this.getTimestamp());
                        user.getUserPlayband().setLastIndex(0);

                        int time2 = user.getPodcastDuration(user.getSelectedName(), library);
                        if (thePodcast != null) stats.setRemainedTime(time2);
                        stats.setStartingTime(this.getTimestamp());
                        user.getUserPlayband().setLoadedPodcast(thePodcast);

                        user.setLoadType(user.getSelectType());
                    } else {
                        stats.setPaused(false);
                        stats.setShuffle(false);
                        stats.setRepeat("No Repeat");
                        PodcastSave thePodcast = user.getPodcastBySavedPodcast(user.getSelectedName());
                        PodcastInput thePodcast1 = user.getPodcastByName(user.getSelectedName(), library);

                        stats.getNowPlaying().setName(thePodcast.getEpisodeName());
                        stats.getNowPlaying().setTimeRemaining(thePodcast.getRemainedEpisodeTime());
                        stats.getNowPlaying().setTimeStart(this.getTimestamp());
                        user.getUserPlayband().setLastIndex(thePodcast.getRemainedIndex());

                        int time2 = thePodcast.getRemainedTime();
                        if (thePodcast != null) stats.setRemainedTime(time2);
                        stats.setStartingTime(this.getTimestamp());
                        user.getUserPlayband().setLoadedPodcast(thePodcast1);

                        user.setLoadType(user.getSelectType());
                    }
                    break;


            }
            user.clearSelect();
            user.clearSearch();
            searchResult.put("message", "Playback loaded successfully.");

        }
        outputs.add(searchResult);
    }
}
