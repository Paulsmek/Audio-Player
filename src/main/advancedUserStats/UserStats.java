package main.advancedUserStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.commandsArtist.Album;
import main.commandsHost.Announcement;
import main.commandsArtist.Event;
import main.commandsArtist.Merch;
import main.commandsPlayList.Playlist;

import java.util.ArrayList;
@Getter @Setter
public class UserStats {
    private String name;
    private ArrayNode searchResults = null;
    private ArrayNode artistOfSongs = null;
    private SelectType selectType;
    private SelectType searchType;
    private SelectType loadType = null;
    private String selectedName;
    private String selectedArtist;
    private Playband userPlayband = new Playband();
    private ArrayList<Playlist> userPlaylists = new ArrayList<>();
    private ArrayList<SongInput> likedSongs = new ArrayList<>();
    private ArrayList<PodcastSave> savedPodcasts = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();
    //etapa 2
    private String userType = "user";
    private boolean onlineStatus = true;
    private String page = "Home";
    private ArrayList<Album> albums = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Merch> merch = new ArrayList<>();
    private ArrayList<PodcastInput> podcasts = new ArrayList<>();
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private String nameOfUserOfPage;
    private int age;
    private String city;

    public SongInput getSongByName(String name, LibraryInput library) {
        ArrayList<SongInput> songs = library.getSongs();
        for (SongInput song : songs) {
            if (song.getName().equals(name)) return song;
        }
        return null;
    }
    public SongInput getSongByNameArtist(String name, LibraryInput library, String artist) {
        ArrayList<SongInput> songs = library.getSongs();
        for (SongInput song : songs) {
            if (song.getName().equals(name) && song.getArtist().equals(artist)) return song;
        }
        return null;
    }

    public boolean PlaylistExists(String name) {
        for (Playlist playList : this.userPlaylists) {
            if (playList.getPlayListName().equals(name)) return true;
        }
        return false;
    }

    public boolean PlaylistIsFollowed(String name) {
        for (Playlist playList : this.followedPlaylists) {
            if (playList.getPlayListName().equals(name)) return true;
        }
        return false;

    }

    public boolean SongExists(String name) {
        for (SongInput song : this.getLikedSongs()) {
            if (song.getName().equals(name)) return true;
        }
        return false;
    }

    public PodcastInput getPodcastByName(String name, LibraryInput library) {
        ArrayList<PodcastInput> podcasts = library.getPodcasts();
        for (PodcastInput podcast : podcasts) {
            if (podcast.getName().equals(name)) return podcast;
        }
        return null;
    }

    public PodcastInput getPodcastByNameLocal(String name) {
        ArrayList<PodcastInput> podcasts = this.getPodcasts();
        for (PodcastInput podcast : podcasts) {
            if (podcast.getName().equals(name)) return podcast;
        }
        return null;
    }


    public Album getAlbumByName(String name, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            for (Album album : user.getAlbums()) {
                if (album.getName().equals(name)) return album;
            }
        }
        return null;
    }

    public Event getEventByName(String name) {
        for (Event event : this.getEvents()) {
            if (event.getName().equals(name)) return event;
        }

        return null;
    }

    public Album getAlbumByNameLocal(String name) {
        for (Album album : this.getAlbums()) {
            if (album.getName().equals(name)) return album;
        }

        return null;
    }

    public Playlist getPlaylistByNameGlobal(String name, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            for (Playlist playlist : user.getUserPlaylists()) {
                if (playlist.getPlayListName().equals(name)) return playlist;
            }
        }
        return null;
    }

    public UserStats getOwnerOfPlaylist(String name, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            for (Playlist playlist : user.getUserPlaylists()) {
                if (playlist.getPlayListName().equals(name)) return user;
            }
        }
        return null;
    }

    public Playlist getPlaylistByName(String name, Database database) {
        for (UserStats user : database.getListaRezUsers()) {
            for (Playlist playlist : user.getUserPlaylists()) {
                if (playlist.getPlayListName().equals(name)) return playlist;
            }
        }
        return null;
    }

    public Announcement getAnnouncementByName(String name) {
        for (Announcement ann : announcements) {
            if (ann.getName().equals(name)) return ann;
        }
        return null;
    }

    public int getPodcastDuration(String name, LibraryInput library) {
        int totalTime = 0;
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(name)) {
                for (EpisodeInput episode : podcast.getEpisodes()) {
                    totalTime += episode.getDuration();
                }
            }
        }
        return totalTime;
    }

    public int getPlaylistDuration(Playlist playlist) {
        int totalTime = 0;
        if (playlist != null) {
            for (SongInput song : playlist.getSongsInPlaylist()) {
                totalTime += song.getDuration();
            }
        }
        return totalTime;
    }

    public int getAlbumDuration(Album album) {
        int totalTime = 0;
        if (album != null) {
            for (SongInput song : album.getSongs()) {
                totalTime += song.getDuration();
            }
        }
        return totalTime;
    }

    public boolean IsPodcastSaved(String name) {
        for (PodcastSave podcast : this.savedPodcasts) {
            if (podcast.getPodcastName().equals(name)) return true;
        }
        return false;
    }

    public PodcastSave getPodcastBySavedPodcast(String name) {
        for (PodcastSave podcast : this.savedPodcasts) {
            if (podcast.getPodcastName().equals(name)) return podcast;
        }
        return null;
    }

    public EpisodeInput getEpisode(String name, PodcastInput podcast) {
        for (EpisodeInput episode : podcast.getEpisodes()) {
            if (episode.getName().equals(name)) return episode;
        }
        return null;

    }

    public int getSongNrByTime(int time, Playlist playlist) {
        int time2 = time;
        int indice = 0;
        for (SongInput song : playlist.getSongsInPlaylist()) {
            if (time2 - song.getDuration() < 1) return indice;
            time2 -= song.getDuration();
            indice++;
        }
        return 0;

    }

    public int getSongTimeRemainPlaylist(int time, Playlist playlist) {
        int time2 = time;
        for (SongInput song : playlist.getSongsInPlaylist()) {
            if (time2 - song.getDuration() < 1) return (song.getDuration() - time2);
            time2 -= song.getDuration();
        }
        return 0;

    }
    public int getSongTimeRemainAlbum(int time, Album album) {
        int time2 = time;
        for (SongInput song : album.getSongs()) {
            if (time2 - song.getDuration() < 1) return (song.getDuration() - time2);
            time2 -= song.getDuration();
        }
        return 0;

    }


    public int getEpisodeNrByTime(int time, PodcastInput podcast) {
        int time2 = time;
        int indice = 0;
        for (EpisodeInput episode : podcast.getEpisodes()) {
            if (time2 - episode.getDuration() < 1) return indice;
            time2 -= episode.getDuration();
            indice++;
        }
        return 0;

    }

    public int getEpisodeTimeRemain(int time, PodcastInput podcast) {
        int time2 = time;
        for (EpisodeInput episode : podcast.getEpisodes()) {
            if (time2 - episode.getDuration() < 1) return (episode.getDuration() - time2);
            time2 -= episode.getDuration();
        }
        return 0;

    }

    public void clearSelectAndLoad() {
        if (this.getLoadType() != SelectType.Podcast) {
            this.setSelectType(null);
            this.setSelectedName(null);
            this.setLoadType(null);
            this.getUserPlayband().setPlayStats(new Stats());
            this.getUserPlayband().setLoadedPlaylist(null);
            this.getUserPlayband().setLoadedPodcast(null);
        } else {
            PodcastSave savePodcast = new PodcastSave();
            savePodcast.setPodcastName(this.getSelectedName());
            savePodcast.setRemainedTime(this.getUserPlayband().getPlayStats().getRemainedTime());
            savePodcast.setRemainedIndex(this.getUserPlayband().getLastIndex());
            savePodcast.setRemainedEpisodeTime(this.getUserPlayband().getPlayStats().getNowPlaying().getTimeRemaining());
            savePodcast.setEpisodeName(this.getUserPlayband().getPlayStats().getNowPlaying().getName());
            this.getSavedPodcasts().add(savePodcast);

            this.setSelectType(null);
            this.setSelectedName(null);
            this.setLoadType(null);
            this.getUserPlayband().setPlayStats(new Stats());
            this.getUserPlayband().setLoadedPlaylist(null);
            this.getUserPlayband().setLoadedPodcast(null);
            this.getUserPlayband().setLoadedAlbum(null);
        }
    }

    public void clearSelect() {
        this.setSelectType(null);
        //this.setSelectedName(null);
    }

    public void clearSearch() {
        this.setSearchResults(null);
    }

    public boolean hasAlbum(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasEvent(String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasPodcast(String name) {
        for (PodcastInput podcast : podcasts) {
            if (podcast.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasMerch(String name) {
        for (Merch mer : merch) {
            if (mer.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasAnnouncement(String name) {
        for (Announcement ann : announcements) {
            if (ann.getName().equals(name)) return true;
        }
        return false;
    }

    public int getLikesOfSong(SongInput song, Database database) {
        int likes = 0;
        for (UserStats user : database.getListaRezUsers()) {
            if (user.getLikedSongs().contains(song)) {
                likes++;
            }
        }
        return likes;
    }
}
