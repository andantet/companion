package dev.andante.companion.api.regex

object RegexKeys {
    const val ROUND_NUMBER_TITLE = "round_number_title"
    const val GAME_STARTED = "game_started"
    const val ROUND_STARTED = "round_started"
    const val ROUND_OVER = "round_over"
    const val GAME_FINISHED = "game_finished"
    const val GAME_OVER = "game_over"
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
    const val PARKOUR_WARRIOR_DOJO_SECTION_TITLE = "parkour_warrior_dojo_section_title"

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
}
