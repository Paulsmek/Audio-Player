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
public class GetTop5Albums extends CommandSkell {

    public void doTop5Albums(ArrayNode outputs, Database database) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayList<AlbumAndLikes> topAlbums = new ArrayList<>();
        for (UserStats user : database.getListaRezUsers()) {
            for (Album album : user.getAlbums()) {
                int likes = this.CalculateLikes(album, database);
                AlbumAndLikes a = new AlbumAndLikes();
                a.setAlbum(album);
                a.setLikes(likes);
                a.setName(album.getName());
                topAlbums.add(a);
            }
        }
        Collections.sort(topAlbums, Comparator
                .comparing(AlbumAndLikes::getLikes, Comparator.reverseOrder())
                .thenComparing(AlbumAndLikes::getName));
        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        int i = 0;
        while (i < 5 && i < topAlbums.size()) {
            searchResults.add(topAlbums.get(i).getAlbum().getName());
            i++;
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }

    private int CalculateLikes(Album album, Database database) {
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
        return likesAlbum;
    }
}
