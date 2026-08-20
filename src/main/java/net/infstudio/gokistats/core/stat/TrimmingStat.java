package net.infstudio.gokistats.core.stat;

public final class TrimmingStat {
	private TrimmingStat() {
	}

	public static double bonusForLevel(int level) {
		if (level <= 0) {
			return 0.0D;
		}

		return level * 0.1D;
	}
}
