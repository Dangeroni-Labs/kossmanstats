package net.infstudio.gokistats.core;

public final class MiningStat {
	public static final String ID = "gokistats:mining";
	public static final int MAX_LEVEL = 25;

	private MiningStat() {
	}

	public static double bonusForLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.3D) * 0.01523D;
	}
}
