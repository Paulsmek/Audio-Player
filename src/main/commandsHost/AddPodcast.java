package main.commandsHost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.Database;
import main.advancedUserStats.CommandSkell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class AddPodcast extends CommandSkell {
    private String name;
    private ArrayList<EpisodeInput> episodes = new ArrayList<>();
    public void doAddPodcast(ArrayNode outputs, Database database, LibraryInput library, JsonNode commandNode) {
        UserStats user = database.findByName(this.getUser());
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());

        if (user != null) {
            if (user.getUserType().equals("host")) {
                if (!user.hasPodcast(this.getName())) {
                    JsonNode episodesInCommand0 = commandNode.path("episodes");
                    Set<String> uniqueEpisodeNames = new HashSet<>();
                    if (episodesInCommand0 != null && episodesInCommand0.isArray()) {
                        for (JsonNode node : episodesInCommand0) {
                            String songName = node.get("name").asText();
                            if (!uniqueEpisodeNames.add(songName)) {
                                searchResult.put("message", this.getUser() + " has the same episode in this podcast.");
                                outputs.add(searchResult);
                                return;
                            }
                        }
                    }
                    PodcastInput podcast = new PodcastInput();
                    podcast.setName(this.getName());
                    podcast.setOwner(this.getUser());

                    JsonNode episodesInCommand = commandNode.path("episodes");
                    if (episodesInCommand != null && episodesInCommand.isArray()) {
                        for (JsonNode node : episodesInCommand) {
                            EpisodeInput episode = new EpisodeInput();
                            if (node.get("name") != null) episode.setName(node.get("name").asText());
                            if (node.get("duration") != null) episode.setDuration(node.get("duration").asInt());
                            if (node.get("description") != null)
                                episode.setDescription(node.get("description").asText());
                            episodes.add(episode);
                        }
                    }
                    podcast.setEpisodes(episodes);
                    library.getPodcasts().add(podcast);
                    user.getPodcasts().add(podcast);
                    searchResult.put("message", this.getUser() + " has added new podcast successfully.");
                } else {
                    searchResult.put("message", this.getUser() + " has another podcast with the same name.");
                }
            } else {
                searchResult.put("message", this.getUser() + " is not a host.");
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
        }
        outputs.add(searchResult);
    }
}
