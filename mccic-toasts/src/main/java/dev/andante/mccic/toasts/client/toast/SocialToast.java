package dev.andante.mccic.toasts.client.toast;

import dev.andante.mccic.api.client.toast.AdaptableIconToast;
import dev.andante.mccic.toasts.MCCICToasts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class SocialToast extends AdaptableIconToast {
    public static final Identifier FRIEND_TEXTURE = new Identifier(MCCICToasts.MOD_ID, "textures/gui/toasts/friends.png");
    public static final Identifier PARTY_TEXTURE = new Identifier(MCCICToasts.MOD_ID, "textures/gui/toasts/parties.png");

    public SocialToast(EventType eventType, String name) {
        super(eventType.isFriend() ? FRIEND_TEXTURE : PARTY_TEXTURE, eventType.getText(), name.isBlank() ? new Text[0] : new Text[]{ Text.of(name) });
    }

    public static void add(EventType eventType, String name) {
        new SocialToast(eventType, name).add();
    }

    public enum EventType {
        FRIEND_JOIN(true),
        FRIEND_LEAVE(true),
        PARTY_INVITE,
        PARTY_JOIN,
        PARTY_JOIN_SELF,
        PARTY_LEAVE,
        PARTY_LEAVE_SELF,
        PARTY_LEADER,
        PARTY_LEADER_SELF,
        PARTY_DISBAND;

        private final boolean friend;
        private final Text text;

        EventType(boolean friend) {
            this.friend = friend;

            String id = this.name().toLowerCase(Locale.ROOT);
            this.text = Text.translatable("toast.%s.type.%s".formatted(MCCICToasts.MOD_ID, id));
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
