package net.infstudio.gokistats.core.progression;

public final class StatProgression {
	private static final double BASE_OFFSET = 8.0D;
	private static final double LINEAR_SCALE = 1.0D;
	private static final double QUADRATIC_SCALE = 0.32D;
	private static final double CUBIC_SCALE = 0.003D;
	private static final double MIDGAME_RAMP_SCALE = 1.2D;
	private static final double LATEGAME_RAMP_SCALE = 4.0D;
	private static final double ENDGAME_RAMP_SCALE = 40.0D;

	private StatProgression() {
	}

	public static int upgradeCostForLevel(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("level must be non-negative");
		}

		double value = level;
		double cost = BASE_OFFSET
				+ LINEAR_SCALE * value
				+ QUADRATIC_SCALE * value * value
				+ CUBIC_SCALE * value * value * value
				+ MIDGAME_RAMP_SCALE * squaredRamp(value, 25.0D)
				+ LATEGAME_RAMP_SCALE * squaredRamp(value, 35.0D)
				+ ENDGAME_RAMP_SCALE * squaredRamp(value, 45.0D);
		return (int) Math.ceil(cost);
	}

	private static double squaredRamp(double level, double startLevel) {
		double delta = Math.max(0.0D, level - startLevel);
		return delta * delta;
	}
}
