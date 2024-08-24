package main.commandsPlayer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.Stats;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class Backward extends CommandSkell {

    public void doBackward(ArrayNode outputs, Database database, LibraryInput library) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user.getLoadType() != null) {
            if (user.getLoadType() == SelectType.Podcast) {
                PodcastInput thePodcast = user.getUserPlayband().getLoadedPodcast();
                EpisodeInput nowEpisode = thePodcast.getEpisodes().get(user.getUserPlayband().getLastIndex());
                Stats theStats = user.getUserPlayband().getPlayStats();
                int passedTime = nowEpisode.getDuration() - theStats.getNowPlaying().getTimeRemaining();
                if (passedTime > 90) {
                    theStats.getNowPlaying().setTimeRemaining(theStats.getNowPlaying().getTimeRemaining() + 90);
                    theStats.setRemainedTime(theStats.getRemainedTime() + 90);
                    theStats.setStartingTime(this.getTimestamp());
                    theStats.getNowPlaying().setTimeStart(this.getTimestamp());
                    searchResult.put("message", "Rewound successfully.");
                }
                else {
                    Prev prev = new Prev();
                    prev.setUser(this.getUser());
                    prev.setCommand(this.getCommand());
                    prev.setTimestamp(this.getTimestamp());
                    prev.doPrev(outputs, database, library, 2);
                }

            }
            else {
                searchResult.put("message", "The loaded source is not a podcast.");
            }
        }
        else {
            searchResult.put("message", "Please load a source before skipping forward.");
        }


        outputs.add(searchResult);
    }
}
