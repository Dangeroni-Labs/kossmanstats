package net.infstudio.gokistats.core.formula;

public final class StatFormulas {
	private static final int MAX_EFFECT_LEVEL = 50;
	private static final double TOOL_SPEED_MAX_BONUS = 0.65D;
	private static final double TOOL_SPEED_EXPONENT = 1.163345D;
	private static final double SWORDSMANSHIP_MAX_DAMAGE_BONUS = 0.35D;
	private static final double SWORDSMANSHIP_EXPONENT = 1.1D;
	private static final double PUGILISM_MAX_DAMAGE_BONUS = 4.0D;
	private static final double PUGILISM_EXPONENT = 1.15D;
	private static final double HEALTH_BONUS_PER_LEVEL = 0.4D;

	private StatFormulas() {
	}

	public static double toolSpeedBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return TOOL_SPEED_MAX_BONUS * Math.pow(normalizedLevel(level), TOOL_SPEED_EXPONENT);
	}

	public static double swordsmanshipDamageBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return SWORDSMANSHIP_MAX_DAMAGE_BONUS * Math.pow(normalizedLevel(level), SWORDSMANSHIP_EXPONENT);
	}

	public static double pugilismDamageBonus(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return PUGILISM_MAX_DAMAGE_BONUS * Math.pow(normalizedLevel(level), PUGILISM_EXPONENT);
	}

	public static double healthMaxHealthBonus(int level) {
		return Math.max(0, level) * HEALTH_BONUS_PER_LEVEL;
	}

	private static double normalizedLevel(int level) {
		return Math.min(level, MAX_EFFECT_LEVEL) / (double) MAX_EFFECT_LEVEL;
	}
}
