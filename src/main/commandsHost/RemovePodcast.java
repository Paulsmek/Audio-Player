package main.commandsHost;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import lombok.Getter;
import lombok.Setter;
import main.advancedUserStats.Database;
import main.advancedUserStats.SelectType;
import main.advancedUserStats.UserStats;
import main.advancedUserStats.CommandSkell;

@Getter @Setter
public class RemovePodcast extends CommandSkell {
    private String name;

    public void DoRemove(ArrayNode outputs, Database database, LibraryInput library) {
        ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
        searchResult.put("command", this.getCommand());
        searchResult.put("user", this.getUser());
        searchResult.put("timestamp", this.getTimestamp());
        UserStats userOfPodcast= database.findByName(this.getUser());

        if(userOfPodcast != null) {
            if (userOfPodcast.getUserType().equals("host")) {
                PodcastInput podcastToDelete = userOfPodcast.getPodcastByNameLocal(this.getName());
                if (podcastToDelete != null) {
                    for (UserStats user : database.getListaRezUsers()) {
                        if (user != userOfPodcast && user.getLoadType() != null) {
                            if (user.getLoadType().equals(SelectType.Podcast)) {
                                PodcastInput thePodcast = user.getUserPlayband().getLoadedPodcast();
                                if (thePodcast == podcastToDelete) {
                                    searchResult.put("message", userOfPodcast.getName() + " can't delete this podcast.");
                                    outputs.add(searchResult);
                                    return;
                                }
                            }
                        }
                    }
                    library.getPodcasts().remove(podcastToDelete);
                    userOfPodcast.getPodcasts().remove(podcastToDelete);
                    searchResult.put("message", userOfPodcast.getName() + " deleted the podcast successfully.");
                    outputs.add(searchResult);

                } else {
                    searchResult.put("message", this.getUser() + " doesn't have a podcast with the given name.");
                    outputs.add(searchResult);
                }
            } else {
                searchResult.put("message", this.getUser() + " is not a host."); //is not an artist?
                outputs.add(searchResult);
            }
        }
        else {
            searchResult.put("message", "The username " + this.getUser() + " doesn't exist.");
            outputs.add(searchResult);
        }
    }

}
