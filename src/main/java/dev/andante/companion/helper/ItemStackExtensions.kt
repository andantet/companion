package dev.andante.companion.helper

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting

private val LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true)

/**
 * Parses and returns a list of the lore texts stored on this stack.
 */
val ItemStack.lore: List<Text> get() {
    val nbt = getSubNbt(ItemStack.DISPLAY_KEY)
    if (nbt != null && nbt.getType(ItemStack.LORE_KEY) == NbtElement.LIST_TYPE) {
        val nbtList = nbt.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE.toInt())
        return nbtList.map(NbtElement::asString).mapNotNull { string ->
            try {
                val text = Text.Serializer.fromJson(string) ?: return@mapNotNull null
                return@mapNotNull Texts.setStyleIfAbsent(text, LORE_STYLE)
            } catch (exception: Exception) {
                return emptyList()
            }
        }
    }

    return emptyList()
}
