package net.infstudio.gokistats.core;

public final class StatProgression {
	private StatProgression() {
	}

	public static int upgradeCostForLevel(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("level must be non-negative");
		}

		return (int) (Math.pow(level, 1.6D) + 6.0D + level);
	}
}
