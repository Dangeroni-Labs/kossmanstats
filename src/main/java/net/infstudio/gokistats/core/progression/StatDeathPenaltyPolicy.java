package net.infstudio.gokistats.core.progression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.config.KossmanDeathPenaltyTuning;
import net.infstudio.gokistats.core.definition.StatId;

public final class StatDeathPenaltyPolicy {
	private StatDeathPenaltyPolicy() {
	}

	public static StatDeathPenaltyResult calculate(Map<StatId, Integer> currentLevels, RandomGenerator random) {
		KossmanDeathPenaltyTuning tuning = KossmanBalance.current().deathPenalty();
		Map<StatId, Integer> mutableLevels = new LinkedHashMap<>();
		Map<StatId, Integer> lostLevels = new LinkedHashMap<>();
		int totalInvestedLevels = 0;
		int availableRemovableLevels = 0;

		for (Map.Entry<StatId, Integer> entry : currentLevels.entrySet()) {
			int level = Math.max(0, entry.getValue());
			mutableLevels.put(entry.getKey(), level);
			if (level > tuning.minimumRetainedStatLevel()) {
				totalInvestedLevels += level;
				availableRemovableLevels += level - tuning.minimumRetainedStatLevel();
			}
		}

		if (totalInvestedLevels == 0 || availableRemovableLevels == 0) {
			return new StatDeathPenaltyResult(0, 0, Map.of());
		}

		int requestedLoss = clamp(
				(int) Math.round(totalInvestedLevels * tuning.lossRate()),
				tuning.minimumLoss(),
				tuning.maximumLoss()
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
			if (level > KossmanBalance.current().deathPenalty().minimumRetainedStatLevel()) {
				totalWeight += level;
			}
		}

		if (totalWeight <= 0) {
			return null;
		}

		int target = random.nextInt(totalWeight);
		for (Map.Entry<StatId, Integer> entry : currentLevels.entrySet()) {
			int level = entry.getValue();
			if (level <= KossmanBalance.current().deathPenalty().minimumRetainedStatLevel()) {
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
