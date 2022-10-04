package dev.andante.mccic.qol.mixin.client;

import dev.andante.mccic.api.client.game.GameTracker;
import dev.andante.mccic.qol.client.config.ConfirmDisconnectMode;
import dev.andante.mccic.qol.client.config.QolClientConfig;
import dev.andante.mccic.qol.client.disconnect.ConfirmDisconnectScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    private GameMenuScreenMixin(Text title) {
        super(title);
    }

    /**
     * Replace disconnect {@link PressAction} with {@link ConfirmDisconnectScreen} when on MCC: Island.
     */
    @ModifyArg(
        method = "initWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V",
            ordinal = 8
        ),
        index = 5
    )
    private PressAction onDisconnectButtonPressAction(PressAction pressAction) {
        ConfirmDisconnectMode mode = QolClientConfig.getConfig().confirmDisconnectMode();
        if (mode != ConfirmDisconnectMode.OFF) {
            GameTracker tracker = GameTracker.INSTANCE;
            if (tracker.isOnServer() && (mode != ConfirmDisconnectMode.IN_GAME || tracker.isInGame())) {
                return button -> this.client.setScreen(new ConfirmDisconnectScreen(this, pressAction));
            }
        }

        return pressAction;
    }
}
