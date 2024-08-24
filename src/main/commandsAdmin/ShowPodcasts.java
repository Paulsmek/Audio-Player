package main.commandsAdmin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class ShowPodcasts extends CommandSkell {

    public void DoShow(ArrayNode outputs, Database database) {
        UserStats user = database.findByName(this.getUser());

        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
        for (PodcastInput podcast : user.getPodcasts()) {
            ObjectNode podcastResult = JsonNodeFactory.instance.objectNode();
            podcastResult.put("name", podcast.getName());

            ArrayNode episodeName = JsonNodeFactory.instance.arrayNode();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                episodeName.add(episode.getName());
            }
            podcastResult.set("episodes", episodeName);
            searchResults.add(podcastResult);
        }
        searchResult.set("result", searchResults);
        outputs.add(searchResult);
    }
}
