package net.infstudio.gokistats.core.progression;

import java.util.Map;
import net.infstudio.gokistats.core.definition.StatId;

public record StatDeathPenaltyResult(int requestedLoss, int appliedLoss, Map<StatId, Integer> lostLevels) {
	public StatDeathPenaltyResult {
		lostLevels = Map.copyOf(lostLevels);
	}
}
