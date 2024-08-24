package main.advancedUserStats;

import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.commandsArtist.Album;
import main.commandsPlayList.Playlist;

@Getter @Setter
public class Playband {
    private Stats playStats = new Stats();
    private PodcastInput loadedPodcast;
    private Playlist loadedPlaylist;
    private Album loadedAlbum;
    private int lastIndex;

    public void EditRemainTime(int currentTime, Database database, String name, LibraryInput library) {
        if (!playStats.isPaused()) {
            playStats.setRemainedTime(playStats.getRemainedTime() - currentTime + playStats.getStartingTime());
            playStats.setStartingTime(currentTime);

            playStats.getNowPlaying().setTimeRemaining(playStats.getNowPlaying().getTimeRemaining() - currentTime + playStats.getNowPlaying().getTimeStart());
            playStats.getNowPlaying().setTimeStart(currentTime);
        }
        UserStats user = database.findByName(name);
        if (playStats.getNowPlaying().getTimeRemaining() <= 0 && user.getLoadType() != null) {
            if (!this.getPlayStats().isShuffle()) {
                if (playStats.getRemainedTime() > 0) {
                    Boolean ok = true;
                    while (playStats.getNowPlaying().getTimeRemaining() <= 0 && ok) {
                        if (loadedPlaylist != null && lastIndex + 1 != loadedPlaylist.getSongsInPlaylist().size()) {
                            SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(++lastIndex);
                            if (this.getPlayStats().getRepeat().equals("Repeat Current Song") && user.getLoadType().equals(SelectType.Playlists)) {
                                nextSong = loadedPlaylist.getSongsInPlaylist().get(--lastIndex);
                            }
                            playStats.getNowPlaying().setName(nextSong.getName());
                            playStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(nextSong.getDuration() + playStats.getNowPlaying().getTimeRemaining());


                        } else if (loadedPodcast != null && lastIndex + 1 != loadedPodcast.getEpisodes().size()) {
                            EpisodeInput nextEpisode = loadedPodcast.getEpisodes().get(++lastIndex);
                            playStats.getNowPlaying().setName(nextEpisode.getName());
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(nextEpisode.getDuration() + playStats.getNowPlaying().getTimeRemaining());

                        } else if (loadedAlbum != null && lastIndex + 1 != loadedAlbum.getSongs().size()) {
                            SongInput nextSong = loadedAlbum.getSongs().get(++lastIndex);
                            if (this.getPlayStats().getRepeat().equals("Repeat Current Song") && user.getLoadType().equals(SelectType.Album)) {
                                nextSong = loadedAlbum.getSongs().get(--lastIndex);
                            }
                            playStats.getNowPlaying().setName(nextSong.getName());
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                            playStats.getNowPlaying().setTimeRemaining(nextSong.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                        }
                        else {
                            ok = false;
                        }
                    }
                }
                else {
                    if (user.getLoadType() == SelectType.Song) {
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        }
                        else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Once")) {
                            String songName = playStats.getNowPlaying().getName();
                            int duration = user.getSongByName(songName, library).getDuration();
                            playStats.getNowPlaying().setTimeRemaining(duration + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.setRepeat("No Repeat");
                            playStats.setRemainedTime(playStats.getNowPlaying().getTimeRemaining());
                            playStats.setStartingTime(playStats.getNowPlaying().getTimeStart());
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Infinite")) {
                            String songName = playStats.getNowPlaying().getName();
                            int duration = user.getSongByName(songName, library).getDuration();
                            int minus = (playStats.getNowPlaying().getTimeRemaining() * -1) % duration;
                            playStats.getNowPlaying().setTimeRemaining(duration - minus);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.setRemainedTime(playStats.getNowPlaying().getTimeRemaining());
                            playStats.setStartingTime(playStats.getNowPlaying().getTimeStart());
                        }
                    } else if (user.getLoadType() == SelectType.Podcast) {
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Once")) {
                            String podcastName = this.getLoadedPodcast().getName();
                            int duration = user.getPodcastDuration(podcastName, library);
                            if (user.getUserPlayband().getPlayStats().getRemainedTime() + duration < 1) {
                                playStats.setRepeat("No Repeat");
                                user.clearSelectAndLoad();
                            } else {
                                int timePassed = -1 * (user.getUserPlayband().getPlayStats().getRemainedTime() + duration);
                                int currentEpisodeNr = user.getEpisodeNrByTime(timePassed, this.getLoadedPodcast());
                                this.setLastIndex(currentEpisodeNr);
                                EpisodeInput episode = this.getLoadedPodcast().getEpisodes().get(currentEpisodeNr);
                                int episodeTime = user.getEpisodeTimeRemain(timePassed, this.getLoadedPodcast());
                                playStats.getNowPlaying().setTimeRemaining(episodeTime);
                                playStats.getNowPlaying().setTimeStart(currentTime);
                                playStats.getNowPlaying().setName(episode.getName());

                                playStats.setRepeat("No Repeat");
                                playStats.setRemainedTime(duration - timePassed);
                                playStats.setStartingTime(currentTime);
                            }
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Infinite")) {
                            String podcastName = this.getLoadedPodcast().getName();
                            int duration = user.getPodcastDuration(podcastName, library);
                            int minus = (playStats.getRemainedTime() * -1) % duration;

                            int timePassed = duration - minus;
                            int currentEpisodeNr = user.getEpisodeNrByTime(timePassed, this.getLoadedPodcast());
                            this.setLastIndex(currentEpisodeNr);
                            EpisodeInput episode = this.getLoadedPodcast().getEpisodes().get(currentEpisodeNr);
                            int episodeTime = user.getEpisodeTimeRemain(timePassed, this.getLoadedPodcast());
                            playStats.getNowPlaying().setTimeRemaining(episodeTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setName(episode.getName());

                            playStats.setRepeat("No Repeat");
                            playStats.setRemainedTime(duration - timePassed);
                            playStats.setStartingTime(currentTime);

                        }
                    } else if (user.getLoadType() == SelectType.Playlists) {
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat All")) {
                            if (user.getUserPlayband().getLastIndex() + 1 < user.getUserPlayband().getLoadedPlaylist().getSongsInPlaylist().size()) {
                                SongInput nextSong = loadedPlaylist.getSongsInPlaylist().get(++lastIndex);
                                playStats.getNowPlaying().setName(nextSong.getName());
                                playStats.getNowPlaying().setTimeStart(currentTime);
                                playStats.getNowPlaying().setSongArtist(nextSong.getArtist());
                                playStats.getNowPlaying().setTimeRemaining(nextSong.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            }
                            else {
                                int duration = user.getPlaylistDuration(this.getLoadedPlaylist());
                                int minus = (playStats.getRemainedTime() * -1) % duration;
                                playStats.setRemainedTime(duration - minus);
                                playStats.setStartingTime(currentTime);

                                int currentSongNr = user.getSongNrByTime(duration - playStats.getRemainedTime(), this.getLoadedPlaylist());
                                this.setLastIndex(currentSongNr);
                                SongInput currentSong = this.getLoadedPlaylist().getSongsInPlaylist().get(currentSongNr);
                                int currentSongTimeRemain = user.getSongTimeRemainPlaylist(duration - playStats.getRemainedTime(), this.getLoadedPlaylist());
                                playStats.getNowPlaying().setTimeRemaining(currentSongTimeRemain);
                                playStats.getNowPlaying().setTimeStart(currentTime);
                                playStats.getNowPlaying().setSongArtist(currentSong.getArtist());
                                playStats.getNowPlaying().setName(currentSong.getName());
                            }

                        }
                        else {
                            SongInput songNow = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            while (playStats.getNowPlaying().getTimeRemaining() <= 0) {
                                playStats.getNowPlaying().setTimeRemaining(songNow.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            }
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.setRemainedTime(playStats.getNowPlaying().getTimeRemaining());
                        }
                    }
                    else { //Album
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("No Repeat")) {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat All")) {
                            int duration = user.getAlbumDuration(this.getLoadedAlbum());
                            int minus = (playStats.getRemainedTime() * -1) % duration;
                            playStats.setRemainedTime(duration - minus);
                            playStats.setStartingTime(currentTime);

                            int currentSongNr = user.getSongNrByTime(duration - playStats.getRemainedTime(), this.getLoadedPlaylist());
                            this.setLastIndex(currentSongNr);
                            SongInput currentSong = this.getLoadedAlbum().getSongs().get(currentSongNr);
                            int currentSongTimeRemain = user.getSongTimeRemainAlbum(duration - playStats.getRemainedTime(), this.getLoadedAlbum());
                            playStats.getNowPlaying().setTimeRemaining(currentSongTimeRemain);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setName(currentSong.getName());
                            playStats.getNowPlaying().setSongArtist(currentSong.getArtist());

                        }
                        else {
                            SongInput songNow = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            while (playStats.getNowPlaying().getTimeRemaining() <= 0) {
                                playStats.getNowPlaying().setTimeRemaining(songNow.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            }
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.setRemainedTime(playStats.getNowPlaying().getTimeRemaining());
                        }
                    }
                }
            }
            else {
                if (user.getLoadType().equals(SelectType.Playlists)) {
                    int shuffleIndx = lastIndex;
                    if (shuffleIndx + 1 >= this.getLoadedPlaylist().getSongsInPlaylist().size()) {
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat All")) {
                            int idx = playStats.getShuffledOrder().get(0);
                            lastIndex = 0;
                            SongInput song = loadedPlaylist.getSongsInPlaylist().get(idx);
                            playStats.setRemainedTime(user.getPlaylistDuration(loadedPlaylist));
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Current Song")) {
                            SongInput song = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            playStats.setRemainedTime(playStats.getRemainedTime() - playStats.getNowPlaying().getTimeRemaining() + song.getDuration());
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        } else {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        }
                    }
                    else {
                        if (!user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Current Song")) {
                            lastIndex = shuffleIndx;
                            while (playStats.getNowPlaying().getTimeRemaining() < 1 && lastIndex + 1 < this.getLoadedPlaylist().getSongsInPlaylist().size()) {
                                this.setLastIndex(++lastIndex);
                                int songIdx = playStats.getShuffledOrder().get(this.getLastIndex());
                                SongInput currentSong = this.getLoadedPlaylist().getSongsInPlaylist().get(songIdx);
                                playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                                playStats.getNowPlaying().setTimeStart(currentTime);
                                playStats.getNowPlaying().setName(currentSong.getName());
                                playStats.getNowPlaying().setSongArtist(currentSong.getArtist());

                            }
                        } else {
                            SongInput song = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            playStats.setRemainedTime(playStats.getRemainedTime() - playStats.getNowPlaying().getTimeRemaining() + song.getDuration());
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        }
                    }
                }
                else {
                    int shuffleIndx = lastIndex;
                    if (shuffleIndx + 1 >= this.getLoadedAlbum().getSongs().size()) {
                        if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat All")) {
                            int idx = playStats.getShuffledOrder().get(0);
                            lastIndex = 0;
                            SongInput song = loadedAlbum.getSongs().get(idx);
                            playStats.setRemainedTime(user.getAlbumDuration(loadedAlbum));
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        } else if (user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Current Song")) {
                            SongInput song = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            playStats.setRemainedTime(playStats.getRemainedTime() - playStats.getNowPlaying().getTimeRemaining() + song.getDuration());
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        } else {
                            playStats.setRepeat("No Repeat");
                            user.clearSelectAndLoad();
                        }
                    }
                    else {
                        if (!user.getUserPlayband().getPlayStats().getRepeat().equals("Repeat Current Song")) {
                            lastIndex = shuffleIndx;
                            while (playStats.getNowPlaying().getTimeRemaining() < 1 && lastIndex + 1 < this.getLoadedAlbum().getSongs().size()) {
                                this.setLastIndex(lastIndex + 1);
                                int songIdx = playStats.getShuffledOrder().get(this.getLastIndex());
                                SongInput currentSong = this.getLoadedAlbum().getSongs().get(songIdx);
                                playStats.getNowPlaying().setTimeRemaining(currentSong.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                                playStats.getNowPlaying().setTimeStart(currentTime);
                                playStats.getNowPlaying().setName(currentSong.getName());
                                playStats.getNowPlaying().setSongArtist(currentSong.getArtist());

                            }
                        } else {
                            SongInput song = user.getSongByName(playStats.getNowPlaying().getName(), library);
                            playStats.setRemainedTime(playStats.getRemainedTime() - playStats.getNowPlaying().getTimeRemaining() + song.getDuration());
                            playStats.setStartingTime(currentTime);
                            playStats.getNowPlaying().setTimeStart(currentTime);
                            playStats.getNowPlaying().setTimeRemaining(song.getDuration() + playStats.getNowPlaying().getTimeRemaining());
                            playStats.getNowPlaying().setName(song.getName());
                            playStats.getNowPlaying().setSongArtist(song.getArtist());
                        }
                    }
                }

            }

        }


    }

}
