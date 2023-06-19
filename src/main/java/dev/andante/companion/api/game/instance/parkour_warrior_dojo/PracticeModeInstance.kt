package dev.andante.companion.api.game.instance.parkour_warrior_dojo

import dev.andante.companion.setting.MusicSettings

/**
 * An instance of Parkour Warrior Dojo practice mode.
 */
class PracticeModeInstance : ParkourWarriorDojoModeInstance(
    { MusicSettings.INSTANCE.parkourWarriorDojoPracticeModeMusic }
)
