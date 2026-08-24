package net.infstudio.gokistats.core.config;

import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.perk.KossmanStatPerks;

public record KossmanStatTuning(boolean enabled, double effectMultiplier, boolean perkEnabled, int perkUnlockLevel) {
	public static final KossmanStatTuning DEFAULT = new KossmanStatTuning(true, 1.0D, true, 0);

	public static KossmanStatTuning defaultsFor(StatDefinition stat) {
		int unlockLevel = KossmanStatPerks.forStat(stat)
				.map(perk -> perk.defaultUnlockLevel())
				.orElse(0);
		return new KossmanStatTuning(true, 1.0D, true, unlockLevel);
	}
}
