package dev.andante.mccic.toasts.client;

import com.mojang.authlib.GameProfile;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.event.MCCIClientLoginHelloEvent;
import dev.andante.mccic.api.mccapi.EventApiHook;
import dev.andante.mccic.api.client.toast.AdaptableIconToast;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.toasts.MCCICToasts;
import dev.andante.mccic.toasts.client.config.ToastsClientConfig;
import dev.andante.mccic.toasts.client.config.ToastsConfigScreen;
import dev.andante.mccic.toasts.client.toast.SocialToast;
import dev.andante.mccic.toasts.client.toast.SocialToast.EventType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.TimeZone;

@Environment(EnvType.CLIENT)
public final class MCCICToastsClientImpl implements MCCICToasts, ClientModInitializer {
    public static final Identifier
        QUEST_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/quest.png"),
        ACHIEVEMENT_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/achievement.png"),
        EVENT_ANNOUNCEMENT_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/event_announcement.png");

    public static final String
        QUEST_COMPLETE_TEXT = "\\(.\\) Quest complete: ",
        ACHIEVEMENT_UNLOCKED_TEXT = "\\(.\\) Achievement unlocked: \\[";

    public static final String
        MCC_SOON_POPUP_TITLE = "text.%s.mcc_soon_popup.title".formatted(MOD_ID),
        MCC_SOON_POPUP_DESCRIPTION = "text.%s.mcc_soon_popup.description".formatted(MOD_ID);

    public static final String
        FRIEND_JOIN_TEXT = " has come online!",
        FRIEND_LEAVE_TEXT = " has gone offline.",
        PARTY_INVITE_TEXT = " invites you to their party!",
        PARTY_JOIN_TEXT = " has joined the party.",
        PARTY_JOIN_YOU_TEXT = "You have joined the party.",
        PARTY_LEAVE_TEXT = " leaves the party.",
        PARTY_LEAVE_YOU_TEXT = "You left the party.",
        PARTY_DISBAND_TEXT = "Your party has been disbanded.";

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

        if (config.friends()) {
            if (raw.contains(FRIEND_JOIN_TEXT)) {
                int l = raw.length();
                String username = raw.substring(0, l - FRIEND_JOIN_TEXT.length());
                if (this.isUsernameValid(username)) {
                    SocialToast.add(EventType.FRIEND_JOIN, username);
                    return EventResult.cancel();
                }
            }

            if (raw.contains(FRIEND_LEAVE_TEXT)) {
                int l = raw.length();
                String username = raw.substring(0, l - FRIEND_LEAVE_TEXT.length());
                if (this.isUsernameValid(username)) {
                    SocialToast.add(EventType.FRIEND_LEAVE, username);
                    return EventResult.cancel();
                }
            }
        }

        if (config.parties()) {
            if (raw.equals(PARTY_DISBAND_TEXT)) {
                SocialToast.add(EventType.PARTY_DISBAND, "");
                return EventResult.cancel();
            }

            if (raw.contains(PARTY_INVITE_TEXT)) {
                int l = raw.length();
                String username = raw.substring(0, l - PARTY_INVITE_TEXT.length() - 6);
                if (this.isUsernameValid(username)) {
                    SocialToast.add(EventType.PARTY_INVITE, username);
                    return EventResult.pass();
                }
            }

            if (raw.contains(PARTY_JOIN_TEXT)) {
                int l = raw.length();
                String username = raw.substring(0, l - PARTY_JOIN_TEXT.length());
                if (this.isUsernameValid(username)) {
                    SocialToast.add(EventType.PARTY_JOIN, username);
                    return EventResult.cancel();
                }
            }

            if (raw.contains(PARTY_LEAVE_TEXT)) {
                int l = raw.length();
                String username = raw.substring(0, l - PARTY_LEAVE_TEXT.length());
                if (this.isUsernameValid(username)) {
                    SocialToast.add(EventType.PARTY_LEAVE, username);
                    return EventResult.cancel();
                }
            }

            if (raw.equals(PARTY_JOIN_YOU_TEXT)) {
                SocialToast.add(EventType.PARTY_JOIN, MinecraftClient.getInstance().getSession().getProfile().getName());
                return EventResult.cancel();
            }

            if (raw.equals(PARTY_LEAVE_YOU_TEXT)) {
                SocialToast.add(EventType.PARTY_LEAVE, MinecraftClient.getInstance().getSession().getProfile().getName());
                return EventResult.cancel();
            }
        }

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

    public boolean isUsernameValid(String username) {
        return username.indexOf(' ') == -1;
    }

    public static boolean isPlayerInPlayerList(MinecraftClient client, PlayerEntity player) {
        GameProfile profile = player.getGameProfile();
        Collection<PlayerListEntry> playerList = client.getNetworkHandler().getPlayerList();
        return playerList.stream().anyMatch(entry -> entry.getProfile().equals(profile));
    }
}
