package dev.andante.mccic.qol.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.andante.mccic.api.MCCIC;
import dev.andante.mccic.api.client.tracker.GameTracker;
import dev.andante.mccic.api.client.tracker.PartyTracker;
import dev.andante.mccic.api.client.tracker.PartyTracker.PartyMember;
import dev.andante.mccic.qol.MCCICQoL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public interface PartyCommandExtensions {
    SimpleCommandExceptionType NO_PARTY_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("text.%s.party_not_found".formatted(MCCICQoL.MOD_ID)));
    SimpleCommandExceptionType NO_OFFLINE_MEMBERS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("text.%s.no_offline_party_members".formatted(MCCICQoL.MOD_ID)));

    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("party-%s".formatted(MCCIC.MOD_ID))
                        .requires(source -> GameTracker.INSTANCE.isOnServer())
                        .then(
                                literal("kickoffline")
                                        .executes(PartyCommandExtensions::executeKickOffline)
                        )
        );
    }

    private static int executeKickOffline(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        PartyTracker tracker = PartyTracker.INSTANCE;

        if (!tracker.isInParty()) {
            throw NO_PARTY_EXCEPTION.create();
        }

        List<PartyMember> members = tracker.getOfflineMembers();
        if (members.isEmpty()) {
            throw NO_OFFLINE_MEMBERS_EXCEPTION.create();
        } else {
            ClientPlayerEntity player = context.getSource().getPlayer();
            for (PartyMember member : members) {
                player.networkHandler.sendCommand("party kick %s".formatted(member.name()));
            }
        }

        return 1;
    }
}
