CHANGELOG FOR 4.0.0-beta.26:

#### ^ `mccic-music` 0.4.0
* Reworked fade out to be more consistent

+ Fade Transition - transitions the game music when the screen fades between worlds (queuing, leaving, etc)
+ `/mccic:play_current`
+ `/mccic:try_fade_out`

#### ^ `mccic-hud` 0.4.0
+ Auto-Close Beta Test Warning - whether to close the beta test warning automatically on join. Disabled by default.
+ Player Preview in Wardrobe menus

* Renamed the IDs of custom hud configurations (these will reset)

#### ^ `mccic-config` 0.3.4
* Load config before saving (fixes file-modified configurations being discarded on save)

#### ^ `mccic-toasts` 0.5.0
+ Update Announcement - moved from `mccic-api`, now configurable
  + Changed to be more descriptive: `4.0.0-beta.26+1.19.3` -> `4.0.0-beta.26 for MC 1.19.3`

#### ^ `mccic-api` 0.7.0
- Removed update toast (moved to `mccic-toasts`)

* Only retrieve `UnicodeIconsStore` on load
* `ClientLoginSuccessEvent` -> `MCCIClientGameJoinEvent`
* Updated GitHub URLs

+ `ClientHelper#getFromClient` and variants
+ `ClientHelper#getTitle` and variants
+ `ClientHelper#isFading`
+ Updated `UnicodeIconsStore$Icon` support
  + `FADE`
  + `GUI_WARDROBE` + `GUI_WARDROBE_*`
  + `GUI_BETA_TEST_WARNING`
