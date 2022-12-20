CHANGELOG FOR 4.0.0-beta.25:

#### ^ `mccic-api` 0.6.0
+ Moved `MCCICSounds` from `mccic-music`

* Cleaned up `ChatModeTracker#switchToNext`: split up methods, and `switchTo` methods now return boolean for if the mode was switched

#### ^ `mccic-hud` 0.3.0
+ MCCIC Loading Screen: Replaces the loading screen with the MCCI: Companion logo when loading MCC: Island

#### ^ `mccic-key-bindings` 0.1.3
* Made the chat mode toggle key binding more responsive
  * Added a click sound when switching
  * Added an error message for when no other chat modes are available

#### ^ `mccic-music` 0.3.0
+ Added Transition to Overtime: transitions from the game music to the overtime music near the end of a game round

* `MCCICMusicClientImpl#GAME_SOUND_MANAGER` -> `GameSoundManager#INSTANCE`
* `MCCICMusicClientImpl#playHoleInTheWallOtherDeathSound` -> `GameSoundManager`
