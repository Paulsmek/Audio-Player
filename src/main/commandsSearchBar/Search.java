package main.commandsSearchBar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import main.advancedUserStats.UserStats;

import java.util.ArrayList;
import java.util.Iterator;

@Getter @Setter
public class Search extends CommandSkell {
    ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
    ArrayNode artistOfSongs = JsonNodeFactory.instance.arrayNode();;
    private int numberFound = 0;

    public void getSongs(JsonNode commandNode, LibraryInput library) {
        ArrayList<SongInput> allSongs = library.getSongs();
        JsonNode filterNode = commandNode.get("filters");
        SongInput FilterSong = new SongInput();
        FilterSong.setReleaseYear(0);
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    FilterSong.setName(nameFilter);
                    break;
                case "album":
                    String albumFilter = filter.asText();
                    FilterSong.setAlbum(albumFilter);
                    break;

                case "tags":
                    FilterSong.setTags(new ArrayList<>());
                    for (JsonNode tag : filter) {
                        String oneTag = tag.toString().toLowerCase();
                        String theTag = oneTag.substring(1, oneTag.length() - 1);
                        FilterSong.getTags().add(theTag);
                    }
                    break;

                case "lyrics":
                    String lyricsFilter = filter.asText().toLowerCase();
                    FilterSong.setLyrics(lyricsFilter);
                    break;

                case "genre":
                    String genreFilter = filter.asText();
                    FilterSong.setGenre(genreFilter);
                    break;

                case "releaseYear":
                    String yearComp = filter.asText();
                    char firstChar = yearComp.charAt(0);
                    String numericPart = yearComp.substring(1);
                    int year = Integer.parseInt(numericPart);
                    year *= 10;
                    if (firstChar == '<') {
                        year++;
                    } else {
                        year += 2;
                    }
                    FilterSong.setReleaseYear(year);
                    break;

                case "artist":
                    String artistFilter = filter.asText();
                    FilterSong.setArtist(artistFilter);
                    break;

            }

        }
        for (SongInput song : allSongs) {
            if (FilterSong.getName() != null && !song.getName().startsWith(FilterSong.getName())) {
                continue;
            }
            if (FilterSong.getAlbum() != null && !song.getAlbum().equals(FilterSong.getAlbum())) {
                continue;
            }
            if (FilterSong.getTags() != null && !song.getTags().containsAll(FilterSong.getTags())) {
                continue;
            }

            if (FilterSong.getLyrics() != null && !song.getLyrics().toLowerCase().contains(FilterSong.getLyrics())) {
                continue;
            }
            if (FilterSong.getGenre() != null && !song.getGenre().equalsIgnoreCase(FilterSong.getGenre())) {
                continue;
            }
            if (FilterSong.getReleaseYear() != 0) {
                int comp = FilterSong.getReleaseYear() % 10;
                int releaseYear = FilterSong.getReleaseYear() / 10;
                boolean ok = true;
                if (comp == 1 && song.getReleaseYear() > releaseYear) {
                    ok = false;
                } else if (comp == 2 && song.getReleaseYear() < releaseYear) {
                    ok = false;
                }
                if (!ok) continue;
            }
            if (FilterSong.getArtist() != null && !song.getArtist().equals(FilterSong.getArtist())) {
                continue;
            }
            if (numberFound < 5) {
                searchResults.add(song.getName());
                artistOfSongs.add(song.getArtist());
                numberFound++;
            }
        }
    }

    public void getPodcast(JsonNode commandNode, LibraryInput library) {
        ArrayList<PodcastInput> allPodcasts = library.getPodcasts();
        JsonNode filterNode = commandNode.get("filters");
        PodcastInput FilterPodcast = new PodcastInput();
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    FilterPodcast.setName(nameFilter);
                    break;

                case "owner":
                    String ownerFilter = filter.asText();
                    FilterPodcast.setOwner(ownerFilter);
                    break;
            }
        }
        for (PodcastInput podcast : allPodcasts) {
            if (FilterPodcast.getName() != null && !podcast.getName().startsWith(FilterPodcast.getName())) {
                continue;
            }
            if (FilterPodcast.getOwner() != null && !podcast.getOwner().equals(FilterPodcast.getOwner())) {
                continue;
            }

            if (numberFound < 5) {
                searchResults.add(podcast.getName());
                numberFound++;
            }
        }
    }

    public void getPlaylists(JsonNode commandNode, LibraryInput library, Database database) {
        JsonNode filterNode = commandNode.get("filters");
        Playlist filterPlaylist = new Playlist();
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    filterPlaylist.setPlayListName(nameFilter);
                    break;

                case "owner":
                    String ownerFilter = filter.asText();
                    filterPlaylist.setUser(ownerFilter);
                    break;
            }
        }
        for (UserStats user : database.getListaRezUsers()) {
            for (Playlist playlist : user.getUserPlaylists()) {
                if (user.getName().equals(this.getUser()) || playlist.getVisibility().equals("public")) {
                    if (filterPlaylist.getPlayListName() != null && !playlist.getPlayListName().startsWith(filterPlaylist.getPlayListName())) {
                        continue;
                    }
                    if (filterPlaylist.getUser() != null && !playlist.getUser().equals(filterPlaylist.getUser())) {
                        continue;
                    }

                    if (numberFound < 5) {
                        searchResults.add(playlist.getPlayListName());
                        numberFound++;
                    }
                }
            }
        }

    }

    public void getAlbums(JsonNode commandNode, Database database) {
        JsonNode filterNode = commandNode.get("filters");
        Album filterAlbum = new Album();
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    filterAlbum.setName(nameFilter);
                    break;

                case "owner":
                    String ownerFilter = filter.asText();
                    filterAlbum.setArtist(ownerFilter);
                    break;
            }
        }
        for (UserStats user : database.getListaRezUsers()) {
            for (Album album : user.getAlbums()) {
                if (filterAlbum.getName() != null && !album.getName().startsWith(filterAlbum.getName())) {
                    continue;
                }
                if (filterAlbum.getArtist() != null && !album.getArtist().equals(filterAlbum.getArtist())) {
                    continue;
                }

                if (numberFound < 5) {
                    searchResults.add(album.getName());
                    numberFound++;
                }
            }

        }

    }

    public void getArtists(JsonNode commandNode, Database database) {
        JsonNode filterNode = commandNode.get("filters");
        UserStats filterUser = new UserStats();
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    filterUser.setName(nameFilter);
                    break;

                case "owner":
                    break;
            }
        }
        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("artist")) {
                if (filterUser.getName() != null && !user.getName().startsWith(filterUser.getName())) {
                    continue;
                }

                if (numberFound < 5) {
                    searchResults.add(user.getName());
                    numberFound++;
                }

            }
        }
    }

    public void getHosts(JsonNode commandNode, Database database) {
        JsonNode filterNode = commandNode.get("filters");
        UserStats filterUser = new UserStats();
        Iterator<String> fieldNames = filterNode.fieldNames();
        while (fieldNames.hasNext()) {
            String filterKey = fieldNames.next();
            JsonNode filter = filterNode.get(filterKey);
            switch (filterKey) {
                case "name":
                    String nameFilter = filter.asText();
                    filterUser.setName(nameFilter);
                    break;

                case "owner":
                    break;
            }
        }
        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("host")) {
                if (filterUser.getName() != null && !user.getName().startsWith(filterUser.getName())) {
                    continue;
                }

                if (numberFound < 5) {
                    searchResults.add(user.getName());
                    numberFound++;
                }

            }
        }
    }

    public void addSongToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Song);
        database.findByName(this.getUser()).setArtistOfSongs(artistOfSongs);
        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }

    public void addPodcastToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Podcast);

        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }

    public void addPlaylistToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Playlists);

        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }

    public void addArtistsToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Artist);

        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }

    public void addHostsToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Host);

        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }

    public void addAlbumsToOut(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        searchResult.put("message", "Search returned " + numberFound + " results");
        searchResult.set("results", this.getSearchResults());

        database.findByName(this.getUser()).setSearchResults(searchResults);
        database.findByName(this.getUser()).setSearchType(SelectType.Album);

        database.findByName(this.getUser()).clearSelectAndLoad();
        outputs.add(searchResult);
    }
}
