package net.infstudio.gokistats.core.definition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class KossmanStatDefinitions {
	public static final StatDefinition MINING = new StatDefinition(
			new StatId("gokistats:mining"),
			"mining",
			"Mining"
	);

	public static final StatDefinition HEALTH = new StatDefinition(
			new StatId("gokistats:health"),
			"health",
			"Health"
	);

	public static final StatDefinition DIGGING = new StatDefinition(
			new StatId("gokistats:digging"),
			"digging",
			"Digging"
	);

	public static final StatDefinition CHOPPING = new StatDefinition(
			new StatId("gokistats:chopping"),
			"chopping",
			"Chopping"
	);

	public static final StatDefinition TRIMMING = new StatDefinition(
			new StatId("gokistats:trimming"),
			"trimming",
			"Trimming"
	);

	public static final StatDefinition SWORDSMANSHIP = new StatDefinition(
			new StatId("gokistats:swordsmanship"),
			"swordsmanship",
			"Swordsmanship"
	);

	public static final StatDefinition PUGILISM = new StatDefinition(
			new StatId("gokistats:pugilism"),
			"pugilism",
			"Pugilism"
	);

	public static final StatDefinition BOWMANSHIP = new StatDefinition(
			new StatId("gokistats:bowmanship"),
			"bowmanship",
			"Bowmanship"
	);

	public static final StatDefinition PROTECTION = new StatDefinition(
			new StatId("gokistats:protection"),
			"protection",
			"Protection"
	);

	public static final StatDefinition TEMPERING = new StatDefinition(
			new StatId("gokistats:tempering"),
			"tempering",
			"Tempering"
	);

	public static final StatDefinition TOUGH_SKIN = new StatDefinition(
			new StatId("gokistats:tough_skin"),
			"tough_skin",
			"Tough Skin"
	);

	public static final StatDefinition FEATHER_FALL = new StatDefinition(
			new StatId("gokistats:feather_fall"),
			"feather_fall",
			"Feather Fall"
	);

	public static final StatDefinition LEAPER_H = new StatDefinition(
			new StatId("gokistats:leaper_h"),
			"leaper_h",
			"Leaper H"
	);

	public static final StatDefinition LEAPER_V = new StatDefinition(
			new StatId("gokistats:leaper_v"),
			"leaper_v",
			"Leaper V"
	);

	public static final StatDefinition CLIMBING = new StatDefinition(
			new StatId("gokistats:climbing"),
			"climbing",
			"Climbing"
	);

	public static final StatDefinition STEADY_GUARD = new StatDefinition(
			new StatId("gokistats:steady_guard"),
			"steady_guard",
			"Steady Guard"
	);

	public static final StatDefinition REAPER = new StatDefinition(
			new StatId("gokistats:reaper"),
			"reaper",
			"Reaper"
	);

	public static final StatDefinition ROLL = new StatDefinition(
			new StatId("gokistats:roll"),
			"roll",
			"Roll"
	);

	public static final StatDefinition TREASURE_FINDER = new StatDefinition(
			new StatId("gokistats:treasure_finder"),
			"treasure_finder",
			"Treasure Finder"
	);

	public static final StatDefinition MINING_MAGICIAN = new StatDefinition(
			new StatId("gokistats:mining_magician"),
			"mining_magician",
			"Mining Magician"
	);

	public static final List<StatDefinition> ALL = List.of(
			MINING,
			HEALTH,
			DIGGING,
			CHOPPING,
			TRIMMING,
			SWORDSMANSHIP,
			PUGILISM,
			BOWMANSHIP,
			PROTECTION,
			TEMPERING,
			TOUGH_SKIN,
			FEATHER_FALL,
			LEAPER_H,
			LEAPER_V,
			CLIMBING,
			STEADY_GUARD,
			REAPER,
			ROLL,
			TREASURE_FINDER,
			MINING_MAGICIAN
	);
	private static final Map<String, StatDefinition> BY_COMMAND_NAME = ALL.stream()
			.collect(Collectors.toUnmodifiableMap(StatDefinition::commandName, stat -> stat));
	private static final Map<StatId, StatDefinition> BY_ID = ALL.stream()
			.collect(Collectors.toUnmodifiableMap(StatDefinition::id, stat -> stat));

	private KossmanStatDefinitions() {
	}

	public static Optional<StatDefinition> byCommandName(String commandName) {
		return Optional.ofNullable(BY_COMMAND_NAME.get(commandName));
	}

	public static Optional<StatDefinition> byId(StatId id) {
		return Optional.ofNullable(BY_ID.get(id));
	}
}
