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

	public static final List<StatDefinition> ALL = List.of(
			MINING,
			HEALTH,
			DIGGING,
			CHOPPING,
			TRIMMING,
			SWORDSMANSHIP,
			PUGILISM
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
