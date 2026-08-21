package net.infstudio.gokistats.fabric.loot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

class PlacedLootBlockPlacementResolverTest {
	@Test
	void prefersClickedPositionWhenItMatches() {
		BlockPos clickedPos = new BlockPos(0, 64, 0);

		assertEquals(clickedPos, PlacedLootBlockPlacementResolver.resolve(clickedPos, Direction.UP, clickedPos::equals));
	}

	@Test
	void fallsBackToAdjacentPlacementPosition() {
		BlockPos clickedPos = new BlockPos(0, 64, 0);
		BlockPos expected = clickedPos.above();

		assertEquals(expected, PlacedLootBlockPlacementResolver.resolve(clickedPos, Direction.UP, expected::equals));
	}

	@Test
	void returnsNullWhenNeitherCandidateMatches() {
		BlockPos clickedPos = new BlockPos(0, 64, 0);

		assertNull(PlacedLootBlockPlacementResolver.resolve(clickedPos, Direction.UP, pos -> false));
	}
}
