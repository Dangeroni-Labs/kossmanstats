package net.infstudio.gokistats.core.progression;

public final class StatProgression {
	private static final double TIER_SPAN = 10.0D;

	private StatProgression() {
	}

	public static int upgradeCostForLevel(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("level must be non-negative");
		}

		double baseCost = Math.pow(level, 1.6D) + 6.0D + level;
		double multiplier = highLevelMultiplier(level + 1);
		return (int) Math.ceil(baseCost * multiplier);
	}

	private static double highLevelMultiplier(int nextLevel) {
		return 1.0D
				+ tierRamp(nextLevel, 10, 1.5D)
				+ tierRamp(nextLevel, 20, 4.0D)
				+ tierRamp(nextLevel, 30, 8.0D)
				+ tierRamp(nextLevel, 40, 14.0D);
	}

	private static double tierRamp(int nextLevel, int startLevel, double strength) {
		double progress = Math.min(1.0D, Math.max(0.0D, (nextLevel - startLevel) / TIER_SPAN));
		return strength * progress * progress;
	}
}
