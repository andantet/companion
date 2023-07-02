### **A lot of features are not present yet in 5.x!**
### **It is recommended to use this version alongside version 4.x, disabling the music configuration in the previous version.**

---

* Fixed certain round status updates not being detected due to a change in Noxcrew's message capitalisation
    * This fixes any music issues!
    * The fix involves a long-term solution so this shouldn't cause as bad of a problem in the future

- **Added Ghosts to Parkour Warrior: Dojo!**
  - Currently accessed via a command
    - `/companion:parkour_warrior_dojo`
        - `ghosts clear`: clears all ghosts from the world
        - `ghosts toggle <fileName> [repeat]`: toggles a ghost in the world
          - `<fileName>`: the name of the run file
          - `[repeat]`: whether to send the ghost back to the beginning of the course when it finishes
        - `runs list`: lists all loaded runs
        - `runs reload`: reloads all runs from disk
  - Runs are stored in your Minecraft folder at `/companion/game_instances/parkour_warrior_dojo/runs/`
    - These files can be renamed to whatever you please and shared to run against other players' ghosts!
  - An interface will be added in the future! I just wanted to get this release out for the music fix!


- Added back 'Close Beta Test Warning' under HUD settings
