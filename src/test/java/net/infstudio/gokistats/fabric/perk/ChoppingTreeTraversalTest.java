package net.infstudio.gokistats.fabric.perk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ChoppingTreeTraversalTest {
	@BeforeAll
	static void bootstrapMinecraft() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void eligibleLogsUseVanillaLogTagFamilies() {
		assertTrue(ChoppingTreeTraversal.isEligibleLog(Blocks.OAK_LOG.defaultBlockState()));
		assertTrue(ChoppingTreeTraversal.isEligibleLog(Blocks.CRIMSON_STEM.defaultBlockState()));
		assertFalse(ChoppingTreeTraversal.isEligibleLog(Blocks.OAK_PLANKS.defaultBlockState()));
	}

	@Test
	void traversalFindsBoundedNaturalTree() {
		FakeTreeAccess access = new FakeTreeAccess();
		BlockPos origin = new BlockPos(0, 64, 0);
		access.put(new BlockPos(0, 65, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(0, 66, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(1, 66, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(0, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(1, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(-1, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());

		ChoppingTreeTraversal.TraversalResult result = ChoppingTreeTraversal.findTree(access, origin, Blocks.OAK_LOG.defaultBlockState());

		assertTrue(result.validTree());
		assertEquals(4, result.logs().size());
		assertEquals(origin, result.logs().getFirst());
	}

	@Test
	void traversalRejectsLeaflessLogWall() {
		FakeTreeAccess access = new FakeTreeAccess();
		BlockPos origin = new BlockPos(0, 64, 0);
		access.put(new BlockPos(0, 65, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(1, 64, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(1, 65, 0), Blocks.OAK_LOG.defaultBlockState());

		ChoppingTreeTraversal.TraversalResult result = ChoppingTreeTraversal.findTree(access, origin, Blocks.OAK_LOG.defaultBlockState());

		assertFalse(result.validTree());
	}

	@Test
	void traversalStopsAtConfiguredCap() {
		FakeTreeAccess access = new FakeTreeAccess();
		BlockPos origin = new BlockPos(0, 64, 0);
		for (int index = 1; index < 80; index++) {
			access.put(new BlockPos(0, 64 + index, 0), Blocks.OAK_LOG.defaultBlockState());
		}
		access.put(new BlockPos(0, 64 + 79, 1), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(1, 64 + 79, 0), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(-1, 64 + 79, 0), Blocks.OAK_LEAVES.defaultBlockState());

		ChoppingTreeTraversal.TraversalResult result = ChoppingTreeTraversal.findTree(access, origin, Blocks.OAK_LOG.defaultBlockState());

		assertTrue(result.validTree());
		assertEquals(ChoppingTreeTraversal.MAX_LOGS_PER_TREE, result.logs().size());
	}

	@Test
	void traversalSkipsPlayerPlacedLogs() {
		FakeTreeAccess access = new FakeTreeAccess();
		BlockPos origin = new BlockPos(0, 64, 0);
		BlockPos placed = new BlockPos(0, 65, 0);
		access.put(placed, Blocks.OAK_LOG.defaultBlockState());
		access.markPlayerPlaced(placed);
		access.put(new BlockPos(0, 66, 0), Blocks.OAK_LOG.defaultBlockState());
		access.put(new BlockPos(0, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(1, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());
		access.put(new BlockPos(-1, 67, 0), Blocks.OAK_LEAVES.defaultBlockState());

		ChoppingTreeTraversal.TraversalResult result = ChoppingTreeTraversal.findTree(access, origin, Blocks.OAK_LOG.defaultBlockState());

		assertFalse(result.validTree());
	}

	private static final class FakeTreeAccess implements ChoppingTreeTraversal.TreeAccess {
		private final Map<Long, BlockState> states = new HashMap<>();
		private final Set<Long> playerPlaced = new HashSet<>();

		void put(BlockPos pos, BlockState state) {
			states.put(pos.asLong(), state);
		}

		void markPlayerPlaced(BlockPos pos) {
			playerPlaced.add(pos.asLong());
		}

		@Override
		public boolean isLoaded(BlockPos pos) {
			return true;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return states.getOrDefault(pos.asLong(), Blocks.AIR.defaultBlockState());
		}

		@Override
		public boolean isPlayerPlaced(BlockPos pos) {
			return playerPlaced.contains(pos.asLong());
		}
	}
}
