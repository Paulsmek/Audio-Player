package main.generalStats;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

import java.util.*;

@Getter @Setter
public class GetTop5Songs extends CommandSkell {

    public void doTop5Songs(ArrayNode outputs, Database database, LibraryInput library) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("timestamp", this.getTimestamp());

        Map<SongInput, Integer> top5Songs = new HashMap<>();
        for (UserStats user : database.getListaRezUsers()) {
            for (SongInput song : user.getLikedSongs()) {
                if (top5Songs.containsKey(song)) {
                    int currentValue = top5Songs.get(song);
                    top5Songs.put(song, currentValue + 1);
                } else {
                    top5Songs.put(song, 1);
                }
            }
        }
        List<Map.Entry<SongInput, Integer>> entryList = new ArrayList<>(top5Songs.entrySet());
        entryList.sort(Map.Entry.<SongInput, Integer>comparingByValue().reversed());

        Map<SongInput, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<SongInput, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        ArrayList<SongAndLikes> theSongs = new ArrayList<>();

        int i = 0;
        for (Map.Entry<SongInput, Integer> entry : sortedMap.entrySet()) {
            if (i < sortedMap.size()) {
                SongAndLikes song = new SongAndLikes();
                song.setSong(entry.getKey());
                song.setLikes(entry.getValue());
                theSongs.add(song);
                i++;
            } else {
                break;
            }
        }
        if (theSongs.size() < 5) {
            ArrayList<SongInput> songs = library.getSongs();
            for (SongInput songg : songs) {
                SongAndLikes song = new SongAndLikes();
                song.setSong(songg);
                song.setLikes(0);
                if (!theSongs.contains(song)) {
                    theSongs.add(song);
                }
                if (theSongs.size() == 5) break;
            }
        }

        Comparator<SongAndLikes> comparator = Comparator.comparingInt(SongAndLikes::getLikes)
                .reversed()
                .thenComparingInt(songAndLikes -> getIdSong(songAndLikes.getSong().getName(), library));

        theSongs.sort(comparator);
        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        i = 0;
        while (i != 5) {
            searchResults.add(theSongs.get(i).getSong().getName());
            i++;
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);

    }

    public int getIdSong(String name, LibraryInput library) {
        int i = 0;
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(name)) return i;
            i++;
        }
        return i;
    }
}
