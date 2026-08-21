package net.infstudio.gokistats.fabric.death;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.progression.StatDeathPenaltyResult;
import net.infstudio.gokistats.fabric.effect.StatEffects;
import net.infstudio.gokistats.fabric.network.StatSnapshotSync;
import net.infstudio.gokistats.fabric.progression.StatDeathPenaltyService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class StatDeathPenaltyHandler {
	private StatDeathPenaltyHandler() {
	}

	public static void register() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (alive) {
				return;
			}

			applyPenalty(oldPlayer, newPlayer);
		});
	}

	private static void applyPenalty(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
		StatDeathPenaltyResult result = StatDeathPenaltyService.apply(oldPlayer, newPlayer);
		StatEffects.afterProgressionChanges(newPlayer, KossmanStatDefinitions.ALL);
		StatSnapshotSync.send(newPlayer);

		if (result.appliedLoss() > 0) {
			newPlayer.sendSystemMessage(Component.literal(feedbackMessage(result)));
		}
	}

	private static String feedbackMessage(StatDeathPenaltyResult result) {
		return "You forgot " + result.appliedLoss() + " stat level" + (result.appliedLoss() == 1 ? "" : "s") + " after dying.";
	}
}
