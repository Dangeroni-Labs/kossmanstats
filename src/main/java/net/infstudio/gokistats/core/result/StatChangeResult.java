package net.infstudio.gokistats.core.result;

import net.infstudio.gokistats.core.definition.StatDefinition;

public record StatChangeResult(boolean changed, int previousLevel, int currentLevel, int xpAmount, String message) {
	public static StatChangeResult upgradeSuccess(StatDefinition stat, int previousLevel, int currentLevel, int cost) {
		return new StatChangeResult(
				true,
				previousLevel,
				currentLevel,
				cost,
				stat.displayName() + " upgraded to level " + currentLevel + "."
		);
	}

	public static StatChangeResult downgradeSuccess(StatDefinition stat, int previousLevel, int currentLevel, int refund) {
		return new StatChangeResult(
				true,
				previousLevel,
				currentLevel,
				refund,
				stat.displayName() + " downgraded to level " + currentLevel + "."
		);
	}

	public static StatChangeResult failure(int level, int xpAmount, String message) {
		return new StatChangeResult(false, level, level, xpAmount, message);
	}
}
