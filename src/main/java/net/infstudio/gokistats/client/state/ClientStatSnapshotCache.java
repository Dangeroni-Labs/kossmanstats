package net.infstudio.gokistats.client.state;

import java.util.LinkedHashMap;
import java.util.Map;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.snapshot.StatSnapshot;

public final class ClientStatSnapshotCache {
	private static StatSnapshot latest = StatSnapshot.EMPTY;

	private ClientStatSnapshotCache() {
	}

	public static StatSnapshot latest() {
		return latest;
	}

	public static int level(StatDefinition stat) {
		return latest.level(stat.id());
	}

	public static void replace(StatSnapshot snapshot) {
		Map<StatId, Integer> knownLevels = new LinkedHashMap<>();

		for (Map.Entry<StatId, Integer> entry : snapshot.levels().entrySet()) {
			if (KossmanStatDefinitions.byId(entry.getKey()).isPresent()) {
				knownLevels.put(entry.getKey(), Math.max(0, entry.getValue()));
			}
		}

		latest = new StatSnapshot(knownLevels);
		KossmanStats.LOGGER.info("Received stat snapshot with {} known stat levels.", latest.levels().size());
	}
}
