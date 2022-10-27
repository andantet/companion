package dev.andante.mccic.api.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MCCISoundPlayEvent {
    /**
     * Invoked when a sound is played.
     */
    Event<MCCISoundPlayEvent> EVENT = EventFactory.createArrayBacked(MCCISoundPlayEvent.class, callbacks -> context -> {
        for (MCCISoundPlayEvent callback : callbacks) {
            callback.onSoundPlay(context);
        }
    });

    void onSoundPlay(Context context);

    record Context(SoundSystem soundSystem, SoundInstance soundInstance) {
        public Identifier getSoundFileIdentifier() {
            return this.soundInstance.getSound().getIdentifier();
        }
    }
}
