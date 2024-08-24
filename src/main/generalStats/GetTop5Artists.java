package main.generalStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;
import main.commandsArtist.Album;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Getter @Setter
public class GetTop5Artists extends CommandSkell {
    public void doTop5Artists(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayList<ArtistAndLikes> topArtists = new ArrayList<>();
        for (UserStats user : database.getListaRezUsers()) {
            if (user.getUserType().equals("artist")) {
                ArtistAndLikes a = new ArtistAndLikes();
                a.setArtist(user);
                a.setName(user.getName());
                a.setLikes(this.CalculateLikes(user, database));
                topArtists.add(a);
            }
        }
        Collections.sort(topArtists, Comparator
                .comparingInt(ArtistAndLikes::getLikes).reversed()
                .thenComparing(ArtistAndLikes::getName));
        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        int i = 0;
        while (i < 5 && i < topArtists.size()) {
            searchResults.add(topArtists.get(i).getArtist().getName());
            i++;
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }
    private int CalculateLikes(UserStats theUser, Database database) {
        int likes = 0;
        for (Album album : theUser.getAlbums()) {
            int likesAlbum = 0;
            for (SongInput song : album.getSongs()) {
                int likesSong = 0;
                for (UserStats user : database.getListaRezUsers()) {
                    if (user.getLikedSongs().contains(song)) {
                        likesSong++;
                    }
                }
                likesAlbum += likesSong;
            }
            likes += likesAlbum;
        }
        return likes;
    }
}
