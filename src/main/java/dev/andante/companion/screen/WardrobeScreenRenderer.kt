package dev.andante.companion.screen

import dev.andante.companion.helper.lore
import dev.andante.companion.item.CustomItemKeys
import dev.andante.companion.item.CustomItemManager
import dev.andante.companion.regex.RegexKeys
import dev.andante.companion.regex.RegexManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

/**
 * Manages rendering of the wardrobe screen.
 */
object WardrobeScreenRenderer {
    // TODO remove get() when finished
    private val REGULAR_WIDTH get() = 176

    private val PLAYER_RENDER_BACKGROUND_WIDTH get() = 178
    private val PLAYER_RENDER_BACKGROUND_HEIGHT get() = 178
    private val PLAYER_RENDER_SEPARATION get() = 20

    /**
     * The current menu that the player is in. Defaults to hats.
     */
    private var currentMenu: CosmeticMenu = CosmeticMenu.HATS

    /**
     * Called before the player is first rendered on the screen.
     */
    fun preparePlayerRender(screen: HandledScreen<*>) {
        screen.x += ((REGULAR_WIDTH  + PLAYER_RENDER_SEPARATION) / 2)
        screen.y += 18
    }

    /**
     * Renders the client player in the screen.
     */
    fun renderPlayer(client: MinecraftClient, screen: HandledScreen<*>, context: DrawContext) {
        // get current menu
        screen.screenHandler.cursorStack.let { stack ->
            if (stack.isEmpty) {
                return@let
            }

            // check menu
            stack.nbt?.getInt("CustomModelData")?.let { value ->
                CosmeticMenu.CUSTOM_ITEM_MAP_RESOLVED
                    .entries
                    .firstOrNull { it.value == value } // compare values
                    ?.let { (menu, _) -> currentMenu = menu } // set current menu
            }

            // check lore
            val loreStrings = stack.lore.map(Text::getString)
            val loreMatcher = { key: String -> loreStrings.any { string -> RegexManager.matches(key, string) } }

            if (loreMatcher(RegexKeys.ITEM_UNEQUIP)) {
                // unequip slot
                currentMenu.equipmentSlot?.let { slot ->
                    client.player?.equipStack(slot, ItemStack.EMPTY)
                }
            } else if (loreMatcher(RegexKeys.ITEM_EQUIP)) {
                // equip slot
                currentMenu.equipmentSlot?.let { slot ->
                    client.send {
                        client.player?.equipStack(slot, stack)
                    }
                }
            }
        }

        // render
        client.player?.let { player ->
            val x = screen.x - PLAYER_RENDER_SEPARATION - PLAYER_RENDER_BACKGROUND_WIDTH
            val y = screen.y
            context.fill(x, y, x + PLAYER_RENDER_BACKGROUND_WIDTH, y + PLAYER_RENDER_BACKGROUND_HEIGHT, 0x7FFFFFFF)
            InventoryScreen.drawEntity(context, x + (PLAYER_RENDER_BACKGROUND_WIDTH / 2) - 30, y + PLAYER_RENDER_BACKGROUND_HEIGHT - 30, 50, -40f, 0f, player)
        }
    }

    /**
     * Called when any screen is removed.
     */
    fun onScreenRemoved() {
        currentMenu = CosmeticMenu.HATS
    }

    /**
     * A cosmetic menu in the wardrobe screen.
     */
    enum class CosmeticMenu(
        /**
         * The key for [CustomItemManager].
         */
        val customItemKey: String,

        /**
         * The slot that the cosmetics of this menu are rendered on.
         */
        val equipmentSlot: EquipmentSlot? = null
    ) {
        HATS(CustomItemKeys.GUI_WARDROBE_HATS, EquipmentSlot.HEAD),
        ACCESSORIES(CustomItemKeys.GUI_WARDROBE_ACCESSORIES, EquipmentSlot.OFFHAND),
        AURAS(CustomItemKeys.GUI_WARDROBE_AURAS),
        TRAILS(CustomItemKeys.GUI_WARDROBE_TRAILS),
        CLOAKS(CustomItemKeys.GUI_WARDROBE_CLOAKS);

        companion object {
            /**
             * A map of the cosmetic menus to functions that return their custom model data key.
             */
            val CUSTOM_ITEM_MAP = values().associateWith { menu -> { CustomItemManager[menu.customItemKey] ?: 999999 } }

            /**
             * A map of the cosmetic menus to their custom model data key.
             */
            val CUSTOM_ITEM_MAP_RESOLVED = CUSTOM_ITEM_MAP.mapValues { (_, function) -> function() }
        }
    }
}
