package net.infstudio.gokistats.core.perk;

import java.util.Map;
import java.util.Optional;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;

public final class KossmanStatPerks {
	public static final StatPerkDefinition MASTER_MINER = new StatPerkDefinition(
			KossmanStatDefinitions.MINING,
			"Master Miner",
			"Metal ores drop smelted output.",
			45
	);
	public static final StatPerkDefinition MOMENTUM = new StatPerkDefinition(
			KossmanStatDefinitions.DIGGING,
			"Momentum",
			"Consecutive digging increases digging speed.",
			25
	);
	public static final StatPerkDefinition LUMBERJACK = new StatPerkDefinition(
			KossmanStatDefinitions.CHOPPING,
			"Lumberjack",
			"Chopping one log can fell the whole tree.",
			45
	);
	private static final Map<StatDefinition, StatPerkDefinition> BY_STAT = Map.of(
			MASTER_MINER.stat(),
			MASTER_MINER,
			LUMBERJACK.stat(),
			LUMBERJACK,
			MOMENTUM.stat(),
			MOMENTUM
	);

	private KossmanStatPerks() {
	}

	public static Optional<StatPerkDefinition> forStat(StatDefinition stat) {
		return Optional.ofNullable(BY_STAT.get(stat));
	}
}
