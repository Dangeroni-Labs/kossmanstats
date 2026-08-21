package net.infstudio.gokistats.core.formula;

import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;

public final class StatFormulas {
	private static final double TOOL_SPEED_MAX_BONUS = 0.65D;
	private static final double TOOL_SPEED_EXPONENT = 1.163345D;
	private static final double SWORDSMANSHIP_MAX_DAMAGE_BONUS = 0.35D;
	private static final double SWORDSMANSHIP_EXPONENT = 1.1D;
	private static final double PUGILISM_MAX_DAMAGE_BONUS = 4.0D;
	private static final double PUGILISM_EXPONENT = 1.15D;
	private static final double BOWMANSHIP_MAX_DAMAGE_BONUS = 0.30D;
	private static final double BOWMANSHIP_EXPONENT = 1.10D;
	private static final double HEALTH_BONUS_PER_LEVEL = 0.4D;
	private static final double PROTECTION_MAX_REDUCTION = 0.35D;
	private static final double TEMPERING_MAX_REDUCTION = 0.50D;
	private static final double FEATHER_FALL_MAX_REDUCTION = 0.60D;
	private static final double PROTECTION_CURVE_EXPONENT = 1.10D;
	private static final double SPECIALIZED_CURVE_EXPONENT = 1.14D;
	private static final double FEATHER_FALL_CURVE_EXPONENT = 1.11D;
	private static final double FEATHER_FALL_SAFE_DISTANCE_BONUS_PER_LEVEL = 0.1D;
	private static final double MAX_DAMAGE_REDUCTION = 0.95D;
	private static final double STEADY_GUARD_MAX_KNOCKBACK_RESISTANCE = 0.60D;
	private static final double STEADY_GUARD_EXPONENT = 1.15D;
	private static final double REAPER_MAX_CHANCE = 0.12D;
	private static final double REAPER_CHANCE_EXPONENT = 1.12D;
	private static final double REAPER_MAX_HEALTH_THRESHOLD = 0.30D;
	private static final double REAPER_THRESHOLD_EXPONENT = 1.08D;
	private static final double REAPER_MAX_TARGET_HEALTH = 40.0D;
	private static final double ROLL_MAX_EVADE_CHANCE = 0.18D;
	private static final double ROLL_EVADE_EXPONENT = 1.18D;
	private static final double LEAPER_HORIZONTAL_MAX_BONUS = 0.30D;
	private static final double LEAPER_VERTICAL_MAX_BONUS = 0.18D;
	private static final double CLIMBING_MAX_BONUS = 0.50D;
	private static final double LEAPER_HORIZONTAL_EXPONENT = 1.10D;
	private static final double LEAPER_VERTICAL_EXPONENT = 1.12D;
	private static final double CLIMBING_EXPONENT = 1.08D;
	private static final double TREASURE_FINDER_MAX_PROC_CHANCE = 0.12D;
	private static final double TREASURE_FINDER_EXPONENT = 1.16D;
	private static final double MINING_MAGICIAN_MAX_PROC_CHANCE = 0.16D;
	private static final double MINING_MAGICIAN_EXPONENT = 1.14D;

	private StatFormulas() {
	}

	public static double toolSpeedBonus(StatDefinition stat, int level) {
		return scaledEffect(stat, TOOL_SPEED_MAX_BONUS * Math.pow(normalizedLevel(level), TOOL_SPEED_EXPONENT));
	}

	public static double swordsmanshipDamageBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.SWORDSMANSHIP, SWORDSMANSHIP_MAX_DAMAGE_BONUS * Math.pow(normalizedLevel(level), SWORDSMANSHIP_EXPONENT));
	}

	public static double pugilismDamageBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.PUGILISM, PUGILISM_MAX_DAMAGE_BONUS * Math.pow(normalizedLevel(level), PUGILISM_EXPONENT));
	}

	public static double bowmanshipDamageBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.BOWMANSHIP, BOWMANSHIP_MAX_DAMAGE_BONUS * Math.pow(normalizedLevel(level), BOWMANSHIP_EXPONENT));
	}

	public static double healthMaxHealthBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.HEALTH, cappedLevel(level) * HEALTH_BONUS_PER_LEVEL);
	}

	public static double protectionDamageReduction(int level) {
		return scaledEffect(KossmanStatDefinitions.PROTECTION, defensiveDamageReduction(level, PROTECTION_MAX_REDUCTION, PROTECTION_CURVE_EXPONENT));
	}

	public static double temperingDamageReduction(int level) {
		return scaledEffect(KossmanStatDefinitions.TEMPERING, defensiveDamageReduction(level, TEMPERING_MAX_REDUCTION, SPECIALIZED_CURVE_EXPONENT));
	}

	public static double toughSkinDamageReduction(int level) {
		return scaledEffect(KossmanStatDefinitions.TOUGH_SKIN, defensiveDamageReduction(level, TEMPERING_MAX_REDUCTION, SPECIALIZED_CURVE_EXPONENT));
	}

	public static double featherFallDamageReduction(int level) {
		return scaledEffect(KossmanStatDefinitions.FEATHER_FALL, defensiveDamageReduction(level, FEATHER_FALL_MAX_REDUCTION, FEATHER_FALL_CURVE_EXPONENT));
	}

	public static double featherFallSafeDistanceBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.FEATHER_FALL, cappedLevel(level) * FEATHER_FALL_SAFE_DISTANCE_BONUS_PER_LEVEL);
	}

	public static double steadyGuardKnockbackResistanceBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.STEADY_GUARD, boundedCurve(level, STEADY_GUARD_MAX_KNOCKBACK_RESISTANCE, STEADY_GUARD_EXPONENT));
	}

	public static double reaperChance(int level) {
		return scaledEffect(KossmanStatDefinitions.REAPER, boundedCurve(level, REAPER_MAX_CHANCE, REAPER_CHANCE_EXPONENT));
	}

	public static double reaperHealthThreshold(int level) {
		return scaledEffect(KossmanStatDefinitions.REAPER, boundedCurve(level, REAPER_MAX_HEALTH_THRESHOLD, REAPER_THRESHOLD_EXPONENT));
	}

	public static double reaperMaxTargetHealth() {
		return scaledEffect(KossmanStatDefinitions.REAPER, REAPER_MAX_TARGET_HEALTH);
	}

	public static double rollEvadeChance(int level) {
		return scaledEffect(KossmanStatDefinitions.ROLL, boundedCurve(level, ROLL_MAX_EVADE_CHANCE, ROLL_EVADE_EXPONENT));
	}

	public static double leaperHorizontalBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.LEAPER_H, boundedCurve(level, LEAPER_HORIZONTAL_MAX_BONUS, LEAPER_HORIZONTAL_EXPONENT));
	}

	public static double leaperVerticalBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.LEAPER_V, boundedCurve(level, LEAPER_VERTICAL_MAX_BONUS, LEAPER_VERTICAL_EXPONENT));
	}

	public static double climbingSpeedBonus(int level) {
		return scaledEffect(KossmanStatDefinitions.CLIMBING, boundedCurve(level, CLIMBING_MAX_BONUS, CLIMBING_EXPONENT));
	}

	public static double treasureFinderProcChance(int level) {
		return scaledEffect(KossmanStatDefinitions.TREASURE_FINDER, boundedCurve(level, TREASURE_FINDER_MAX_PROC_CHANCE, TREASURE_FINDER_EXPONENT));
	}

	public static double miningMagicianProcChance(int level) {
		return scaledEffect(KossmanStatDefinitions.MINING_MAGICIAN, boundedCurve(level, MINING_MAGICIAN_MAX_PROC_CHANCE, MINING_MAGICIAN_EXPONENT));
	}

	public static float applyDamageReduction(float amount, double reduction) {
		if (amount <= 0.0F || reduction <= 0.0D) {
			return amount;
		}

		double multiplier = Math.max(0.0D, 1.0D - Math.min(reduction, MAX_DAMAGE_REDUCTION));
		return (float) (amount * multiplier);
	}

	private static double defensiveDamageReduction(int level, double levelCapReduction, double curveExponent) {
		return boundedCurve(level, levelCapReduction, curveExponent, MAX_DAMAGE_REDUCTION);
	}

	private static double boundedCurve(int level, double maxValue, double exponent) {
		return boundedCurve(level, maxValue, exponent, maxValue);
	}

	private static double boundedCurve(int level, double maxValue, double exponent, double upperBound) {
		if (level <= 0) {
			return 0.0D;
		}

		int maxLevel = Math.max(1, KossmanBalance.current().maxStatLevel());
		double normalized = Math.min(level, maxLevel) / (double) maxLevel;
		return Math.min(upperBound, maxValue * Math.pow(normalized, exponent));
	}

	private static double normalizedLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		int maxLevel = Math.max(1, KossmanBalance.current().maxStatLevel());
		return Math.min(level, maxLevel) / (double) maxLevel;
	}

	private static int cappedLevel(int level) {
		return Math.min(Math.max(0, level), Math.max(1, KossmanBalance.current().maxStatLevel()));
	}

	private static double scaledEffect(StatDefinition stat, double value) {
		if (value <= 0.0D) {
			return 0.0D;
		}

		return value * KossmanBalance.current().effectMultiplier(stat);
	}
}
