package net.infstudio.gokistats.client.gui;

import java.util.Optional;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.perk.KossmanStatPerks;
import net.infstudio.gokistats.core.perk.StatPerkDefinition;

final class StatPresentation {
	private StatPresentation() {
	}

	static String displayName(StatDefinition stat, int level) {
		return activePerk(stat, level)
				.map(StatPerkDefinition::masteryName)
				.orElse(stat.displayName());
	}

	static Optional<StatPerkDefinition> activePerk(StatDefinition stat, int level) {
		return KossmanBalance.current().isPerkUnlocked(stat, level)
				? KossmanStatPerks.forStat(stat)
				: Optional.empty();
	}
}
