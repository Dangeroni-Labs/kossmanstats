package net.infstudio.gokistats.core.progression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import net.infstudio.gokistats.core.definition.StatId;

public final class StatDeathPenaltyPolicy {
	public static final double DEATH_LOSS_RATE = 0.10D;
	public static final int MINIMUM_DEATH_LOSS = 1;
	public static final int MAXIMUM_DEATH_LOSS = 5;
	public static final int MINIMUM_RETAINED_STAT_LEVEL = 1;

	private StatDeathPenaltyPolicy() {
	}

	public static StatDeathPenaltyResult calculate(Map<StatId, Integer> currentLevels, RandomGenerator random) {
		Map<StatId, Integer> mutableLevels = new LinkedHashMap<>();
		Map<StatId, Integer> lostLevels = new LinkedHashMap<>();
		int totalInvestedLevels = 0;
		int availableRemovableLevels = 0;

		for (Map.Entry<StatId, Integer> entry : currentLevels.entrySet()) {
			int level = Math.max(0, entry.getValue());
			mutableLevels.put(entry.getKey(), level);
			if (level > MINIMUM_RETAINED_STAT_LEVEL) {
				totalInvestedLevels += level;
				availableRemovableLevels += level - MINIMUM_RETAINED_STAT_LEVEL;
			}
		}

		if (totalInvestedLevels == 0 || availableRemovableLevels == 0) {
			return new StatDeathPenaltyResult(0, 0, Map.of());
		}

		int requestedLoss = clamp(
				(int) Math.round(totalInvestedLevels * DEATH_LOSS_RATE),
				MINIMUM_DEATH_LOSS,
				MAXIMUM_DEATH_LOSS
		);
		int appliedLoss = Math.min(requestedLoss, availableRemovableLevels);

		for (int removed = 0; removed < appliedLoss; removed++) {
			StatId selected = selectWeightedStat(mutableLevels, random);
			if (selected == null) {
				break;
			}

			mutableLevels.put(selected, mutableLevels.get(selected) - 1);
			lostLevels.merge(selected, 1, Integer::sum);
		}

		return new StatDeathPenaltyResult(requestedLoss, lostLevels.values().stream().mapToInt(Integer::intValue).sum(), lostLevels);
	}

	private static StatId selectWeightedStat(Map<StatId, Integer> currentLevels, RandomGenerator random) {
		int totalWeight = 0;

		for (int level : currentLevels.values()) {
			if (level > MINIMUM_RETAINED_STAT_LEVEL) {
				totalWeight += level;
			}
		}

		if (totalWeight <= 0) {
			return null;
		}

		int target = random.nextInt(totalWeight);
		for (Map.Entry<StatId, Integer> entry : currentLevels.entrySet()) {
			int level = entry.getValue();
			if (level <= MINIMUM_RETAINED_STAT_LEVEL) {
				continue;
			}

			target -= level;
			if (target < 0) {
				return entry.getKey();
			}
		}

		return null;
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
