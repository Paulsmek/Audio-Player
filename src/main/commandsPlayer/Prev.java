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
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;

@Getter @Setter
public class Prev extends CommandSkell {

    public void doPrev(ArrayNode outputs, Database database, LibraryInput library, int nr) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (user.getLoadType() != null) {
            Stats theStats = user.getUserPlayband().getPlayStats();
            if (!theStats.isShuffle()) {
                if (user.getLoadType() == SelectType.Playlists) {
                    if (user.getUserPlayband().getLastIndex() > 0) {
                        Playlist thePlaylist = user.getUserPlayband().getLoadedPlaylist();
                        SongInput songNow = user.getSongByName(theStats.getNowPlaying().getName(), library);
                        if (theStats.getNowPlaying().getTimeRemaining() != songNow.getDuration()) {
                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(songNow.getName());
                            theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");
                        }
                        else {
                            SongInput prevSong = thePlaylist.getSongsInPlaylist().get(user.getUserPlayband().getLastIndex() - 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() - 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration() + prevSong.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(prevSong.getName());
                            theStats.getNowPlaying().setSongArtist(prevSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(prevSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + prevSong.getName() + ".");

                        }
                    }
                    else {
                        SongInput songNow = user.getUserPlayband().getLoadedPlaylist().getSongsInPlaylist().get(user.getUserPlayband().getLastIndex());
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(songNow.getName());
                        theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");

                    }
                }
                else if (user.getLoadType() == SelectType.Song) {
                    SongInput songNow = user.getSongByName(theStats.getNowPlaying().getName(), library);
                    theStats.setRemainedTime(songNow.getDuration());
                    theStats.setStartingTime(this.getTimestamp());

                    theStats.getNowPlaying().setName(songNow.getName());
                    theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                    theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                    theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                    searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");

                }
                if (user.getLoadType() == SelectType.Album) {
                    if (user.getUserPlayband().getLastIndex() > 0) {
                        Album theAlbum = user.getUserPlayband().getLoadedAlbum();
                        SongInput songNow = user.getSongByName(theStats.getNowPlaying().getName(), library);
                        if (theStats.getNowPlaying().getTimeRemaining() != songNow.getDuration()) {
                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(songNow.getName());
                            theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");
                        }
                        else {
                            SongInput prevSong = theAlbum.getSongs().get(user.getUserPlayband().getLastIndex() - 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() - 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration() + prevSong.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(prevSong.getName());
                            theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(prevSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + prevSong.getName() + ".");

                        }
                    }
                    else {
                        SongInput songNow = user.getUserPlayband().getLoadedAlbum().getSongs().get(user.getUserPlayband().getLastIndex());
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(songNow.getName());
                        theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");

                    }
                }
                else if (user.getUserPlayband().getLoadedPodcast() != null){
                    if (user.getUserPlayband().getLastIndex() > 0) {
                        PodcastInput thePodcast = user.getUserPlayband().getLoadedPodcast();
                        EpisodeInput episodeNow = thePodcast.getEpisodes().get(user.getUserPlayband().getLastIndex());
                        if (theStats.getNowPlaying().getTimeRemaining() != episodeNow.getDuration()) {
                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + episodeNow.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(episodeNow.getName());
                            theStats.getNowPlaying().setTimeRemaining(episodeNow.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + episodeNow.getName() + ".");
                        }
                        else {
                            EpisodeInput prevEpisode = thePodcast.getEpisodes().get(user.getUserPlayband().getLastIndex() - 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() - 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + episodeNow.getDuration() + prevEpisode.getDuration());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(prevEpisode.getName());
                            theStats.getNowPlaying().setTimeRemaining(prevEpisode.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Returned to previous track successfully. The current track is " + prevEpisode.getName() + ".");

                        }
                    }
                    else {

                        EpisodeInput episodeNow = user.getUserPlayband().getLoadedPodcast().getEpisodes().get(user.getUserPlayband().getLastIndex());
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + episodeNow.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(episodeNow.getName());
                        theStats.getNowPlaying().setTimeRemaining(episodeNow.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Returned to previous track successfully. The current track is " + episodeNow.getName() + ".");

                    }
                }
            }
            else {
                int lastIndex = user.getUserPlayband().getLastIndex();
                Stats playStats = user.getUserPlayband().getPlayStats();
                Playlist loadedPlaylist = user.getUserPlayband().getLoadedPlaylist();
                int shuffleIndx = lastIndex;
                if (shuffleIndx < 1) {
                    SongInput currentSong = user.getSongByName(playStats.getNowPlaying().getName(), library);
                    playStats.setStartingTime(this.getTimestamp());
                    playStats.setRemainedTime(playStats.getRemainedTime() - playStats.getNowPlaying().getTimeRemaining() + currentSong.getDuration());

                    playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                    playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                    playStats.getNowPlaying().setName(currentSong.getName());
                    playStats.getNowPlaying().setSongArtist(currentSong.getArtist());

                    searchResult.put("message", "Returned to previous track successfully. The current track is " + currentSong.getName() + ".");
                }
                else {
                    SongInput songNow = user.getSongByName(playStats.getNowPlaying().getName(), library);
                    if (theStats.getNowPlaying().getTimeRemaining() != songNow.getDuration()) {
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(songNow.getName());
                        theStats.getNowPlaying().setSongArtist(songNow.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(songNow.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Returned to previous track successfully. The current track is " + songNow.getName() + ".");
                    }
                    else {
                        int id = theStats.getShuffledOrder().get(lastIndex - 1);
                        user.getUserPlayband().setLastIndex(lastIndex - 1);
                        SongInput prevSong = loadedPlaylist.getSongsInPlaylist().get(id);

                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + songNow.getDuration() + prevSong.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(prevSong.getName());
                        theStats.getNowPlaying().setSongArtist(prevSong.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(prevSong.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Returned to previous track successfully. The current track is " + prevSong.getName() + ".");

                    }
                }
            }
        }
        else {
            searchResult.put("message", "Please load a source before returning to the previous track.");

        }
        user.getUserPlayband().getPlayStats().setPaused(false);
        if (nr == 1) outputs.add(searchResult);
    }
}
