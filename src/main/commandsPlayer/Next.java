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
public class Next extends CommandSkell {

    public void doNext(ArrayNode outputs, Database database, LibraryInput library, int nr) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        if (user.getLoadType() != null) {
            Stats theStats = user.getUserPlayband().getPlayStats();
            if (!theStats.isShuffle()) {
                if (user.getLoadType() == SelectType.Playlists) {
                    int lastIndex = user.getUserPlayband().getLastIndex();
                    Playlist loadedPlaylist = user.getUserPlayband().getLoadedPlaylist();
                    if (theStats.getRepeat().equals("No Repeat")) {
                        if (lastIndex + 1 >= loadedPlaylist.getSongsInPlaylist().size()) {
                            user.clearSelectAndLoad();
                            searchResult.put("message", "Please load a source before skipping to the next track.");
                        }
                        else {
                            SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(lastIndex + 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() + 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                    }
                    else if (theStats.getRepeat().equals("Repeat All")) {
                        if (user.getUserPlayband().getLastIndex() + 1 >= loadedPlaylist.getSongsInPlaylist().size()) {
                            SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(0);
                            theStats.setRemainedTime(user.getPlaylistDuration(loadedPlaylist));
                            theStats.setStartingTime(this.getTimestamp());

                            user.getUserPlayband().setLastIndex(0);
                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                        else {
                            SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(lastIndex + 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() + 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                    }
                    else {
                        SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(lastIndex);
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + nextSong.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(nextSong.getName());
                        theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                    }
                }
                else if (user.getLoadType() == SelectType.Album) {
                    int lastIndex = user.getUserPlayband().getLastIndex();
                    Album loadedAlbum = user.getUserPlayband().getLoadedAlbum();
                    if (theStats.getRepeat().equals("No Repeat")) {
                        if (lastIndex + 1 >= loadedAlbum.getSongs().size()) {
                            user.clearSelectAndLoad();
                            searchResult.put("message", "Please load a source before skipping to the next track.");
                        }
                        else {
                            SongInput nextSong = loadedAlbum.getSongs().get(lastIndex + 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() + 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                    }
                    else if (theStats.getRepeat().equals("Repeat All")) {
                        if (lastIndex + 1 >= loadedAlbum.getSongs().size()) {
                            SongInput nextSong = loadedAlbum.getSongs().get(0);
                            theStats.setRemainedTime(user.getAlbumDuration(loadedAlbum));
                            theStats.setStartingTime(this.getTimestamp());

                            user.getUserPlayband().setLastIndex(0);
                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                        else {
                            SongInput nextSong = loadedAlbum.getSongs().get(lastIndex + 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() + 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(nextSong.getName());
                            theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                        }
                    }
                    else {
                        SongInput nextSong = loadedAlbum.getSongs().get(lastIndex);
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + nextSong.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setName(nextSong.getName());
                        theStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                        theStats.getNowPlaying().setTimeRemaining(nextSong.getDuration());
                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + nextSong.getName() + ".");
                    }
                }
                else if (user.getLoadType() == SelectType.Song) {
                    if (theStats.getRepeat().equals("No Repeat")) {
                        user.clearSelectAndLoad();
                        searchResult.put("message", "Please load a source before skipping to the next track.");
                    }
                    else {
                        SongInput theSong = user.getSongByName(theStats.getNowPlaying().getName(), library);
                        theStats.setRemainedTime(theSong.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        theStats.getNowPlaying().setTimeRemaining(theSong.getDuration());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + theSong.getName() + ".");
                    }
                }
                else if (user.getUserPlayband().getLoadedPodcast() != null){
                    int lastIndex = user.getUserPlayband().getLastIndex();
                    PodcastInput loadedPodcast = user.getUserPlayband().getLoadedPodcast();
                    if (theStats.getRepeat().equals("No Repeat")) {
                        if (lastIndex + 1 >= loadedPodcast.getEpisodes().size()) {
                            user.clearSelectAndLoad();
                            searchResult.put("message", "Please load a source before skipping to the next track.");
                        }
                        else {
                            EpisodeInput nextEpisode = loadedPodcast.getEpisodes().get(lastIndex + 1);
                            user.getUserPlayband().setLastIndex(user.getUserPlayband().getLastIndex() + 1);

                            theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining());
                            theStats.setStartingTime(this.getTimestamp());

                            theStats.getNowPlaying().setName(nextEpisode.getName());
                            theStats.getNowPlaying().setTimeRemaining(nextEpisode.getDuration());
                            theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + nextEpisode.getName() + ".");
                        }
                    }
                    else {
                        EpisodeInput theEpisode = user.getEpisode(theStats.getNowPlaying().getName(), user.getUserPlayband().getLoadedPodcast());
                        theStats.setRemainedTime(theStats.getRemainedTime() - theStats.getNowPlaying().getTimeRemaining() + theEpisode.getDuration());
                        theStats.setStartingTime(this.getTimestamp());

                        theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        theStats.getNowPlaying().setTimeRemaining(theEpisode.getDuration());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + theEpisode.getName() + ".");
                    }
                }
            }
            else {
                if (user.getLoadType().equals(SelectType.Playlists)) {
                    int lastIndex = user.getUserPlayband().getLastIndex();
                    Playlist loadedPlaylist = user.getUserPlayband().getLoadedPlaylist();
                    Stats playStats = user.getUserPlayband().getPlayStats();
                    if (theStats.getRepeat().equals("No Repeat")) {
                        int shuffleIndx = lastIndex;
                        if (shuffleIndx + 1 >= loadedPlaylist.getSongsInPlaylist().size()) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                            searchResult.put("message", "Please load a source before skipping to the next track.");
                        }
                        else {
                            lastIndex = shuffleIndx;
                            int songIdx = playStats.getShuffledOrder().get(++lastIndex);
                            user.getUserPlayband().setLastIndex(lastIndex);

                            SongInput currentSong = loadedPlaylist.getSongsInPlaylist().get(songIdx);
                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");

                        }

                    }
                    else if (theStats.getRepeat().equals("Repeat All")) {
                        int lastIdx = user.getUserPlayband().getLastIndex();
                        if (lastIdx + 1 >= loadedPlaylist.getSongsInPlaylist().size()) {
                            int idx = user.getUserPlayband().getPlayStats().getShuffledOrder().get(0);
                            user.getUserPlayband().setLastIndex(0);
                            SongInput currentSong = loadedPlaylist.getSongsInPlaylist().get(idx);
                            playStats.setStartingTime(this.getTimestamp());
                            playStats.setStartingTime(user.getPlaylistDuration(loadedPlaylist));

                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");


                        } else {
                            int idx = playStats.getShuffledOrder().get(lastIdx + 1);
                            user.getUserPlayband().setLastIndex(lastIndex + 1);
                            SongInput currentSong = loadedPlaylist.getSongsInPlaylist().get(idx);

                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");


                        }
                    }
                    else {
                        SongInput currentSong = user.getSongByName(playStats.getNowPlaying().getName(), library);
                        playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                        playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        playStats.getNowPlaying().setName(currentSong.getName());
                        playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");

                    }


                }
                else {
                    int lastIndex = user.getUserPlayband().getLastIndex();
                    Album loadedAlbum = user.getUserPlayband().getLoadedAlbum();
                    Stats playStats = user.getUserPlayband().getPlayStats();
                    if (theStats.getRepeat().equals("No Repeat")) {
                        int shuffleIndx = lastIndex;
                        if (shuffleIndx + 1 >= loadedAlbum.getSongs().size()) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                            searchResult.put("message", "Please load a source before skipping to the next track.");
                        }
                        else {
                            lastIndex = shuffleIndx;
                            int songIdx = playStats.getShuffledOrder().get(++lastIndex);
                            user.getUserPlayband().setLastIndex(lastIndex);

                            SongInput currentSong = loadedAlbum.getSongs().get(songIdx);
                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");

                        }

                    }
                    else if (theStats.getRepeat().equals("Repeat All")) {
                        int lastIdx = user.getUserPlayband().getLastIndex();
                        if (lastIdx + 1 >= loadedAlbum.getSongs().size()) {
                            int idx = user.getUserPlayband().getPlayStats().getShuffledOrder().get(0);
                            user.getUserPlayband().setLastIndex(0);
                            SongInput currentSong = loadedAlbum.getSongs().get(idx);
                            playStats.setStartingTime(this.getTimestamp());
                            playStats.setStartingTime(user.getAlbumDuration(loadedAlbum));

                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");


                        } else {
                            int idx = playStats.getShuffledOrder().get(lastIdx + 1);
                            user.getUserPlayband().setLastIndex(lastIndex + 1);
                            SongInput currentSong = loadedAlbum.getSongs().get(idx);

                            playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                            playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                            searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");


                        }
                    }
                    else {
                        SongInput currentSong = user.getSongByName(playStats.getNowPlaying().getName(), library);
                        playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration());
                        playStats.getNowPlaying().setTimeStart(this.getTimestamp());
                        playStats.getNowPlaying().setName(currentSong.getName());
                        playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                        searchResult.put("message", "Skipped to next track successfully. The current track is " + playStats.getNowPlaying().getName() + ".");

                    }
                }
            }
        }
        else {
            searchResult.put("message", "Please load a source before skipping to the next track.");

        }
        user.getUserPlayband().getPlayStats().setPaused(false);
        if (nr == 1) outputs.add(searchResult);
    }
}
