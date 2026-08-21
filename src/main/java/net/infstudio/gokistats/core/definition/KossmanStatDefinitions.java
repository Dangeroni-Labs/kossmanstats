package net.infstudio.gokistats.core.definition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class KossmanStatDefinitions {
	private static final int MAX_LEVEL = 50;

	public static final StatDefinition MINING = new StatDefinition(
			new StatId("gokistats:mining"),
			"mining",
			"Mining",
			MAX_LEVEL
	);

	public static final StatDefinition HEALTH = new StatDefinition(
			new StatId("gokistats:health"),
			"health",
			"Health",
			MAX_LEVEL
	);

	public static final StatDefinition DIGGING = new StatDefinition(
			new StatId("gokistats:digging"),
			"digging",
			"Digging",
			MAX_LEVEL
	);

	public static final StatDefinition CHOPPING = new StatDefinition(
			new StatId("gokistats:chopping"),
			"chopping",
			"Chopping",
			MAX_LEVEL
	);

	public static final StatDefinition TRIMMING = new StatDefinition(
			new StatId("gokistats:trimming"),
			"trimming",
			"Trimming",
			MAX_LEVEL
	);

	public static final StatDefinition SWORDSMANSHIP = new StatDefinition(
			new StatId("gokistats:swordsmanship"),
			"swordsmanship",
			"Swordsmanship",
			MAX_LEVEL
	);

	public static final StatDefinition PUGILISM = new StatDefinition(
			new StatId("gokistats:pugilism"),
			"pugilism",
			"Pugilism",
			MAX_LEVEL
	);

	public static final StatDefinition PROTECTION = new StatDefinition(
			new StatId("gokistats:protection"),
			"protection",
			"Protection",
			MAX_LEVEL
	);

	public static final StatDefinition TEMPERING = new StatDefinition(
			new StatId("gokistats:tempering"),
			"tempering",
			"Tempering",
			MAX_LEVEL
	);

	public static final StatDefinition TOUGH_SKIN = new StatDefinition(
			new StatId("gokistats:tough_skin"),
			"tough_skin",
			"Tough Skin",
			MAX_LEVEL
	);

	public static final StatDefinition FEATHER_FALL = new StatDefinition(
			new StatId("gokistats:feather_fall"),
			"feather_fall",
			"Feather Fall",
			MAX_LEVEL
	);

	public static final StatDefinition LEAPER_H = new StatDefinition(
			new StatId("gokistats:leaper_h"),
			"leaper_h",
			"Leaper H",
			MAX_LEVEL
	);

	public static final StatDefinition LEAPER_V = new StatDefinition(
			new StatId("gokistats:leaper_v"),
			"leaper_v",
			"Leaper V",
			MAX_LEVEL
	);

	public static final StatDefinition CLIMBING = new StatDefinition(
			new StatId("gokistats:climbing"),
			"climbing",
			"Climbing",
			MAX_LEVEL
	);

	public static final StatDefinition STEADY_GUARD = new StatDefinition(
			new StatId("gokistats:steady_guard"),
			"steady_guard",
			"Steady Guard",
			MAX_LEVEL
	);

	public static final StatDefinition REAPER = new StatDefinition(
			new StatId("gokistats:reaper"),
			"reaper",
			"Reaper",
			MAX_LEVEL
	);

	public static final StatDefinition ROLL = new StatDefinition(
			new StatId("gokistats:roll"),
			"roll",
			"Roll",
			MAX_LEVEL
	);

	public static final List<StatDefinition> ALL = List.of(
			MINING,
			HEALTH,
			DIGGING,
			CHOPPING,
			TRIMMING,
			SWORDSMANSHIP,
			PUGILISM,
			PROTECTION,
			TEMPERING,
			TOUGH_SKIN,
			FEATHER_FALL,
			LEAPER_H,
			LEAPER_V,
			CLIMBING,
			STEADY_GUARD,
			REAPER,
			ROLL
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
