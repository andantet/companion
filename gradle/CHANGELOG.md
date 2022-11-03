CHANGELOG FOR 4.0.0-beta.19:

* Added Discord link to Mod Menu
* More config tooltips!

#### Changes in `mccic-hud` **!!! NEW !!!**
+ *First iteration* of a custom HUD system
+ Can be enabled to disable the 'vanilla' hud and activate a custom-rendered HUD that emulates the vanilla feeling
+ Currently has the ability to display *the game time and queue status*; game-specific information is not present as of yet
+ Hud elements can be positioned *at the top of the screen or to the left*

![hud on the left](https://media.discordapp.net/attachments/680748717644578880/1037872569082191932/image.png?width=400&height=200)

#### Changes in `mccic-music`
+ Configuring 'Hole in the Wall Death Sound' now previews the sound
* Score/Score Acquired HITW Death Sounds no longer vary pitch

#### Changes in `mccic-discord-rp`
+ More configs!
  + Display Game
  + Display Game Time
  + Display Game State
  + Display Queue Status

#### Changes in `mccic-api`
+ `QueueTracker`: current queue data

#### Changes in `mccic-debug`
* New debug HUD! Now fancier!

#### Technical Changes
* Flattened `GameTracker`
* Refactored a considerable amount of code for readability and expandability
  * Configuration
  * Discord RPC
  * `EnumOption` 🥰
  * `ClientHelper#getBossBars` 🥰
  * `TextQuery`
* Fixed some warnings where `remap=false` should be present
+ Added the ability to disable the server resource pack (testmod only, jank af)
