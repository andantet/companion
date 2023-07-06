package dev.andante.companion.api.setting

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Settings regarding the HUD.
 */
data class HudSettings(
    /**
     * Whether to close the beta test warning on join.
     */
    val closeBetaTestWarning: Boolean,

    /**
     * Whether to render the player in the wardrobe menus.
     */
    val renderPlayerInWardrobe: Boolean
) {
    companion object {
        /**
         * The default settings.
         */
        private val DEFAULT = HudSettings(
            closeBetaTestWarning = false,
            renderPlayerInWardrobe = true,
        )

        /**
         * The codec for serializing these settings.
         */
        val CODEC: Codec<HudSettings> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.fieldOf("close_beta_test_warning")
                    .orElse(DEFAULT.closeBetaTestWarning)
                    .forGetter(HudSettings::closeBetaTestWarning),
                Codec.BOOL.fieldOf("render_player_in_wardrobe")
                    .orElse(DEFAULT.renderPlayerInWardrobe)
                    .forGetter(HudSettings::renderPlayerInWardrobe)
            ).apply(instance, ::HudSettings)
        }

        /**
         * An container for these settings.
         */
        val CONTAINER = SettingsContainer("hud", CODEC, DEFAULT)

        /**
         * The instance of these settings.
         */
        val INSTANCE get() = CONTAINER.serializableObject
    }
}
