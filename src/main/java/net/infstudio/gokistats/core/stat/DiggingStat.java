package net.infstudio.gokistats.core.stat;

public final class DiggingStat {
	private DiggingStat() {
	}

	public static double bonusForLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return Math.pow(level, 1.3D) * 0.01523D;
	}
}
