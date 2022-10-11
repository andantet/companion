package dev.andante.mccic.api.mixin.client;

import dev.andante.mccic.api.client.event.MCCIClientScreenServerJoinEvent;
import dev.andante.mccic.api.client.game.GameTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V", at = @At("TAIL"))
    private static void onConnect(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        if (GameTracker.INSTANCE.isOnServer()) {
            MCCIClientScreenServerJoinEvent.EVENT.invoker().onClientScreenServerJoin(screen, client, address, info);
        }
    }
}
