package net.infstudio.gokistats.fabric.progression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.progression.StatDeathPenaltyPolicy;
import net.infstudio.gokistats.core.progression.StatDeathPenaltyResult;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;

public final class StatDeathPenaltyService {
	private StatDeathPenaltyService() {
	}

	public static StatDeathPenaltyResult apply(ServerPlayer player) {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(currentLevels(player), new Random(player.getRandom().nextLong()));

		for (Map.Entry<StatId, Integer> entry : result.lostLevels().entrySet()) {
			StatDefinition stat = KossmanStatDefinitions.byId(entry.getKey()).orElse(null);
			if (stat == null) {
				continue;
			}

			KossmanPlayerStateStorage.decrementLevel(player, stat, entry.getValue(), StatDeathPenaltyPolicy.MINIMUM_RETAINED_STAT_LEVEL);
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
