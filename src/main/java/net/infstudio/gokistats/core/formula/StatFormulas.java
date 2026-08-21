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
	private static final int MAX_DEFENSIVE_LEVEL = 50;
	private static final double PROTECTION_MAX_REDUCTION = 0.35D;
	private static final double TEMPERING_MAX_REDUCTION = 0.50D;
	private static final double FEATHER_FALL_MAX_REDUCTION = 0.60D;
	private static final double PROTECTION_CURVE_EXPONENT = 1.10D;
	private static final double SPECIALIZED_CURVE_EXPONENT = 1.14D;
	private static final double FEATHER_FALL_CURVE_EXPONENT = 1.11D;
	private static final double FEATHER_FALL_SAFE_DISTANCE_BONUS_PER_LEVEL = 0.1D;
	private static final double MAX_DAMAGE_REDUCTION = 0.95D;

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

	public static double protectionDamageReduction(int level) {
		return defensiveDamageReduction(level, PROTECTION_MAX_REDUCTION, PROTECTION_CURVE_EXPONENT);
	}

	public static double temperingDamageReduction(int level) {
		return defensiveDamageReduction(level, TEMPERING_MAX_REDUCTION, SPECIALIZED_CURVE_EXPONENT);
	}

	public static double toughSkinDamageReduction(int level) {
		return defensiveDamageReduction(level, TEMPERING_MAX_REDUCTION, SPECIALIZED_CURVE_EXPONENT);
	}

	public static double featherFallDamageReduction(int level) {
		return defensiveDamageReduction(level, FEATHER_FALL_MAX_REDUCTION, FEATHER_FALL_CURVE_EXPONENT);
	}

	public static double featherFallSafeDistanceBonus(int level) {
		return Math.max(0, level) * FEATHER_FALL_SAFE_DISTANCE_BONUS_PER_LEVEL;
	}

	public static float applyDamageReduction(float amount, double reduction) {
		if (amount <= 0.0F || reduction <= 0.0D) {
			return amount;
		}

		double multiplier = Math.max(0.0D, 1.0D - Math.min(reduction, MAX_DAMAGE_REDUCTION));
		return (float) (amount * multiplier);
	}

	private static double defensiveDamageReduction(int level, double levelCapReduction, double curveExponent) {
		if (level <= 0) {
			return 0.0D;
		}

		double normalized = Math.min(level, MAX_DEFENSIVE_LEVEL) / (double) MAX_DEFENSIVE_LEVEL;
		return Math.min(MAX_DAMAGE_REDUCTION, levelCapReduction * Math.pow(normalized, curveExponent));
	}

	private static double normalizedLevel(int level) {
		return Math.min(level, MAX_EFFECT_LEVEL) / (double) MAX_EFFECT_LEVEL;
	}
}
