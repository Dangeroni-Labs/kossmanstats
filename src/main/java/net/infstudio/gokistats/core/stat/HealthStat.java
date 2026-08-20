package net.infstudio.gokistats.core.stat;

public final class HealthStat {
	private HealthStat() {
	}

	public static double maxHealthBonusForLevel(int level) {
		return Math.max(0, level);
	}
}
