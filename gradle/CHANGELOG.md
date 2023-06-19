### **A lot of features are not present yet in 5.x!**
### **It is recommended to use this version alongside version 4.x**

---

Differences to 4.x:

+ **Added Parkour Warrior music**
+ **Added custom _One Minute to MCC_ music to the _To The Dome_ map in _To Get to the Other Side_** (configurable)
+ **Added music speed up for the _Double Time_ modifier in _To Get to the Other Side_** (configurable)
+ **Added settings for disabling specific game music**
* **Game music will now loop**
* **Individual game tracks have been modified to better match the MCC event experience**
  * This means that a lot of tracks have been changed to skip the intro
  * Battle Box in particular has been modified so that the music starts playing as the countdown starts, just like event


+ **Added game instances**
    + Game instances actively track data about the game that is currently being played
    + At the end of a game, a game instance will dump a file of all the information gathered
      + These files can be found at `/companion/game_instances/`
      + Currently, game instance information is only properly set up for _Parkour Warrior: Dojo_ and *To Get to the Other Side*
      + The intention of these files is to provide a quick way for tournament managers to gather information from their participants. These files can be paired with screenshot proof to easier manage tournaments. Programs can be made to process these files en masse to provide statistics and other useful information on a tournament.


* Renamed configuration to settings, and moved files to `/companion/settings/`
* Rewritten the mod in Kotlin
