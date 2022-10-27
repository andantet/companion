package dev.andante.mccic.toasts.client;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.event.MCCIClientLoginHelloEvent;
import dev.andante.mccic.api.client.mccapi.EventApiHook;
import dev.andante.mccic.api.client.toast.AdaptableIconToast;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.toasts.MCCICToasts;
import dev.andante.mccic.toasts.client.config.ToastsClientConfig;
import dev.andante.mccic.toasts.client.config.ToastsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Calendar;
import java.util.OptionalInt;
import java.util.TimeZone;

@Environment(EnvType.CLIENT)
public final class MCCICToastsClientImpl implements MCCICToasts, ClientModInitializer {
    public static final Identifier QUEST_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/quest.png");
    public static final Identifier ACHIEVEMENT_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/achievement.png");
    public static final Identifier EVENT_ANNOUNCEMENT_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/event_announcement.png");

    public static final String QUEST_COMPLETE_TEXT = "\\(.\\) Quest complete: ";
    public static final String ACHIEVEMENT_UNLOCKED_TEXT = "\\(.\\) Achievement unlocked: \\[";

    public static final String MCC_SOON_POPUP_TITLE = "text.%s.mcc_soon_popup.title".formatted(MOD_ID);
    public static final String MCC_SOON_POPUP_DESCRIPTION = "text.%s.mcc_soon_popup.description".formatted(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(ToastsClientConfig.CONFIG_HOLDER, ToastsConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, ToastsConfigScreen::new);

        MCCIChatEvent.EVENT.register(this::onChatEvent);
        MCCIClientLoginHelloEvent.EVENT.register(this::onClientLoginHello);
    }

    public EventResult onChatEvent(MCCIChatEvent.Context context) {
        ToastsClientConfig config = ToastsClientConfig.getConfig();
        Text message = context.message();
        String raw = context.getRaw();

        if (config.quests()) {
            OptionalInt opt = processPrefix(message, raw, QUEST_COMPLETE_TEXT, Icon.QUEST_BOOK);
            if (opt.isPresent()) {
                int sub = opt.getAsInt();
                String name = raw.substring(sub);
                new AdaptableIconToast(QUEST_TOAST_TEXTURE,
                    Text.translatable("toast.%s.quest_complete.title".formatted(MOD_ID)),
                    Text.translatable("toast.%s.quest_complete.description".formatted(MOD_ID), name)
                ).add();
                return EventResult.cancel();
            }
        }

        if (config.achievements()) {
            OptionalInt opt = processPrefix(message, raw, ACHIEVEMENT_UNLOCKED_TEXT, Icon.ACHIEVEMENT);
            if (opt.isPresent()) {
                int sub = opt.getAsInt();
                String name = raw.substring(sub, raw.length() - 1);
                new AdaptableIconToast(ACHIEVEMENT_TOAST_TEXTURE,
                    Text.translatable("toast.%s.achievement_unlocked.title".formatted(MOD_ID)),
                    Text.translatable("toast.%s.achievement_unlocked.description".formatted(MOD_ID), name)
                ).add();
                return EventResult.cancel();
            }
        }

        return EventResult.pass();
    }

    public static OptionalInt processPrefix(Text message, String raw, String pattern, Icon icon) {
        return raw.matches(pattern + ".+") && UnicodeIconsStore.textContainsIcon(message, icon)
            ? OptionalInt.of(pattern.replaceAll("\\\\", "").length())
            : OptionalInt.empty();
    }

    private void onClientLoginHello(ClientLoginNetworkHandler handler, LoginHelloS2CPacket packet) {
        if (ToastsClientConfig.getConfig().eventAnnouncements()) {
            EventApiHook api = EventApiHook.INSTANCE;
            api.retrieve();
            if (api.isEventDateInFuture()) {
                api.getData().ifPresent(data -> {
                    data.createDate().ifPresent(date -> {
                        Calendar calendar = Calendar.getInstance();
                        TimeZone timeZone = calendar.getTimeZone();
                        calendar.setTime(date);
                        new AdaptableIconToast(EVENT_ANNOUNCEMENT_TOAST_TEXTURE,
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
