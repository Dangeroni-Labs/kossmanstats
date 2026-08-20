package net.infstudio.gokistats.core.stat;

public final class SwordsmanshipStat {
	private SwordsmanshipStat() {
	}

	public static double bonusForLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.0895D) * 0.03D;
	}
}
