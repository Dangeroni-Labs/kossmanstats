package net.infstudio.gokistats.core.snapshot;

import java.util.Map;
import net.infstudio.gokistats.core.definition.StatId;

public record StatSnapshot(Map<StatId, Integer> levels) {
	public static final StatSnapshot EMPTY = new StatSnapshot(Map.of());

	public StatSnapshot {
		levels = Map.copyOf(levels);
	}

	public int level(StatId statId) {
		return Math.max(0, levels.getOrDefault(statId, 0));
	}
}
