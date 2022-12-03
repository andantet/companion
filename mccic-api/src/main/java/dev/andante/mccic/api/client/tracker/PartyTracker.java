package dev.andante.mccic.api.client.tracker;

import dev.andante.mccic.api.client.UnicodeIconsStore;
import dev.andante.mccic.api.client.UnicodeIconsStore.Icon;
import dev.andante.mccic.api.client.util.ClientHelper;
import dev.andante.mccic.api.util.TextQuery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks active party data.
 */
@Environment(EnvType.CLIENT)
public class PartyTracker {
    public static final PartyTracker INSTANCE = new PartyTracker();

    private PartyInstance instance;

    public PartyTracker() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        this.instance = null;

        if (GameTracker.INSTANCE.isOnServer()) {
            ClientHelper.getScoreboard()
                        .flatMap(scoreboard -> ClientHelper.getScoreboardPlayerNames()
                                                           .map(names -> names.stream()
                                                                              .map(scoreboard::getPlayerTeam)
                                                                              .map(team -> team.decorateName(Text.empty()))
                                                                              .toList()
                                                           )
                        )
                        .ifPresent(names -> {
                            String expectedParty = names.get(1).getString();
                            if (expectedParty.contains("PARTY:")) {
                                List<PartyMember> members = new ArrayList<>();
                                boolean foundLeader = false;
                                for (int i = 0; i < 4; i++) {
                                    int index = 2 + i;
                                    if (index < names.size()) {
                                        MutableText text = names.get(2 + i);
                                        TextQuery query = TextQuery.findText(text, TextQuery.USERNAME_REGEX).orElse(null);
                                        if (query != null) {
                                            Text result = query.getResult();
                                            TextColor color = result.getStyle().getColor();
                                            String colorName = color == null ? Formatting.WHITE.getName() : color.getName();
                                            boolean online = !colorName.equals(Formatting.DARK_GRAY.getName());
                                            boolean leader = !foundLeader && UnicodeIconsStore.doesTextContainIconExact(text, Icon.CROWN);
                                            members.add(new PartyMember(result.getString(), online, leader));

                                            if (leader) {
                                                foundLeader = true;
                                            }
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }

                                this.instance = new PartyInstance(members);
                            }
                        });
        }
    }

    /**
     * @return the {@link PartyInstance} for the current tick
     */
    @Nullable
    public PartyInstance getInstance() {
        return this.instance;
    }

    /**
     * @return whether or not the client player is in a party
     */
    public boolean isInParty() {
        return this.instance != null;
    }

    /**
     * @return the size of the party, or -1 if the client player is not in a party
     */
    public int getSize() {
        return this.instance == null ? -1 : this.instance.getSize();
    }

    /**
     * @return the members of the party, or an empty list if the client player is not in a party
     */
    public List<PartyMember> getMembers() {
        return this.instance == null ? Collections.emptyList() : this.instance.members();
    }

    /**
     * An instance of a party.
     */
    public record PartyInstance(List<PartyMember> members) {
        public PartyMember getLeader() {
            return this.members.stream().filter(PartyMember::leader).findAny().orElse(new PartyMember("", true, true));
        }

        public int getSize() {
            return this.members.size();
        }

        @Override
        public List<PartyMember> members() {
            return new ArrayList<>(this.members);
        }
    }

    /**
     * A member of a party.
     */
    public record PartyMember(String name, boolean online, boolean leader) {
    }
}
