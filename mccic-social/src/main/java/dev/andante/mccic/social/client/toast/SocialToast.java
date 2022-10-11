package dev.andante.mccic.social.client.toast;

import dev.andante.mccic.api.client.toast.CustomToast;
import dev.andante.mccic.social.MCCICSocial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class SocialToast extends CustomToast {
    public static final Identifier FRIEND_TEXTURE = new Identifier(MCCICSocial.MOD_ID, "textures/gui/toasts/social_friend.png");
    public static final Identifier PARTY_TEXTURE = new Identifier(MCCICSocial.MOD_ID, "textures/gui/toasts/social_party.png");

    public SocialToast(EventType eventType, String name) {
        super(eventType.getText(), Text.of(name), eventType.isFriend() ? FRIEND_TEXTURE : PARTY_TEXTURE);
    }

    public static void add(EventType eventType, String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getToastManager().add(new SocialToast(eventType, name));
    }

    public static void add(EventType eventType) {
        add(eventType, null);
    }

    public enum EventType {
        FRIEND_JOIN(true),
        FRIEND_LEAVE(true),
        PARTY_INVITE,
        PARTY_JOIN,
        PARTY_LEAVE,
        PARTY_DISBAND;

        private final boolean friend;
        private final Text text;

        EventType(boolean friend) {
            this.friend = friend;

            String id = this.name().toLowerCase(Locale.ROOT);
            this.text = Text.translatable("toast.%s.type.%s".formatted(MCCICSocial.MOD_ID, id));
        }

        EventType() {
            this(false);
        }

        public boolean isFriend() {
            return this.friend;
        }

        public Text getText() {
            return this.text;
        }
    }
}
