package dev.andante.companion.test

import dev.andante.companion.game.GameTracker
import dev.andante.companion.server.ServerTracker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text

object CompanionTest : ClientModInitializer {
    override fun onInitializeClient() {
        // debug hud
        HudRenderCallback.EVENT.register(::renderHud)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderHud(context: DrawContext, tickDelta: Float) {
        if (!ServerTracker.isConnectedToMccIsland || !FabricLoader.getInstance().isDevelopmentEnvironment) {
            return
        }

        // render game instance debug hud
        GameTracker.gameInstance?.let { instance ->
            val textRenderer = MinecraftClient.getInstance().textRenderer

            var largestWidth = 0
            val renderBuffer = mutableListOf<Text>()

            // setup consumer for pass-offs
            val textRendererConsumer: (Text) -> Unit = { text ->
                // add to render buffer
                renderBuffer.add(text)

                // update largest width
                val width = textRenderer.getWidth(text)
                if (width > largestWidth) {
                    largestWidth = width
                }
            }

            // add type
            textRendererConsumer(Text.literal(instance.type.toString()))
            textRendererConsumer(Text.literal(instance::class.simpleName))
            textRendererConsumer(Text.literal(instance.uuid.toString()))

            // pass off to instance
            instance.renderDebugHud(textRendererConsumer)

            // render text
            val border = 2

            val textHeightConsumer = { i: Int -> (textRenderer.fontHeight + 1) * i }
            val textHeight = textHeightConsumer(renderBuffer.size)

            val backgroundWidth = largestWidth + 1 + border
            val backgroundHeight = textHeight + border

            val startX = 10
            val startY = context.scaledWindowHeight / 2 - textHeight / 2

            // render background

            val backgroundX = startX - border
            val backgroundY = startY - border

            context.fill(
                RenderLayer.getGuiOverlay(),
                backgroundX, backgroundY,
                backgroundX + backgroundWidth, backgroundY + backgroundHeight,
                0x7C000000
            )

            // render text from buffer
            var i = 0
            renderBuffer.forEach { text ->
                context.drawTextWithShadow(textRenderer, text, startX, startY + textHeightConsumer(i), 0xFFFFFF)
                i++
            }
        }
    }
}
