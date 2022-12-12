CHANGELOG FOR 4.0.0-beta.22:

* Update to 1.19.3 (now is the working release)
+ Hungarian Translations (@kispintyi)

#### ^ `mccic-api` 0.5.0
+ `ChatModeTracker` - see javadoc
+ `PartyTracker` - see javadoc

* `UnicodeIconsStore$Icon` - support for local/party/team chat, and party leader crown. Removed support for achievement and infinibag.
* `GameTracker` - fixed game start triggers (they removed the round start message?!)
* Fixed American Date Format pack
* `MCCIClientLoginHelloEvent` -> `ClientLoginSuccessEvent`

+ Scoreboard utilities in `ClientHelper`
+ `Game#hasTeamChat`
+ `ChatMode`
+ `TextQuery#USERNAME_REGEX`

- `MCCIClientDeathScreenEvent` - didn't work, not worth it

#### ^ `mccic-chat` 0.2.1

#### ^ `mccic-config` 0.3.0
+ `ConfigCodecBuilder` - cleans up config record creation in all modules

#### ^ `mccic-debug` 0.3.0
+ `/mccic-debug:chat_raw_action_bar`
+ `/mccic-debug:chat_unicodes`
+ `/mccic-debug:chat_sidebar_names`
+ `/mccic-debug:chat_party_instance`

+ `unpin_server_resource_packs`

#### ^ `mccic-discord-rp` 0.2.3

#### ^ `mccic-hud` 0.2.3

#### + `mccic-key-bindings` 0.1.0 - Adds key bindings for various MCC: Island functionality.
+ 'Toggle Chat Mode' key binding - toggles between local, party, team chats. Certain chats are only available and cycled through in their relevant contexts.

#### ^ `mccic-music` 0.2.3
+ Stop Music On Death config
+ Stop Music On Chicken Hit config

* Code cleanup

#### ^ `mccic-qol` 0.3.0
+ `/party-mccic`
  + `/party-mccic kickoffline` - kicks offline party members

#### ^ `mccic-toasts` 0.4.0
- Removed Achievements
