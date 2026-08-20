package net.infstudio.gokistats.core.definition;

import java.util.List;
import java.util.Optional;

public final class GokiStatsDefinitions {
	public static final StatDefinition MINING = new StatDefinition(
			new StatId("gokistats:mining"),
			"mining",
			"Mining",
			25
	);

	public static final StatDefinition HEALTH = new StatDefinition(
			new StatId("gokistats:health"),
			"health",
			"Health",
			16
	);

	public static final StatDefinition DIGGING = new StatDefinition(
			new StatId("gokistats:digging"),
			"digging",
			"Digging",
			25
	);

	public static final StatDefinition CHOPPING = new StatDefinition(
			new StatId("gokistats:chopping"),
			"chopping",
			"Chopping",
			25
	);

	public static final StatDefinition TRIMMING = new StatDefinition(
			new StatId("gokistats:trimming"),
			"trimming",
			"Trimming",
			25
	);

	public static final List<StatDefinition> ALL = List.of(MINING, HEALTH, DIGGING, CHOPPING, TRIMMING);

	private GokiStatsDefinitions() {
	}

	public static Optional<StatDefinition> byCommandName(String commandName) {
		return ALL.stream()
				.filter(stat -> stat.commandName().equals(commandName))
				.findFirst();
	}
}
