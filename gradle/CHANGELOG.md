CHANGELOG FOR 4.0.0-beta.18:

#### Changes in `mccic-qol`
+ Glowing config: `DEFAULT`, vanilla glowing behaviour; `DISABLED`, glowing is disabled; `DISABLED_FOR_PLAYERS`, glowing is disabled for players only

#### Changes in `mccic-music`
* Fixed game music volume settings not updating in real time
* Renamed game music volume settings, these settings will reset

#### Changes in `mccic-toasts`
+ Custom toast texture for event announcements

#### Changes in `mccic-api`
+ Custom toast texture for updates

* Updated 'American Date Format' pack icon
* Made `POST_ROUND_SELF` not count as ending a game

#### Changes in `mccic-config`
* Fixed button ordering in config screens (tabbing works correctly now)

#### Technical Changes
* Automated mod menu config screen registering
* Unicode icons in messages parsing rework
* Depluralised toast texture names
* Vastly improved the code for detecting social toasts
* `MCCIC#createModId`
* Renamed `MusicTracker` -> `GameSoundManager`
* Removed `mccic-social`
* Moved the majority of toasts to `mccic-toasts`
* Moved `AdaptableToastIcon`: `mccic-toasts` -> `mccic-api`
* Moved the 'American Date Format' resource pack: `mccic-qol` -> `mccic-api`
