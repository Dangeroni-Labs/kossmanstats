package net.infstudio.gokistats.core.config;

import java.util.LinkedHashMap;
import java.util.Map;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;

public record KossmanBalanceTuning(
		int maxStatLevel,
		KossmanXpTuning xp,
		double downgradeRefundRate,
		KossmanDeathPenaltyTuning deathPenalty,
		Map<String, KossmanStatTuning> stats
) {
	public static final int DEFAULT_MAX_STAT_LEVEL = 50;
	public static final KossmanBalanceTuning DEFAULT = new KossmanBalanceTuning(
			DEFAULT_MAX_STAT_LEVEL,
			KossmanXpTuning.DEFAULT,
			0.70D,
			KossmanDeathPenaltyTuning.DEFAULT,
			defaultStats()
	);

	public KossmanBalanceTuning {
		stats = Map.copyOf(stats);
	}

	public KossmanStatTuning stat(StatDefinition stat) {
		return stats.getOrDefault(stat.commandName(), KossmanStatTuning.DEFAULT);
	}

	public boolean isEnabled(StatDefinition stat) {
		return stat(stat).enabled();
	}

	public double effectMultiplier(StatDefinition stat) {
		if (!isEnabled(stat)) {
			return 0.0D;
		}

		return stat(stat).effectMultiplier();
	}

	public int effectiveLevel(StatDefinition stat, int storedLevel) {
		if (!isEnabled(stat)) {
			return 0;
		}

		return Math.min(Math.max(0, storedLevel), maxStatLevel);
	}

	private static Map<String, KossmanStatTuning> defaultStats() {
		Map<String, KossmanStatTuning> defaults = new LinkedHashMap<>();
		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			defaults.put(stat.commandName(), KossmanStatTuning.DEFAULT);
		}
		return defaults;
	}
}
