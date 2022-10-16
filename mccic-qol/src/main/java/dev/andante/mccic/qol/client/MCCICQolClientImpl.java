package dev.andante.mccic.qol.client;

import dev.andante.mccic.api.client.event.MCCIClientLoginHelloEvent;
import dev.andante.mccic.api.client.mccapi.EventApiHook;
import dev.andante.mccic.api.client.toast.CustomToast;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.qol.MCCICQoL;
import dev.andante.mccic.qol.client.config.QoLClientConfig;
import dev.andante.mccic.qol.client.config.QoLConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Calendar;
import java.util.TimeZone;

@Environment(EnvType.CLIENT)
public final class MCCICQolClientImpl implements MCCICQoL, ClientModInitializer {
    public static final String MCC_SOON_POPUP_TITLE = "text.%s.mcc_soon_popup.title".formatted(MOD_ID);
    public static final String MCC_SOON_POPUP_DESCRIPTION = "text.%s.mcc_soon_popup.description".formatted(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(QoLClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, QoLConfigScreen::new);

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
            Identifier id = new Identifier(container.getMetadata().getId(), "american_date_format");
            ResourceManagerHelper.registerBuiltinResourcePack(id, container, "MCCIC: American Date Format", ResourcePackActivationType.NORMAL);
        });

        MCCIClientLoginHelloEvent.EVENT.register(this::onClientLoginHello);
    }

    private void onClientLoginHello(ClientLoginNetworkHandler handler, LoginHelloS2CPacket packet) {
        if (QoLClientConfig.getConfig().eventAnnouncementToast()) {
            EventApiHook api = EventApiHook.INSTANCE;
            api.retrieve();
            if (api.isEventDateInFuture()) {
                api.getData().ifPresent(data -> {
                    data.createDate().ifPresent(date -> {
                        Calendar calendar = Calendar.getInstance();
                        TimeZone timeZone = calendar.getTimeZone();
                        calendar.setTime(date);
                        new CustomToast(
                            Text.translatable(MCC_SOON_POPUP_TITLE, data.getEventNumber()),
                            Text.translatable(MCC_SOON_POPUP_DESCRIPTION,
                                "%02d".formatted(calendar.get(Calendar.DAY_OF_MONTH)),
                                "%02d".formatted(calendar.get(Calendar.MONTH) + 1),
                                calendar.get(Calendar.HOUR),
                                calendar.get(Calendar.AM_PM) == Calendar.PM ? "pm": "am",
                                timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.SHORT)
                            )
                        ).add();
                    });
                });
            }
        }
    }
}
