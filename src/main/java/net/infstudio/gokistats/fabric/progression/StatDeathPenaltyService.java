package net.infstudio.gokistats.fabric.progression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.config.KossmanBalanceTuning;
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
		Map<StatId, Integer> oldLevels = storedLevels(oldPlayer);
		KossmanBalanceTuning tuning = KossmanBalance.current();
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(eligibleLevels(oldLevels), new Random(newPlayer.getRandom().nextLong()));

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			int oldLevel = oldLevels.getOrDefault(stat.id(), 0);
			int lostLevels = result.lostLevels().getOrDefault(stat.id(), 0);
			int finalLevel = finalLevelAfterPenalty(oldLevel, lostLevels, tuning.deathPenalty().minimumRetainedStatLevel());

			KossmanPlayerStateStorage.setLevel(newPlayer, stat, finalLevel);
		}

		return result;
	}

	private static Map<StatId, Integer> storedLevels(ServerPlayer player) {
		Map<StatId, Integer> levels = new LinkedHashMap<>();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			levels.put(stat.id(), KossmanPlayerStateStorage.getLevel(player, stat));
		}

		return levels;
	}

	private static Map<StatId, Integer> eligibleLevels(Map<StatId, Integer> storedLevels) {
		Map<StatId, Integer> eligibleLevels = new LinkedHashMap<>();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			int level = KossmanBalance.current().isEnabled(stat)
					? storedLevels.getOrDefault(stat.id(), 0)
					: 0;
			eligibleLevels.put(stat.id(), level);
		}

		return eligibleLevels;
	}

	static int finalLevelAfterPenalty(int oldLevel, int lostLevels, int minimumRetainedStatLevel) {
		if (oldLevel <= 0) {
			return 0;
		}

		int clampedOldLevel = Math.max(0, oldLevel);
		int reducedLevel = Math.max(0, clampedOldLevel - Math.max(0, lostLevels));
		if (clampedOldLevel <= minimumRetainedStatLevel) {
			return reducedLevel;
		}

		return Math.max(minimumRetainedStatLevel, reducedLevel);
	}
}
