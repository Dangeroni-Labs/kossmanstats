package net.infstudio.gokistats.fabric.loot;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.LongPredicate;

final class TrackedPositionSetOps {
	private TrackedPositionSetOps() {
	}

	static Set<Long> withTrackedPosition(Set<Long> trackedPositions, long packedPos) {
		if (trackedPositions.contains(packedPos)) {
			return trackedPositions;
		}

		Set<Long> updated = new LinkedHashSet<>(trackedPositions);
		updated.add(packedPos);
		return Set.copyOf(updated);
	}

	static Set<Long> withoutTrackedPosition(Set<Long> trackedPositions, long packedPos) {
		if (!trackedPositions.contains(packedPos)) {
			return trackedPositions;
		}

		Set<Long> updated = new LinkedHashSet<>(trackedPositions);
		updated.remove(packedPos);
		return updated.isEmpty() ? Set.of() : Set.copyOf(updated);
	}

	static Set<Long> retainTrackedPositions(Set<Long> trackedPositions, LongPredicate keepPredicate) {
		Set<Long> retained = trackedPositions.stream()
				.filter(keepPredicate::test)
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
		return retained.size() == trackedPositions.size() ? trackedPositions : (retained.isEmpty() ? Set.of() : Set.copyOf(retained));
	}
}
