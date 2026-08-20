package net.infstudio.gokistats.core;

public record UpgradeResult(boolean upgraded, int level, int cost, String message) {
	public static UpgradeResult success(int level, int cost) {
		return new UpgradeResult(true, level, cost, "Mining upgraded to level " + level + ".");
	}

	public static UpgradeResult failure(int level, int cost, String message) {
		return new UpgradeResult(false, level, cost, message);
	}
}
