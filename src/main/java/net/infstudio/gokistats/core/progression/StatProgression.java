package net.infstudio.gokistats.core.progression;

import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.config.KossmanXpTuning;

public final class StatProgression {
	private StatProgression() {
	}

	public static int upgradeCostForLevel(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("level must be non-negative");
		}

		KossmanXpTuning tuning = KossmanBalance.current().xp();
		double value = level;
		double cost = tuning.baseOffset()
				+ tuning.linearScale() * value
				+ tuning.quadraticScale() * value * value
				+ tuning.cubicScale() * value * value * value
				+ tuning.midgameRampScale() * squaredRamp(value, tuning.midgameRampStart())
				+ tuning.lategameRampScale() * squaredRamp(value, tuning.lategameRampStart())
				+ tuning.endgameRampScale() * squaredRamp(value, tuning.endgameRampStart());
		cost *= tuning.costMultiplier();
		return (int) Math.ceil(cost);
	}

	public static int downgradeRefundForLevel(int currentLevel) {
		if (currentLevel <= 0) {
			return 0;
		}

		return (int) Math.floor(upgradeCostForLevel(currentLevel - 1) * KossmanBalance.current().downgradeRefundRate());
	}

	private static double squaredRamp(double level, double startLevel) {
		double delta = Math.max(0.0D, level - startLevel);
		return delta * delta;
	}
}
