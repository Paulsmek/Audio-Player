# Proiect GlobalWaves

<div align="center"><img src="https://tenor.com/view/catjam-cat-jam-jamming-cute-gif-1340929034057342144.gif" width="350px"></div>

## Code Structure

* src/
  * checker/ - checker files
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
      * commandProcessor - class that can read all the commands and prepares them for execution
      	* CommandsPlayer - package for all commands related to "Comenzi player"
      	  * AddRemoveInPlaylist - class that adds or removes songs in a playlist, while checkign if it exists in it.
      	  * Backward - class that adds 90 sec to the remaining time of the thing that is playing. If the time was not enough to do this, call prev function to handle moving to the 
      previous song/episode. 
      	  * Forward - class that substracts 90 sec from the remaining time of the thing that is playing. It calls next in case of not enough time left.
      	  * Like - class that adds a song in the list of liked songs of the respective user, and removes it if it is allready liked/ is in the list
      	  * Load - class that handles load. It adds the selected thing in the Playband (AdvancedUserStats) and also in NowPlaying.
      	  * Next - class that adds the next thing in NowPlaying
      	  * PlayPause - class that pauses/unpauses what is currently playing
      	  * Prev - class that adds the previous thing in NowPlaying
      	  * Repeat - class that handles all the repeat states of all cases. It modifies the stats of Playband
      	  * Shuffle - class that handles all the shuffle cases and modifies the stats of Playband
      	  * Status - class that shows the stats in Playband
      	* commandsPlaylist - package for all commands related to "Comenzi Playlists"
      	  * Follow - class that adds a platlist in the follow list of a user
      	  * Playlist - class that defines a playlist and adds to outputs the proper JSON
      	  * ShowPlaylists - class that shows all the playlists
      	  * SwitchVisibility - class that switches the visibility of a playlist of a user
      	* commandsPlaylist - package for all commands related to "Search Bar"
      	  * Search - class that handles searching and all cases of it
      	  * Select - class that adds the certain search result in the Playband. (dont confuse with load)
      	* generalStats - package for all commands related to "Statistici generale"
      	  * GetTop5Playlists - class that finds the top 5 playlist using all the lists of followed playlists in all users
      	  * GetTop5Songs - class that finds the top 5 songs using all the lists of liked songs in all users
      	  * SongAndLikes - class that defines a song with the number of likes it has, in order to help me with the top 5 songs
      	* advancedUserStats - package that helps me keep track of things a user does while running commands
      	  * Playband - class that knows what is currently playing and keeps track of time left in certain areas, and also all the stats
      	  * NowPlaying - class that defines what is currently playing in my Playband
      	  * PodcastSave - class that defines a type of podcast that remembers the last thing about a respective podcast that was once loaded
      	  * SelectType - enumeration that tells me if i have a song, playlist or podcast. It is used to remember what type I`ve selected or loaded
      	  * Stats - class that defines stats for Playband
      	  * UserStats - class that defines all the stats that a advanced user can have
      	  * Database - class that contains Singleton pattern . It contains an array of advanced users
      	  * CommandSkell - class that handles username, time and command memoration and methodes that passes it to others and handles offline output
      	* userStatistics - package for all commands releated to "Statistici utilizatori"
      	  * ShowPrefferedSongs - class that adds to outputs all liked songs that a user has
      	  
      	  <div align="center"><img src="https://tenor.com/view/baby-gif-22495021.gif" width="350px"></div>
      	  
       NEW (other that some modified classes in etapa 1 code):
      	* commandNormalUser - package for the only command that a normal user can do
	  * SwitchConnectionStatus - class that handles switching status from online-offline and vice-versa
	* commandsAdmin - package for all commands related to "Comenzi Admin"
	  * AddUser - class that adds a user in database
	  * DeleteUser - class that removes a user from database and all traces of it only in case it has no interaction with other users
	  * ShowAlbums - class that shows all albums of an artist 
	  * ShowPodcast - class that shows all podcast belonging to a host user
	* commandsArtist - package for all commands related to "Comenzi Artist"
	  * AddAlbum - class that adds an album to an artist if the specified user is one
	  * RemoveAlbum
	  * AddEvent - class that adds an event to an artist if the specified user is one 
	  * RemoveEvent
	  * AddMerch - class that adds merch to an artist if the specified user is one
	  * Album & * Event - classes that define what and album/event is
	* commandsHost - package for all commands related to "Comenzi Host"
	  * AddPodcast - class that adds a podcast to a host if the specified user is one
	  * RemovePodcast 
	  * AddAnnouncement - class that adds an announcement to a host if the specified user is one 
	  * RemoveAnnouncement
	  * Announcement - class that defines what and announcement is
	* commandsPage - package for all commands related to "Comenzi pentru sistemul de pagini"
   	  * ChangePage - class that handles changing the page of a user, either to home or likedcontent
   	  * PrintCurrentPage - class that prints the proper page with all its requirements
   	* Added to generalStats :
   	  * AlbumAndLikes & ArtistAndLikes - classes that define an album/artist with the number of likes it has, in order to help me with top5Artists/top5Albums
   	  * GetAllUsers - class that sends to output all users
   	  * GetOnlineUsers - class that sends to output all online users
   	  * GetTop5Albums & * GetTop5Artists - classes that show the most liked album/artists at the current time of the command

