CHANGELOG FOR 4.0.0-beta.20:

* Updated URLs in Mod Menu

#### + `mccic-chat` 0.1.0
+ Chat mentions: highlights your name in chat!

#### ^ `mccic-hud` 0.2.1
* Tweaked borders and element separators

#### ^ `mccic-music` 0.2.1
* Game Music Volume, default change: `1.0` -> `0.5`
* Game Music Volume (After Death), default change: `0.3` -> `0.25`
* Fixed the Hole in the Wall Death Sound preview playing in so many stupid places for whatever reason

#### ^ `mccic-api` 0.3.0
+ `GameRegistry`: moved games from an enum to a registry, which allows for game-specific classes and more game-specific functionality in the future
+ `TextQuery#findTexts`: finds every text matching a predicate within a given text object and its siblings

* `TextQuery#findText(Text, Predicate<Text>)`: added a more abstract version of `findText`, the regex overloads still exist but just point to the predicate overload
* Separated `MCCI: Companion (API)` as a child of `MCCI: Companion` in Mod Menu

#### ^ `mccic-debug` 0.2.1
+ 'Display Name Suffix' debug option: adds `(DN)` to the end of anything that uses `PlayerEntity#getDisplayName`
