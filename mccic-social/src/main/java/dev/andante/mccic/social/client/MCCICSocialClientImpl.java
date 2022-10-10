package dev.andante.mccic.social.client;

import dev.andante.mccic.api.client.event.MCCIChatEvent;
import dev.andante.mccic.config.client.ClientConfigRegistry;
import dev.andante.mccic.config.client.command.MCCICConfigCommand;
import dev.andante.mccic.social.client.config.MCCICSocialConfigScreen;
import dev.andante.mccic.social.client.config.SocialClientConfig;
import dev.andante.mccic.social.client.toast.SocialToast;
import dev.andante.mccic.social.client.toast.SocialToast.EventType;
import dev.andante.mccic.api.event.EventResult;
import dev.andante.mccic.social.MCCICSocial;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class MCCICSocialClientImpl implements MCCICSocial, ClientModInitializer {
    public static final String FRIEND_JOIN_TEXT = " has come online!";
    public static final String FRIEND_LEAVE_TEXT = " has gone offline.";
    public static final String PARTY_INVITE_TEXT = " invites you to their party!";
    public static final String PARTY_JOIN_TEXT = " has joined the party.";
    public static final String PARTY_JOIN_YOU_TEXT = "You have joined the party.";
    public static final String PARTY_LEAVE_TEXT = " leaves the party.";
    public static final String PARTY_LEAVE_YOU_TEXT = "You left the party.";
    public static final String PARTY_DISBAND_TEXT = "Your party has been disbanded.";

    @Override
    public void onInitializeClient() {
        ClientConfigRegistry.INSTANCE.registerAndLoad(SocialClientConfig.CONFIG_HOLDER);
        MCCICConfigCommand.registerNewConfig(ID, MCCICSocialConfigScreen::new);
        MCCIChatEvent.EVENT.register(this::onChatEvent);
    }

    public EventResult onChatEvent(ChatHud chatHud, Text message, String raw, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh) {
        if (!SocialClientConfig.getConfig().allToasts()) {
            return EventResult.pass();
        }

        if (raw.equals(PARTY_DISBAND_TEXT)) {
            SocialToast.add(EventType.PARTY_DISBAND, "");
            return EventResult.cancel();
        }

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

        return EventResult.pass();
    }

    public boolean isUsernameValid(String username) {
        return username.indexOf(' ') == -1;
    }
}
