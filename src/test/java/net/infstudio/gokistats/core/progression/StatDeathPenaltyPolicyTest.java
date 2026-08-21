package net.infstudio.gokistats.core.progression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class StatDeathPenaltyPolicyTest {
	@AfterEach
	void resetBalance() {
		KossmanBalance.reset();
	}

	@Test
	void returnsNoLossWhenNoStatsAreUpgraded() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(), new Random(1L));

		assertEquals(0, result.requestedLoss());
		assertEquals(0, result.appliedLoss());
		assertTrue(result.lostLevels().isEmpty());
	}

	@Test
	void returnsNoLossWhenOnlyLevelOneStatsExist() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 1),
				level(KossmanStatDefinitions.HEALTH.id(), 1)
		), new Random(2L));

		assertEquals(0, result.requestedLoss());
		assertEquals(0, result.appliedLoss());
		assertTrue(result.lostLevels().isEmpty());
	}

	@Test
	void removesOneLevelFromSingleEligibleStat() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 2)
		), new Random(3L));

		assertEquals(1, result.requestedLoss());
		assertEquals(1, result.appliedLoss());
		assertEquals(1, result.lostLevels().get(KossmanStatDefinitions.MINING.id()));
	}

	@Test
	void appliesMinimumLossOfOneWhenEligibleStatsExist() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 3),
				level(KossmanStatDefinitions.HEALTH.id(), 2)
		), new Random(4L));

		assertEquals(1, result.requestedLoss());
		assertEquals(1, result.appliedLoss());
		assertEquals(1, totalLost(result));
	}

	@Test
	void calculatesMidRangePenaltyBetweenTwoAndFour() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 10),
				level(KossmanStatDefinitions.HEALTH.id(), 10),
				level(KossmanStatDefinitions.PROTECTION.id(), 10)
		), new Random(5L));

		assertEquals(3, result.requestedLoss());
		assertEquals(3, result.appliedLoss());
		assertEquals(3, totalLost(result));
	}

	@Test
	void capsRequestedLossAtFiveLevels() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 32),
				level(KossmanStatDefinitions.HEALTH.id(), 20),
				level(KossmanStatDefinitions.SWORDSMANSHIP.id(), 15),
				level(KossmanStatDefinitions.PROTECTION.id(), 8)
		), new Random(6L));

		assertEquals(5, result.requestedLoss());
		assertEquals(5, result.appliedLoss());
		assertEquals(5, totalLost(result));
	}

	@Test
	void neverDropsStatsBelowRetainedLevelOne() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 2),
				level(KossmanStatDefinitions.HEALTH.id(), 2)
		), new AlwaysZeroRandom());

		assertEquals(1, result.appliedLoss());
		assertTrue(result.lostLevels().values().stream().allMatch(loss -> loss <= 1));
	}

	@Test
	void appliedLossNeverExceedsRemovableLevels() {
		Map<StatId, Integer> levels = levels(
				level(KossmanStatDefinitions.MINING.id(), 2),
				level(KossmanStatDefinitions.HEALTH.id(), 1),
				level(KossmanStatDefinitions.PROTECTION.id(), 0)
		);

		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels, new Random(7L));

		assertTrue(result.appliedLoss() <= removableLevels(levels));
		assertEquals(totalLost(result), result.appliedLoss());
	}

	@Test
	void totalRemovedLevelsMatchesExpectedPenalty() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 12),
				level(KossmanStatDefinitions.HEALTH.id(), 9),
				level(KossmanStatDefinitions.PROTECTION.id(), 4)
		), new Random(8L));

		assertEquals(3, result.requestedLoss());
		assertEquals(3, result.appliedLoss());
		assertEquals(3, totalLost(result));
	}

	@Test
	void repeatedWeightedSelectionCanRemoveMultipleLevelsFromOneStat() {
		StatDeathPenaltyResult result = StatDeathPenaltyPolicy.calculate(levels(
				level(KossmanStatDefinitions.MINING.id(), 50)
		), new AlwaysZeroRandom());

		assertEquals(5, result.requestedLoss());
		assertEquals(5, result.appliedLoss());
		assertEquals(5, result.lostLevels().get(KossmanStatDefinitions.MINING.id()));
	}

	private static int totalLost(StatDeathPenaltyResult result) {
		return result.lostLevels().values().stream().mapToInt(Integer::intValue).sum();
	}

	private static int removableLevels(Map<StatId, Integer> levels) {
		return levels.values().stream()
				.mapToInt(level -> Math.max(0, level - KossmanBalance.current().deathPenalty().minimumRetainedStatLevel()))
				.sum();
	}

	@SafeVarargs
	private static Map<StatId, Integer> levels(Map.Entry<StatId, Integer>... entries) {
		Map<StatId, Integer> levels = new LinkedHashMap<>();
		for (Map.Entry<StatId, Integer> entry : entries) {
			levels.put(entry.getKey(), entry.getValue());
		}
		return levels;
	}

	private static Map.Entry<StatId, Integer> level(StatId statId, int level) {
		return Map.entry(statId, level);
	}

	private static final class AlwaysZeroRandom implements RandomGenerator {
		@Override
		public int nextInt(int bound) {
			return 0;
		}

		@Override
		public long nextLong() {
			return 0L;
		}

		@Override
		public boolean nextBoolean() {
			return false;
		}

		@Override
		public float nextFloat() {
			return 0.0F;
		}

		@Override
		public double nextDouble() {
			return 0.0D;
		}
	}
}
