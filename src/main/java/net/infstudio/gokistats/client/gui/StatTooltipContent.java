package net.infstudio.gokistats.client.gui;

import java.util.List;
import java.util.Locale;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.minecraft.network.chat.Component;

final class StatTooltipContent {
	private StatTooltipContent() {
	}

	static List<Component> forStat(StatDefinition stat, int level) {
		if (stat.equals(KossmanStatDefinitions.MINING)) {
			return bonusTooltip(stat, level, "Increases mining speed with pickaxes.", "speed", StatFormulas::toolSpeedBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.DIGGING)) {
			return bonusTooltip(stat, level, "Increases digging speed with shovels.", "speed", StatFormulas::toolSpeedBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.CHOPPING)) {
			return bonusTooltip(stat, level, "Increases chopping speed with axes.", "speed", StatFormulas::toolSpeedBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.TRIMMING)) {
			return bonusTooltip(stat, level, "Increases trimming speed with shears.", "speed", StatFormulas::toolSpeedBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.SWORDSMANSHIP)) {
			return bonusTooltip(stat, level, "Increases melee damage with swords.", "damage", StatFormulas::swordsmanshipDamageBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.PUGILISM)) {
			return bonusTooltip(stat, level, "Increases melee damage while unarmed.", "damage", StatFormulas::pugilismDamageBonus, false);
		}

		if (stat.equals(KossmanStatDefinitions.HEALTH)) {
			return bonusTooltip(stat, level, "Increases maximum health.", "HP", StatFormulas::healthMaxHealthBonus, false);
		}

		if (stat.equals(KossmanStatDefinitions.PROTECTION)) {
			return reductionTooltip(stat, level, "Reduces melee damage from mobs.", StatFormulas::protectionDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.TEMPERING)) {
			return reductionTooltip(stat, level, "Reduces damage from fire and lava.", StatFormulas::specializedDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.TOUGH_SKIN)) {
			return reductionTooltip(stat, level, "Reduces damage from explosions.", StatFormulas::specializedDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.FEATHER_FALL)) {
			return featherFallTooltip(stat, level);
		}

		return List.of(
				Component.literal(stat.displayName()),
				Component.literal("No effect description available.")
		);
	}

	private static List<Component> bonusTooltip(
			StatDefinition stat,
			int level,
			String description,
			String unit,
			LevelBonus bonus,
			boolean percent
	) {
		if (level >= stat.maxLevel()) {
			return List.of(
					Component.literal(stat.displayName()),
					Component.literal(description),
					Component.literal("Current: " + formatBonus(bonus.value(level), unit, percent)),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(stat.displayName()),
				Component.literal(description),
				Component.literal("Current: " + formatBonus(bonus.value(level), unit, percent)),
				Component.literal("Next: " + formatBonus(bonus.value(level + 1), unit, percent))
		);
	}

	private static List<Component> reductionTooltip(
			StatDefinition stat,
			int level,
			String description,
			LevelBonus reduction
	) {
		if (level >= stat.maxLevel()) {
			return List.of(
					Component.literal(stat.displayName()),
					Component.literal(description),
					Component.literal("Current: " + formatReduction(reduction.value(level))),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(stat.displayName()),
				Component.literal(description),
				Component.literal("Current: " + formatReduction(reduction.value(level))),
				Component.literal("Next: " + formatReduction(reduction.value(level + 1)))
		);
	}

	private static List<Component> featherFallTooltip(StatDefinition stat, int level) {
		if (level >= stat.maxLevel()) {
			return List.of(
					Component.literal(stat.displayName()),
					Component.literal("Reduces fall damage and increases safe fall distance."),
					Component.literal("Current: " + formatReduction(StatFormulas.specializedDamageReduction(level))),
					Component.literal("Safe distance: +" + formatNumber(StatFormulas.featherFallSafeDistanceBonus(level)) + " blocks"),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(stat.displayName()),
				Component.literal("Reduces fall damage and increases safe fall distance."),
				Component.literal("Current: " + formatReduction(StatFormulas.specializedDamageReduction(level))),
				Component.literal("Next: " + formatReduction(StatFormulas.specializedDamageReduction(level + 1))),
				Component.literal("Safe distance: +" + formatNumber(StatFormulas.featherFallSafeDistanceBonus(level)) + " blocks")
		);
	}

	private static String formatBonus(double value, String unit, boolean percent) {
		if (percent) {
			return "+" + formatNumber(value * 100.0D) + "% " + unit;
		}

		return "+" + formatNumber(value) + " " + unit;
	}

	private static String formatReduction(double value) {
		return "-" + formatNumber(Math.min(1.0D, Math.max(0.0D, value)) * 100.0D) + "% damage";
	}

	private static String formatNumber(double value) {
		if (Math.abs(value - Math.rint(value)) < 0.05D) {
			return String.format(Locale.ROOT, "%.0f", value);
		}

		return String.format(Locale.ROOT, "%.1f", value);
	}

	@FunctionalInterface
	private interface LevelBonus {
		double value(int level);
	}
}
