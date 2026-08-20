package net.infstudio.gokistats.core.result;

import net.infstudio.gokistats.core.definition.StatDefinition;

public record UpgradeResult(boolean upgraded, int level, int cost, String message) {
	public static UpgradeResult success(StatDefinition stat, int level, int cost) {
		return new UpgradeResult(true, level, cost, stat.displayName() + " upgraded to level " + level + ".");
	}

	public static UpgradeResult failure(int level, int cost, String message) {
		return new UpgradeResult(false, level, cost, message);
	}
}
