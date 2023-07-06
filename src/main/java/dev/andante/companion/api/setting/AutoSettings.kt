package dev.andante.companion.api.setting

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.andante.companion.api.game.round.RoundManager

/**
 * Settings regarding automatic functionality.
 */
data class AutoSettings(
    /**
     * Settings for the auto-glhf message.
     */
    val autoGlhf: AutoMessageSettings,

    /**
     * Settings for the auto-gg message.
     */
    val autoGg: AutoMessageSettings,
) {
    /**
     * All auto message settings.
     */
    val allAutoMessageSettings = listOf(autoGlhf, autoGg)

    companion object {
        /**
         * The default settings.
         */
        private val DEFAULT = AutoSettings(
            autoGlhf = AutoMessageSettings(
                enabled = false,
                message = "glhf!",
                roundStateToSend = RoundManager.State.INITIALIZED
            ),
            autoGg = AutoMessageSettings(
                enabled = false,
                message = "gg!",
                roundStateToSend = RoundManager.State.FINISHED
            ),
        )

        /**
         * The codec for serializing these settings.
         */
        val CODEC: Codec<AutoSettings> = RecordCodecBuilder.create { instance ->
            instance.group(
                AutoMessageSettings.CODEC.fieldOf("auto_glhf")
                    .orElse(DEFAULT.autoGlhf)
                    .forGetter(AutoSettings::autoGlhf),
                AutoMessageSettings.CODEC.fieldOf("auto_gg")
                    .orElse(DEFAULT.autoGg)
                    .forGetter(AutoSettings::autoGg),
            ).apply(instance, ::AutoSettings)
        }

        /**
         * An container for these settings.
         */
        val CONTAINER = SettingsContainer("auto", CODEC, DEFAULT)

        /**
         * The instance of these settings.
         */
        val INSTANCE get() = CONTAINER.serializableObject
    }

    data class AutoMessageSettings(
        /**
         * Whether the entire message is enabled.
         */
        val enabled: Boolean,

        /**
         * The message to send.
         */
        val message: String,

        /**
         * The game round state to send the message at.
         */
        val roundStateToSend: RoundManager.State
    ) {
        companion object {
            /**
             * The codec for these settings.
             */
            val CODEC: Codec<AutoMessageSettings> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("enabled")
                        .forGetter(AutoMessageSettings::enabled),
                    Codec.STRING.fieldOf("message")
                        .forGetter(AutoMessageSettings::message),
                    RoundManager.State.CODEC.fieldOf("round_state_to_send")
                        .forGetter(AutoMessageSettings::roundStateToSend),
                ).apply(instance, ::AutoMessageSettings)
            }
        }
    }
}
