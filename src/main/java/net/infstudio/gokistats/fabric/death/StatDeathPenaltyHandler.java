package net.infstudio.gokistats.fabric.death;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
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

			applyPenalty(newPlayer);
		});
	}

	private static void applyPenalty(ServerPlayer player) {
		StatDeathPenaltyResult result = StatDeathPenaltyService.apply(player);
		if (result.appliedLoss() <= 0) {
			StatSnapshotSync.send(player);
			return;
		}

		StatEffects.afterProgressionChanges(player, affectedStats(result));
		StatSnapshotSync.send(player);
		player.sendSystemMessage(Component.literal(feedbackMessage(result)));
	}

	private static List<StatDefinition> affectedStats(StatDeathPenaltyResult result) {
		List<StatDefinition> stats = new ArrayList<>();

		for (Map.Entry<StatId, Integer> entry : result.lostLevels().entrySet()) {
			KossmanStatDefinitions.byId(entry.getKey()).ifPresent(stats::add);
		}

		return stats;
	}

	private static String feedbackMessage(StatDeathPenaltyResult result) {
		return "You forgot " + result.appliedLoss() + " stat level" + (result.appliedLoss() == 1 ? "" : "s") + " after dying.";
	}
}
