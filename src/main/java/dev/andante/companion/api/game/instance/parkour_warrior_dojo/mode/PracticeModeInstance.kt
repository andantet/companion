package dev.andante.companion.api.game.instance.parkour_warrior_dojo.mode

import dev.andante.companion.setting.MusicSettings

/**
 * An instance of Parkour Warrior Dojo practice mode.
 */
class PracticeModeInstance : DojoModeInstance(
    { MusicSettings.INSTANCE.parkourWarriorDojoPracticeModeMusic }
)
