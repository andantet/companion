package dev.andante.companion.regex

object RegexKeys {
    /**
     * A regex that matches the title displayed to establish a round.
     */
    const val ROUND_NUMBER_TITLE = "round_number_title"

    /**
     * A regex that matches the message sent to start a game.
     */
    const val GAME_STARTED = "game_started"

    /**
     * A regex that matches the message sent to start a round.
     */
    const val ROUND_STARTED = "round_started"

    /**
     * A regex that matches the message sent to end a round.
     */
    const val ROUND_OVER = "round_over"

    /**
     * A regex that matches the message sent when the player finishes a game.
     */
    const val GAME_FINISHED = "game_finished"

    /**
     * A regex that matches the message sent to end a game.
     */
    const val GAME_OVER = "game_over"

    /**
     * A regex that matches the message sent when the player is facing another team.
     */
    const val FACING_TEAM = "facing_team"

    /**
     * A regex that matches the message sent when the mode changes.
     */
    const val PARKOUR_WARRIOR_DOJO_MODE_CHANGE = "parkour_warrior_dojo_mode_change"

    /**
     * The title text displayed when a challenge mode run starts.
     */
    const val PARKOUR_WARRIOR_DOJO_COURSE_STARTED = "parkour_warrior_dojo_course_started"

    /**
     * A regex that matches the message sent when the player restarts a course.
     */
    const val PARKOUR_WARRIOR_DOJO_COURSE_RESTARTED = "parkour_warrior_dojo_course_restarted"

    /**
     * A regex that matches the title sent when the player enters a section.
     */
    const val PARKOUR_WARRIOR_SECTION_TITLE = "parkour_warrior_section_title"

    /**
     * A regex that matches the title sent when the player gains a medal.
     */
    const val PARKOUR_WARRIOR_DOJO_MEDAL_GAINED = "parkour_warrior_dojo_medal_gained"

    /**
     * A regex that matches the subtitle sent when the player completes a run.
     */
    const val PARKOUR_WARRIOR_DOJO_RUN_COMPLETE_TITLE = "parkour_warrior_dojo_run_complete_title"

    /**
     * The message sent when the player finishes in TGTTOS.
     */
    const val PLAYER_FINISHED = "player_finished"

    /**
     * The message sent when another player finishes in TGTTOS.
     */
    const val OTHER_PLAYER_FINISHED = "other_player_finished"

    /**
     * A regex that matches the map text displayed on the sidebar.
     */
    const val MAP_SIDEBAR = "map_sidebar"

    /**
     * A regex that matches the modifier text displayed on the sidebar.
     */
    const val MODIFIER_SIDEBAR = "modifier_sidebar"

    /**
     * A regex that matches the course text displayed on the sidebar.
     */
    const val PARKOUR_WARRIOR_DOJO_COURSE_SIDEBAR = "parkour_warrior_dojo_course_sidebar"

    /**
     * A regex that matches the daily challenge course text displayed on the sidebar.
     */
    const val PARKOUR_WARRIOR_DOJO_COURSE_SIDEBAR_DAILY_CHALLENGE = "parkour_warrior_dojo_course_sidebar_daily_challenge"

    /**
     * A regex that matches the leap text displayed on the sidebar.
     */
    const val PARKOUR_WARRIOR_SURVIVOR_LEAP_SIDEBAR = "parkour_warrior_survivor_leap_sidebar"

    /**
     * A regex that matches the tooltip displayed to equip items.
     */
    const val ITEM_EQUIP = "item_equip"

    /**
     * A regex that matches the tooltip displayed to unequip items.
     */
    const val ITEM_UNEQUIP = "item_unequip"

    /**
     * A regex that matches the leap started message.
     */
    const val LEAP_STARTED = "leap_started"

    /**
     * A regex that matches the leap section complete message.
     */
    const val LEAP_SECTION_COMPLETE = "leap_section_complete"

    /**
     * A regex that matches the leap complete message.
     */
    const val LEAP_COMPLETE = "leap_complete"

    /**
     * A regex that matches the leap ended message.
     */
    const val LEAP_ENDED = "leap_ended"

    /**
     * A regex that matches the title displayed to establish a leap.
     */
    const val LEAP_NUMBER_TITLE = "leap_number_title"

    /**
     * A regex that matches the message sent when the player wins the showdown.
     * (Parkour Warrior: Survivor)
     */
    const val PLAYER_WON_SHOWDOWN = "player_won_showdown"

    /**
     * A regex that matches the message sent when the player is eliminated with a placement.
     */
    const val PLAYER_ELIMINATED_PLACEMENT = "player_eliminated_placement"

    /**
     * A regex that matches the placements text displayed on the sidebar.
     */
    const val PARKOUR_WARRIOR_SURVIVOR_PLACEMENTS_SIDEBAR = "parkour_warrior_survivor_placements_sidebar"
}
