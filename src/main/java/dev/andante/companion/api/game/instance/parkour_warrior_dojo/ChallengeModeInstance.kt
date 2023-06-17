package dev.andante.companion.api.game.instance.parkour_warrior_dojo

/**
 * An instance of Parkour Warrior Dojo challenge mode.
 */
class ChallengeModeInstance : ParkourWarriorDojoModeInstance() {
    override fun onCourseRestart(): Boolean {
        return true
    }
}
