package net.infstudio.gokistats.core.stat;

public final class PugilismStat {
	private PugilismStat() {
	}

	public static double bonusForLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.03D) * 0.1816D;
	}
}
