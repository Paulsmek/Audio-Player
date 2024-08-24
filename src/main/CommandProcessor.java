package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;
import main.advancedUserStats.CommandSkell;
import main.advancedUserStats.SelectType;
import main.commandNormalUser.SwitchConnectionStatus;
import main.commandsAdmin.AddUser;
import main.commandsAdmin.DeleteUser;
import main.commandsAdmin.ShowAlbums;
import main.commandsAdmin.ShowPodcasts;
import main.commandsArtist.*;
import main.commandsHost.AddAnnouncement;
import main.commandsHost.AddPodcast;
import main.commandsHost.RemoveAnnouncement;
import main.commandsHost.RemovePodcast;
import main.commandsPage.ChangePage;
import main.commandsPage.PrintCurrentPage;
import main.commandsPlayer.*;
import main.commandsSearchBar.Search;
import main.commandsSearchBar.Select;
import main.commandsPlayList.Follow;
import main.commandsPlayList.Playlist;
import main.commandsPlayList.ShowPlaylists;
import main.commandsPlayList.SwitchVisibility;
import main.generalStats.*;
import main.advancedUserStats.Database;
import main.advancedUserStats.UserStats;
import main.userStatistics.ShowPrefferedSongs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CommandProcessor {
    private final ObjectMapper objectMapper;
    public CommandProcessor() {
        this.objectMapper = new ObjectMapper();
    }
    public void processCommands(String filePath, LibraryInput library, ArrayNode outputs) {
        try {
            File commandsFile = new File("input/" + filePath);
            JsonNode commandsNode = objectMapper.readTree(commandsFile);
            Database database = Database.getInstance(library.getUsers(), true);
            for (JsonNode commandNode : commandsNode) {
                JsonNode commandField = commandNode.get("command");
                if (commandField != null) {
                    String commandType = commandField.asText();
                    CommandSkell comm = new CommandSkell();
                    comm.setCommand(commandType);
                    JsonNode username = commandNode.get("username");
                    JsonNode time = commandNode.get("timestamp");
                    if (time != null) comm.setTimestamp(time.asInt());
                    if (username != null) comm.setUser(username.asText());
                    for (UserStats user : database.getListaRezUsers()) {
                        if (user.getLoadType() != null && user.isOnlineStatus()) {
                            user.getUserPlayband().EditRemainTime(time.asInt(), database, user.getName(), library);
                        }
                    }
                    switch (commandType) {
                        case "search":
                            Search searchNode = new Search();
                            searchNode.MoveData(comm);
                            if (database.findByName(searchNode.getUser()).isOnlineStatus()) {
                                String itemType = commandNode.get("type").asText();
                                switch (itemType) {
                                    case "song":
                                        searchNode.getSongs(commandNode, library);
                                        searchNode.addSongToOut(outputs, database);
                                        break;

                                    case "playlist":
                                        searchNode.getPlaylists(commandNode, library, database);
                                        searchNode.addPlaylistToOut(outputs, database);
                                        break;

                                    case "podcast":
                                        searchNode.getPodcast(commandNode, library);
                                        searchNode.addPodcastToOut(outputs, database);
                                        break;

                                    case "artist":
                                        searchNode.getArtists(commandNode, database);
                                        searchNode.addArtistsToOut(outputs, database);
                                        break;

                                    case "host":
                                        searchNode.getHosts(commandNode, database);
                                        searchNode.addHostsToOut(outputs, database);
                                        break;

                                    case "album":
                                        searchNode.getAlbums(commandNode, database);
                                        searchNode.addAlbumsToOut(outputs, database);
                                        break;

                                    default:
                                }
                            } else {
                                searchNode.DoOffline(outputs);
                            }
                            break;

                        case "select":
                            Select selectNode = new Select();
                            selectNode.MoveData(comm);
                            JsonNode index = commandNode.get("itemNumber");
                            if (index != null) selectNode.setIndex(index.asInt());
                            if (database.findByName(selectNode.getUser()).isOnlineStatus()) {
                                if (database.findByName(selectNode.getUser()).getSearchResults() != null) {
                                    selectNode.select(database, outputs, 1);
                                } else {
                                    selectNode.select(database, outputs, 2);
                                }
                            } else {
                                selectNode.DoOffline(outputs);
                            }
                            break;

                        case "load":
                            Load loadNode = new Load();
                            loadNode.MoveData(comm);
                            if (database.findByName(loadNode.getUser()).getSelectType() != null) {
                                loadNode.load(outputs, database, 1, library);
                            } else {
                                loadNode.load(outputs, database, 2, library);
                            }
                            break;

                        case "status":
                            Status statusNode = new Status();
                            statusNode.MoveData(comm);
                            statusNode.addStatusToOut(outputs, database);
                            break;

                        case "playPause":
                            PlayPause playPauseNode = new PlayPause();
                            playPauseNode.MoveData(comm);
                            playPauseNode.addPlayPauseToOut(outputs, database);
                            break;

                        case "createPlaylist":
                            Playlist playListNode = new Playlist();
                            playListNode.MoveData(comm);
                            JsonNode playListName = commandNode.get("playlistName");
                            if (playListName != null) playListNode.setPlayListName(playListName.asText());
                            if (!database.findByName(playListNode.getUser()).PlaylistExists(playListNode.getPlayListName())) {
                                database.findByName(playListNode.getUser()).getUserPlaylists().add(playListNode);
                                playListNode.addPlaylistToOut(outputs, database, 1);
                            } else {
                                playListNode.addPlaylistToOut(outputs, database, 2);
                            }
                            break;

                        case "addRemoveInPlaylist":
                            AddRemoveInPlaylist addRemoveNode = new AddRemoveInPlaylist();
                            addRemoveNode.MoveData(comm);
                            JsonNode Id = commandNode.get("playlistId");
                            if (Id != null) addRemoveNode.setPlaylistId(Id.asInt());
                            addRemoveNode.addRemove(outputs, database, library);
                            break;

                        case "like":
                            Like likeNode = new Like();
                            likeNode.MoveData(comm);
                            if (database.findByName(likeNode.getUser()).isOnlineStatus()) {
                                likeNode.LikeUnlike(outputs, database, library);
                            } else {
                                likeNode.DoOffline(outputs);
                            }
                            break;

                        case "showPlaylists":
                            ShowPlaylists showPlaylistNode = new ShowPlaylists();
                            showPlaylistNode.MoveData(comm);
                            showPlaylistNode.showPlaylists(outputs, database);
                            break;

                        case "showPreferredSongs":
                            ShowPrefferedSongs showPrefSongs = new ShowPrefferedSongs();
                            showPrefSongs.MoveData(comm);
                            showPrefSongs.showSongs(outputs, database);
                            break;

                        case "repeat":
                            Repeat repeatNode = new Repeat();
                            repeatNode.MoveData(comm);
                            repeatNode.DoRepeat(outputs, database);
                            break;

                        case "shuffle":
                            Shuffle shuffleNode = new Shuffle();
                            shuffleNode.MoveData(comm);
                            JsonNode shuffle = commandNode.get("seed");
                            if (shuffle != null) shuffleNode.setSeed(shuffle.asInt());
                            String usernameField12 = shuffleNode.getUser();
                            if (database.findByName(usernameField12).getLoadType() != null) {
                                if (!database.findByName(usernameField12).getUserPlayband().getPlayStats().isShuffle()) {
                                    if (database.findByName(usernameField12).getLoadType().equals(SelectType.Playlists)) {
                                        database.findByName(usernameField12).getUserPlayband().getPlayStats().setShuffledOrder(shuffleNode.DoTheShuffleP(database));
                                    } else {
                                        database.findByName(usernameField12).getUserPlayband().getPlayStats().setShuffledOrder(shuffleNode.DoTheShuffleA(database));
                                    }
                                } else {
                                    UserStats user = database.findByName(shuffleNode.getUser());
                                    user.getUserPlayband().getPlayStats().setRemainedTime(user.getPlaylistDuration(user.getUserPlayband().getLoadedPlaylist()));
                                    int lastIdx = user.getUserPlayband().getLastIndex();
                                    int idx = user.getUserPlayband().getPlayStats().getShuffledOrder().get(lastIdx);
                                    user.getUserPlayband().setLastIndex(idx);
                                    database.findByName(usernameField12).getUserPlayband().getPlayStats().setShuffledOrder(new ArrayList<>());
                                }
                            }
                            shuffleNode.ShuffleToOut(outputs, database);
                            break;

                        case "next":
                            Next nextNode = new Next();
                            nextNode.MoveData(comm);
                            nextNode.doNext(outputs, database, library, 1);
                            break;

                        case "prev":
                            Prev prevNode = new Prev();
                            prevNode.MoveData(comm);
                            prevNode.doPrev(outputs, database, library, 1);
                            break;

                        case "forward":
                            Forward forwardNode = new Forward();
                            forwardNode.MoveData(comm);
                            forwardNode.doForward(outputs, database, library);
                            break;

                        case "backward":
                            Backward backwardNode = new Backward();
                            backwardNode.MoveData(comm);
                            backwardNode.doBackward(outputs, database, library);
                            break;

                        case "follow":
                            Follow followNode = new Follow();
                            followNode.MoveData(comm);
                            followNode.doFollow(outputs, database, library);
                            break;

                        case "switchVisibility":
                            SwitchVisibility switchNode = new SwitchVisibility();
                            switchNode.MoveData(comm);
                            JsonNode Id2 = commandNode.get("playlistId");
                            if (Id2 != null) switchNode.setId(Id2.asInt());
                            switchNode.doSwitch(outputs, database);
                            break;

                        case "getTop5Playlists":
                            GetTop5Playlists top5PlaylistsNode = new GetTop5Playlists();
                            top5PlaylistsNode.MoveData(comm);
                            top5PlaylistsNode.doTop5Playlists(outputs, database);
                            break;

                        case "getTop5Songs":
                            GetTop5Songs top5SongsNode = new GetTop5Songs();
                            top5SongsNode.MoveData(comm);
                            top5SongsNode.doTop5Songs(outputs, database, library);
                            break;

                        case "switchConnectionStatus":
                            SwitchConnectionStatus conNode = new SwitchConnectionStatus();
                            conNode.MoveData(comm);
                            conNode.SwitchCon(outputs, database);
                            break;

                        case "getOnlineUsers":
                            GetOnlineUsers onlineNode = new GetOnlineUsers();
                            onlineNode.MoveData(comm);
                            onlineNode.Online(outputs, database);
                            break;

                        case "addUser":
                            AddUser addNode = new AddUser();
                            addNode.MoveData(comm);
                            JsonNode type = commandNode.get("type");
                            if (type != null) addNode.setType(type.asText());
                            JsonNode age = commandNode.get("age");
                            if (age != null) addNode.setAge(age.asInt());
                            JsonNode city = commandNode.get("city");
                            if (city != null) addNode.setCity(city.asText());
                            addNode.DoAdd(outputs, database, library);
                            break;

                        case "addAlbum":
                            AddAlbum addAlbumNode = new AddAlbum();
                            addAlbumNode.MoveData(comm);
                            JsonNode name24 = commandNode.get("name");
                            if (name24 != null) addAlbumNode.setName(name24.asText());
                            JsonNode releaseYear = commandNode.get("releaseYear");
                            if (releaseYear != null) addAlbumNode.setReleaseYear(releaseYear.asInt());
                            JsonNode description24 = commandNode.get("description");
                            if (description24 != null) addAlbumNode.setDescription(description24.asText());
                            addAlbumNode.DoAddAlbum(outputs, database, library, commandNode);
                            break;

                        case "showAlbums":
                            ShowAlbums showAlbumsNode = new ShowAlbums();
                            showAlbumsNode.MoveData(comm);
                            showAlbumsNode.Show(outputs, database);
                            break;

                        case "printCurrentPage":
                            PrintCurrentPage printNode = new PrintCurrentPage();
                            printNode.MoveData(comm);
                            if (database.findByName(printNode.getUser()).isOnlineStatus()) {
                                printNode.PrintPage(outputs, database);
                            } else {
                                printNode.DoOffline(outputs);
                            }
                            break;

                        case "addEvent":
                            AddEvent eventNode = new AddEvent();
                            eventNode.MoveData(comm);
                            JsonNode name27 = commandNode.get("name");
                            if (name27 != null) eventNode.setName(name27.asText());
                            JsonNode description27 = commandNode.get("description");
                            if (description27 != null) eventNode.setDescription(description27.asText());
                            JsonNode date27 = commandNode.get("date");
                            if (date27 != null) eventNode.setDate(date27.asText());
                            eventNode.DoAddEvent(outputs, database);
                            break;

                        case "addMerch":
                            AddMerch merchNode = new AddMerch();
                            merchNode.MoveData(comm);
                            JsonNode name28 = commandNode.get("name");
                            if (name28 != null) merchNode.setName(name28.asText());
                            JsonNode description28 = commandNode.get("description");
                            if (description28 != null) merchNode.setDescription(description28.asText());
                            JsonNode price27 = commandNode.get("price");
                            if (price27 != null) merchNode.setPrice(price27.asInt());
                            merchNode.DoAddMerch(outputs, database);
                            break;

                        case "getAllUsers":
                            GetAllUsers allUsersNode = new GetAllUsers();
                            allUsersNode.MoveData(comm);
                            allUsersNode.PrintUsers(outputs, database);
                            break;

                        case "deleteUser":
                            DeleteUser deleteUserNode = new DeleteUser();
                            deleteUserNode.MoveData(comm);
                            deleteUserNode.DoDelete(outputs, database, library);
                            break;

                        case "addPodcast":
                            AddPodcast addPodcastNode = new AddPodcast();
                            addPodcastNode.MoveData(comm);
                            JsonNode name31 = commandNode.get("name");
                            if (name31 != null) addPodcastNode.setName(name31.asText());
                            addPodcastNode.doAddPodcast(outputs, database, library, commandNode);
                            break;

                        case "addAnnouncement":
                            AddAnnouncement annNode = new AddAnnouncement();
                            annNode.MoveData(comm);
                            JsonNode name32 = commandNode.get("name");
                            if (name32 != null) annNode.setName(name32.asText());
                            JsonNode description32 = commandNode.get("description");
                            if (description32 != null) annNode.setDescription(description32.asText());
                            annNode.DoAddAnnouncement(outputs, database);
                            break;

                        case "removeAnnouncement":
                            RemoveAnnouncement rannNode = new RemoveAnnouncement();
                            rannNode.MoveData(comm);
                            JsonNode name33 = commandNode.get("name");
                            if (name33 != null) rannNode.setName(name33.asText());
                            rannNode.DoRemove(outputs, database);
                            break;

                        case "showPodcasts":
                            ShowPodcasts showPodcastsNode = new ShowPodcasts();
                            showPodcastsNode.MoveData(comm);
                            showPodcastsNode.DoShow(outputs, database);
                            break;

                        case "removeAlbum":
                            RemoveAlbum removeAlbumNode = new RemoveAlbum();
                            removeAlbumNode.MoveData(comm);
                            JsonNode name35 = commandNode.get("name");
                            if (name35 != null) removeAlbumNode.setName(name35.asText());
                            removeAlbumNode.DoRemove(outputs, database, library);
                            break;

                        case "changePage":
                            ChangePage changePageNode = new ChangePage();
                            changePageNode.MoveData(comm);
                            JsonNode next36 = commandNode.get("nextPage");
                            if (next36 != null) changePageNode.setNextPage(next36.asText());
                            changePageNode.DoChange(outputs, database);
                            break;

                        case "removePodcast":
                            RemovePodcast removePodcastNode = new RemovePodcast();
                            removePodcastNode.MoveData(comm);
                            JsonNode name37 = commandNode.get("name");
                            if (name37 != null) removePodcastNode.setName(name37.asText());
                            removePodcastNode.DoRemove(outputs, database, library);
                            break;

                        case "removeEvent":
                            RemoveEvent removeEventNode = new RemoveEvent();
                            removeEventNode.MoveData(comm);
                            JsonNode name38 = commandNode.get("name");
                            if (name38 != null) removeEventNode.setName(name38.asText());
                            removeEventNode.DoRemove(outputs, database);
                            break;

                        case "getTop5Albums":
                            GetTop5Albums top5AlbumsNode = new GetTop5Albums();
                            top5AlbumsNode.MoveData(comm);
                            top5AlbumsNode.doTop5Albums(outputs, database);
                            break;

                        case "getTop5Artists":
                            GetTop5Artists top5ArtistsNode = new GetTop5Artists();
                            top5ArtistsNode.MoveData(comm);
                            top5ArtistsNode.doTop5Artists(outputs, database);
                            break;
                        default:
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
