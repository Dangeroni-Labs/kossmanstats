package net.infstudio.gokistats.core.formula;

public final class StatFormulas {
	private StatFormulas() {
	}

	public static double legacyToolSpeedBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.3D) * 0.01523D;
	}

	public static double trimmingToolSpeedBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return level * 0.1D;
	}

	public static double swordsmanshipDamageBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.0895D) * 0.03D;
	}

	public static double pugilismDamageBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.03D) * 0.1816D;
	}

	public static double healthMaxHealthBonus(int level) {
		return Math.max(0, level);
	}
}
