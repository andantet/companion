package dev.andante.mccic.toasts.client;

import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.UpdateTracker;
import dev.andante.mccic.api.client.event.MCCIClientGameJoinEvent;
import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.api.client.toast.AdaptableIconToast;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.api.mccapi.EventApiHook;
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
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TimeZone;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public final class MCCICToastsClientImpl implements MCCICToasts, ClientModInitializer {
    public static final Identifier
        QUEST_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/quest.png"),
        BADGE_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/badge.png"),
        EVENT_ANNOUNCEMENT_TOAST_TEXTURE = new Identifier(MOD_ID, "textures/gui/toasts/event_announcement.png");

    public static final String
        QUEST_COMPLETE_TEXT = "\\(.\\) Quest complete: ",
        BADGE_UNLOCKED_TEXT = "\\(.\\) You achieved the \\[";

    public static final String
        MCC_SOON_POPUP_TITLE = "text.%s.mcc_soon_popup.title".formatted(MOD_ID),
        MCC_SOON_POPUP_DESCRIPTION = "text.%s.mcc_soon_popup.description".formatted(MOD_ID);

    public static final BooleanSupplier
        FRIEND_CONFIG = () -> ToastsClientConfig.getConfig().friends(),
        PARTY_CONFIG = () -> ToastsClientConfig.getConfig().parties();

    public static final String
            UPDATE_POPUP_TITLE = "text.%s.update_available.title".formatted(MCCICToasts.MOD_ID),
            UPDATE_POPUP_DESCRIPTION = "text.%s.update_available.description".formatted(MCCICToasts.MOD_ID),
            UPDATE_VERSION = "text.%s.update_version".formatted(MCCICToasts.MOD_ID),
            UPDATE_MESSAGE = "text.%s.update_message".formatted(MCCICToasts.MOD_ID),
            UPDATE_MESSAGE_DOWNLOAD = "%s.download".formatted(UPDATE_MESSAGE),
            UPDATE_MESSAGE_TOOLTIP = "%s.tooltip".formatted(UPDATE_MESSAGE);

    public static final Identifier UPDATE_TOAST_TEXTURE = new Identifier(MCCICToasts.MOD_ID, "textures/gui/toasts/update.png");

    public static final List<SocialToastBehavior> SOCIAL_TOAST_BEHAVIORS = List.of(
        SocialToastBehavior.create("%s has come online!", FRIEND_CONFIG, EventType.FRIEND_JOIN),
        SocialToastBehavior.create("%s has gone offline.", FRIEND_CONFIG, EventType.FRIEND_LEAVE),

        SocialToastBehavior.create("%s invites you to their party!.+", PARTY_CONFIG, EventType.PARTY_INVITE),
        SocialToastBehavior.create("%s has joined the party.", PARTY_CONFIG, EventType.PARTY_JOIN),
        SocialToastBehavior.create("%s leaves the party.", PARTY_CONFIG, EventType.PARTY_LEAVE),
        SocialToastBehavior.create("%s has been promoted to party leader.", PARTY_CONFIG, EventType.PARTY_LEADER),
        SocialToastBehavior.createUncaptured("%s has made you the party leader.", PARTY_CONFIG, EventType.PARTY_LEADER_SELF),
        SocialToastBehavior.createUncaptured("%s has gone offline, making you the new party leader.", PARTY_CONFIG, EventType.PARTY_LEADER_SELF),

        SocialToastBehavior.create("Your party has been disbanded.", PARTY_CONFIG, EventType.PARTY_DISBAND),
        SocialToastBehavior.create("You have joined the party.", PARTY_CONFIG, EventType.PARTY_JOIN_SELF),
        SocialToastBehavior.create("You left the party.", PARTY_CONFIG, EventType.PARTY_LEAVE_SELF),
        SocialToastBehavior.create("You are now the party leader.", PARTY_CONFIG, EventType.PARTY_LEADER_SELF)
    );

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(ToastsClientConfig.CONFIG_HOLDER, ToastsConfigScreen::new);
        MCCICConfigCommand.registerNewConfig(ID, ToastsConfigScreen::new);

        MCCIChatEvent.EVENT.register(this::onChatEvent);
        MCCIClientGameJoinEvent.EVENT.register(this::onGameJoin);
    }

    public EventResult onChatEvent(MCCIChatEvent.Context context) {
        if (context.isEmote()) {
            return EventResult.pass();
        }

        String raw = context.getRaw();

        boolean socialToastBehaviorPassed = false;

        for (SocialToastBehavior behavior : SOCIAL_TOAST_BEHAVIORS) {
            if (behavior.shouldToast()) {
                Optional<String> maybeUsername = behavior.matchAndRetrieveUsername(raw);
                if (maybeUsername.isPresent()) {
                    String username = maybeUsername.get();
                    for (EventType eventType : behavior.getEventTypes()) {
                        SocialToast.add(eventType, username);
                    }

                    socialToastBehaviorPassed = true;
                }
            }
        }

        if (socialToastBehaviorPassed) {
            return EventResult.cancel();
        }

        ToastsClientConfig config = ToastsClientConfig.getConfig();
        Text message = context.message();

        if (config.quests()) {
            OptionalInt opt = matchAndGrabIndex(message, raw, QUEST_COMPLETE_TEXT, Icon.QUEST_BOOK);
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

        if (config.badges()) {
            OptionalInt opt = matchAndGrabIndex(message, raw, BADGE_UNLOCKED_TEXT, Icon.BADGE);
            if (opt.isPresent()) {
                int sub = opt.getAsInt();
                String name = raw.substring(sub, raw.length() - "] badge!".length());
                new AdaptableIconToast(BADGE_TOAST_TEXTURE,
                    Text.translatable("toast.%s.badge_unlocked.title".formatted(MOD_ID)),
                    Text.translatable("toast.%s.badge_unlocked.description".formatted(MOD_ID), name)
                ).add();
                return EventResult.cancel();
            }
        }

        return EventResult.pass();
    }

    public static OptionalInt matchAndGrabIndex(Text message, String raw, String pattern, Icon icon) {
        return raw.matches(pattern + ".+") && UnicodeIconsStore.doesTextContainIcon(message, icon)
            ? OptionalInt.of(pattern.replaceAll("\\\\", "").length())
            : OptionalInt.empty();
    }

    private void onGameJoin(ClientPlayNetworkHandler handler, GameJoinS2CPacket packet) {
        ToastsClientConfig config = ToastsClientConfig.getConfig();
        if (config.eventAnnouncements()) {
            EventApiHook.INSTANCE.retrieve().thenAccept(api -> {
                if (api.isEventDateInFuture()) {
                    api.getData().ifPresent(data -> {
                        data.createDate().ifPresent(date -> {
                            Calendar calendar = Calendar.getInstance();
                            TimeZone timeZone = calendar.getTimeZone();
                            calendar.setTime(date);
                            new AdaptableIconToast(EVENT_ANNOUNCEMENT_TOAST_TEXTURE,
                                    Text.translatable(MCC_SOON_POPUP_TITLE, data.event()),
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
            });
        }

        if (config.updateNotifications()) {
            UpdateTracker.INSTANCE.retrieve().thenAccept(tracker -> {
                tracker.getData().ifPresent(data -> {
                    if (tracker.isUpdateAvailable()) {
                        data.createSemanticVersion().ifPresent(version -> {
                            FabricLoader loader = FabricLoader.getInstance();
                            Text updateVersion = Text.translatable(UPDATE_VERSION, "%s.%s.%s%s".formatted(
                                    version.getVersionComponent(0), version.getVersionComponent(1), version.getVersionComponent(2),
                                    version.getPrereleaseKey().map(s -> "-" + s).orElse("")), version.getBuildKey().orElseGet(MinecraftClient.getInstance()::getGameVersion)
                            );

                            new AdaptableIconToast(UPDATE_TOAST_TEXTURE, Text.translatable(UPDATE_POPUP_TITLE, updateVersion), Text.translatable(UPDATE_POPUP_DESCRIPTION)).add();

                            loader.getModContainer(MCCIC.MOD_ID)
                                    .map(ModContainer::getMetadata)
                                    .map(ModMetadata::getContact)
                                    .flatMap(contacts -> contacts.get("update"))
                                    .map(s -> s.formatted(data.latest()))
                                    .ifPresent(downloadUrl -> {
                                        MinecraftClient client = MinecraftClient.getInstance();
                                        if (client.player != null) {
                                            client.player.sendMessage(
                                                    Text.translatable(UPDATE_MESSAGE, updateVersion, Text.translatable(UPDATE_MESSAGE_DOWNLOAD).formatted(Formatting.BOLD, Formatting.UNDERLINE))
                                                            .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(UPDATE_MESSAGE_TOOLTIP).formatted(Formatting.GREEN)))
                                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl)))
                                                            .formatted(Formatting.GREEN)
                                            );
                                        }
                                    });
                        });
                    }
                });
            });
        }
    }
}
