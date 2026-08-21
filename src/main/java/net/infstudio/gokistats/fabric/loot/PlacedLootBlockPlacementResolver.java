package net.infstudio.gokistats.fabric.loot;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class PlacedLootBlockPlacementResolver {
	private PlacedLootBlockPlacementResolver() {
	}

	public static BlockPos resolve(BlockPos clickedPos, Direction clickedFace, Predicate<BlockPos> matchesPlacedState) {
		if (matchesPlacedState.test(clickedPos)) {
			return clickedPos;
		}

		BlockPos relativePos = clickedPos.relative(clickedFace);
		return matchesPlacedState.test(relativePos) ? relativePos : null;
	}
}
