package net.infstudio.gokistats.client.gui;

import java.util.List;
import java.util.Locale;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.core.perk.KossmanStatPerks;
import net.infstudio.gokistats.core.progression.StatProgression;
import net.minecraft.network.chat.Component;

final class StatTooltipContent {
	private StatTooltipContent() {
	}

	static List<Component> forStat(StatDefinition stat, int level) {
		String displayName = StatPresentation.displayName(stat, level);
		if (!KossmanBalance.current().isEnabled(stat)) {
			return List.of(
					Component.literal(displayName),
					Component.literal("Disabled by server configuration."),
					Component.literal("Stored level: " + level)
			);
		}

		if (stat.equals(KossmanStatDefinitions.MINING)) {
			return miningTooltip(stat, level, displayName);
		}

		if (stat.equals(KossmanStatDefinitions.DIGGING)) {
			return appendPerkLine(
					stat,
					level,
					bonusTooltip(stat, level, displayName, "Increases digging speed with shovels.", "speed", currentLevel -> StatFormulas.toolSpeedBonus(stat, currentLevel), true)
			);
		}

		if (stat.equals(KossmanStatDefinitions.CHOPPING)) {
			return appendPerkLine(
					stat,
					level,
					bonusTooltip(stat, level, displayName, "Increases chopping speed with axes.", "speed", currentLevel -> StatFormulas.toolSpeedBonus(stat, currentLevel), true)
			);
		}

		if (stat.equals(KossmanStatDefinitions.TRIMMING)) {
			return bonusTooltip(stat, level, displayName, "Increases trimming speed with shears.", "speed", currentLevel -> StatFormulas.toolSpeedBonus(stat, currentLevel), true);
		}

		if (stat.equals(KossmanStatDefinitions.SWORDSMANSHIP)) {
			return bonusTooltip(stat, level, displayName, "Increases melee damage with swords.", "damage", StatFormulas::swordsmanshipDamageBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.PUGILISM)) {
			return bonusTooltip(stat, level, displayName, "Increases melee damage while unarmed.", "damage", StatFormulas::pugilismDamageBonus, false);
		}

		if (stat.equals(KossmanStatDefinitions.BOWMANSHIP)) {
			return bonusTooltip(stat, level, displayName, "Increases damage dealt by bow shots.", "bow damage", StatFormulas::bowmanshipDamageBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.HEALTH)) {
			return bonusTooltip(stat, level, displayName, "Increases maximum health.", "HP", StatFormulas::healthMaxHealthBonus, false);
		}

		if (stat.equals(KossmanStatDefinitions.PROTECTION)) {
			return reductionTooltip(stat, level, displayName, "Reduces melee damage from mobs.", StatFormulas::protectionDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.TEMPERING)) {
			return reductionTooltip(stat, level, displayName, "Reduces damage from fire and lava.", StatFormulas::temperingDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.TOUGH_SKIN)) {
			return reductionTooltip(stat, level, displayName, "Reduces damage from explosions.", StatFormulas::toughSkinDamageReduction);
		}

		if (stat.equals(KossmanStatDefinitions.FEATHER_FALL)) {
			return featherFallTooltip(stat, level);
		}

		if (stat.equals(KossmanStatDefinitions.LEAPER_H)) {
			return bonusTooltip(stat, level, displayName, "Increases forward sprint-jump distance.", "horizontal jump boost", StatFormulas::leaperHorizontalBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.LEAPER_V)) {
			return bonusTooltip(stat, level, displayName, "Increases sprint-jump height.", "jump height bonus", StatFormulas::leaperVerticalBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.CLIMBING)) {
			return bonusTooltip(stat, level, displayName, "Increases upward climbing speed on climbables.", "climbing speed", StatFormulas::climbingSpeedBonus, true);
		}

		if (stat.equals(KossmanStatDefinitions.STEADY_GUARD)) {
			return steadyGuardTooltip(stat, level);
		}

		if (stat.equals(KossmanStatDefinitions.REAPER)) {
			return reaperTooltip(stat, level);
		}

		if (stat.equals(KossmanStatDefinitions.ROLL)) {
			return rollTooltip(stat, level);
		}

		if (stat.equals(KossmanStatDefinitions.TREASURE_FINDER)) {
			return chanceTooltip(stat, level, displayName, "Chance to find extra treasure from configured blocks.", StatFormulas::treasureFinderProcChance, "extra treasure proc chance");
		}

		if (stat.equals(KossmanStatDefinitions.MINING_MAGICIAN)) {
			return chanceTooltip(stat, level, displayName, "Chance to conjure an extra configured reward from eligible ores.", StatFormulas::miningMagicianProcChance, "magician proc chance");
		}

		return List.of(
				Component.literal(displayName),
				Component.literal("No effect description available.")
		);
	}

	static List<Component> forDowngrade(StatDefinition stat, int level) {
		if (!KossmanBalance.current().isEnabled(stat)) {
			return List.of(
					Component.literal(stat.displayName()),
					Component.literal("Disabled by server configuration."),
					Component.literal("Downgrade unavailable.")
			);
		}

		if (level <= 0) {
			return List.of(
					Component.literal(stat.displayName()),
					Component.literal("Already at level 0."),
					Component.literal("No refund available.")
			);
		}

		return List.of(
				Component.literal("Downgrade to level " + (level - 1)),
				Component.literal(
						"Refund: "
								+ StatProgression.downgradeRefundForLevel(level)
								+ " XP ("
								+ formatNumber(KossmanBalance.current().downgradeRefundRate() * 100.0D)
								+ "%)"
				)
		);
	}

	private static List<Component> bonusTooltip(
			StatDefinition stat,
			int level,
			String displayName,
			String description,
			String unit,
			LevelBonus bonus,
			boolean percent
	) {
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal(description),
					Component.literal("Current: " + formatBonus(bonus.value(level), unit, percent)),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal(description),
				Component.literal("Current: " + formatBonus(bonus.value(level), unit, percent)),
				Component.literal("Next: " + formatBonus(bonus.value(level + 1), unit, percent))
		);
	}

	private static List<Component> reductionTooltip(
			StatDefinition stat,
			int level,
			String displayName,
			String description,
			LevelBonus reduction
	) {
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal(description),
					Component.literal("Current: " + formatReduction(reduction.value(level))),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal(description),
				Component.literal("Current: " + formatReduction(reduction.value(level))),
				Component.literal("Next: " + formatReduction(reduction.value(level + 1)))
		);
	}

	private static List<Component> featherFallTooltip(StatDefinition stat, int level) {
		String displayName = StatPresentation.displayName(stat, level);
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal("Reduces fall damage and increases safe fall distance."),
					Component.literal("Current: " + formatReduction(StatFormulas.featherFallDamageReduction(level))),
					Component.literal("Safe distance: +" + formatNumber(StatFormulas.featherFallSafeDistanceBonus(level)) + " blocks"),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal("Reduces fall damage and increases safe fall distance."),
				Component.literal("Current: " + formatReduction(StatFormulas.featherFallDamageReduction(level))),
				Component.literal("Next: " + formatReduction(StatFormulas.featherFallDamageReduction(level + 1))),
				Component.literal("Safe distance: +" + formatNumber(StatFormulas.featherFallSafeDistanceBonus(level)) + " blocks")
		);
	}

	private static List<Component> steadyGuardTooltip(StatDefinition stat, int level) {
		String displayName = StatPresentation.displayName(stat, level);
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal("Increases knockback resistance while actively blocking."),
					Component.literal("Current: +" + formatPercent(StatFormulas.steadyGuardKnockbackResistanceBonus(level)) + " knockback resistance while blocking"),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal("Increases knockback resistance while actively blocking."),
				Component.literal("Current: +" + formatPercent(StatFormulas.steadyGuardKnockbackResistanceBonus(level)) + " knockback resistance while blocking"),
				Component.literal("Next: +" + formatPercent(StatFormulas.steadyGuardKnockbackResistanceBonus(level + 1)) + " knockback resistance while blocking")
		);
	}

	private static List<Component> reaperTooltip(StatDefinition stat, int level) {
		String displayName = StatPresentation.displayName(stat, level);
		String capText = formatNumber(StatFormulas.reaperMaxTargetHealth());
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal("Chance to execute weakened non-player targets."),
					Component.literal("Current: " + formatPercent(StatFormulas.reaperChance(level)) + " chance below " + formatPercent(StatFormulas.reaperHealthThreshold(level)) + " target health"),
					Component.literal("Targets: non-player living targets up to " + capText + " max HP"),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal("Chance to execute weakened non-player targets."),
				Component.literal("Current: " + formatPercent(StatFormulas.reaperChance(level)) + " chance below " + formatPercent(StatFormulas.reaperHealthThreshold(level)) + " target health"),
				Component.literal("Next: " + formatPercent(StatFormulas.reaperChance(level + 1)) + " chance below " + formatPercent(StatFormulas.reaperHealthThreshold(level + 1)) + " target health"),
				Component.literal("Targets: non-player living targets up to " + capText + " max HP")
		);
	}

	private static List<Component> rollTooltip(StatDefinition stat, int level) {
		String displayName = StatPresentation.displayName(stat, level);
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal("Chance to fully evade eligible direct attacks."),
					Component.literal("Current: " + formatPercent(StatFormulas.rollEvadeChance(level)) + " evade chance"),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal("Chance to fully evade eligible direct attacks."),
				Component.literal("Current: " + formatPercent(StatFormulas.rollEvadeChance(level)) + " evade chance"),
				Component.literal("Next: " + formatPercent(StatFormulas.rollEvadeChance(level + 1)) + " evade chance")
		);
	}

	private static List<Component> chanceTooltip(
			StatDefinition stat,
			int level,
			String displayName,
			String description,
			LevelBonus chance,
			String label
	) {
		if (level >= KossmanBalance.current().maxStatLevel()) {
			return List.of(
					Component.literal(displayName),
					Component.literal(description),
					Component.literal("Current: " + formatPercent(chance.value(level)) + " " + label),
					Component.literal("Next: max level")
			);
		}

		return List.of(
				Component.literal(displayName),
				Component.literal(description),
				Component.literal("Current: " + formatPercent(chance.value(level)) + " " + label),
				Component.literal("Next: " + formatPercent(chance.value(level + 1)) + " " + label)
		);
	}

	private static List<Component> miningTooltip(StatDefinition stat, int level, String displayName) {
		List<Component> tooltip = bonusTooltip(
				stat,
				level,
				displayName,
				"Increases mining speed with pickaxes.",
				"speed",
				currentLevel -> StatFormulas.toolSpeedBonus(stat, currentLevel),
				true
		);
		return appendPerkLine(stat, level, tooltip);
	}

	private static List<Component> appendPerkLine(StatDefinition stat, int level, List<Component> tooltip) {
		return KossmanStatPerks.forStat(stat)
				.map(perk -> {
					java.util.ArrayList<Component> lines = new java.util.ArrayList<>(tooltip);
					if (KossmanBalance.current().isPerkUnlocked(stat, level)) {
						lines.add(Component.literal("Perk: " + perk.description()));
					} else if (KossmanBalance.current().isPerkEnabled(stat)) {
						lines.add(Component.literal("Perk unlocks at Lv. " + KossmanBalance.current().perkUnlockLevel(stat)));
					}
					return List.copyOf(lines);
				})
				.orElse(tooltip);
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

	private static String formatPercent(double value) {
		return formatNumber(Math.max(0.0D, value) * 100.0D) + "%";
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
