package net.infstudio.gokistats.fabric.progression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.progression.StatDeathPenaltyPolicy;
import net.infstudio.gokistats.core.progression.StatDeathPenaltyResult;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;

public final class StatDeathPenaltyService {
	private StatDeathPenaltyService() {
	}

	public static StatDeathPenaltyResult apply(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
		Map<StatId, Integer> oldLevels = currentLevels(oldPlayer);
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(oldLevels, new Random(newPlayer.getRandom().nextLong()));

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			int oldLevel = oldLevels.getOrDefault(stat.id(), 0);
			int lostLevels = result.lostLevels().getOrDefault(stat.id(), 0);
			int finalLevel = Math.max(
					0,
					Math.max(StatDeathPenaltyPolicy.MINIMUM_RETAINED_STAT_LEVEL, oldLevel - lostLevels)
			);

			if (oldLevel <= 0) {
				finalLevel = 0;
			}

			KossmanPlayerStateStorage.setLevel(newPlayer, stat, finalLevel);
		}

		return result;
	}

	private static Map<StatId, Integer> currentLevels(ServerPlayer player) {
		Map<StatId, Integer> levels = new LinkedHashMap<>();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			levels.put(stat.id(), KossmanPlayerStateStorage.getLevel(player, stat));
		}

		return levels;
	}
}
