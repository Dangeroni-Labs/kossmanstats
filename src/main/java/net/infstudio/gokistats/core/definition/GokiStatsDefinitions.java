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

	public static final List<StatDefinition> ALL = List.of(MINING, HEALTH);

	private GokiStatsDefinitions() {
	}

	public static Optional<StatDefinition> byCommandName(String commandName) {
		return ALL.stream()
				.filter(stat -> stat.commandName().equals(commandName))
				.findFirst();
	}
}
