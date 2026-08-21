package net.infstudio.gokistats.fabric.loot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class PlayerPlacedLootBlockTrackerTest {
	@Test
	void addingDuplicateTrackedPositionIsStable() {
		long packedPos = new BlockPos(4, 64, 8).asLong();
		Set<Long> tracked = TrackedPositionSetOps.withTrackedPosition(Set.of(), packedPos);

		assertEquals(Set.of(packedPos), tracked);
		assertSame(tracked, TrackedPositionSetOps.withTrackedPosition(tracked, packedPos));
	}

	@Test
	void removingLastTrackedPositionReturnsEmptySet() {
		long packedPos = new BlockPos(4, 64, 8).asLong();

		assertTrue(TrackedPositionSetOps.withoutTrackedPosition(Set.of(packedPos), packedPos).isEmpty());
	}

	@Test
	void staleTrackedPositionsCanBePrunedWithoutAWorldScan() {
		long keep = new BlockPos(1, 32, 1).asLong();
		long remove = new BlockPos(2, 32, 2).asLong();

		Set<Long> retained = TrackedPositionSetOps.retainTrackedPositions(Set.of(keep, remove), packedPos -> packedPos == keep);

		assertEquals(Set.of(keep), retained);
	}
}
