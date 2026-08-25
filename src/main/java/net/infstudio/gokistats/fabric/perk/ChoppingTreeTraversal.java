package net.infstudio.gokistats.fabric.perk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class ChoppingTreeTraversal {
	static final int MAX_LOGS_PER_TREE = 64;
	private static final int MAX_VALIDATION_LOGS = MAX_LOGS_PER_TREE * 4;
	private static final int MAX_EXPANSIONS = MAX_VALIDATION_LOGS * 2;
	private static final int MIN_TREE_HEIGHT = 3;
	private static final int CANOPY_RADIUS = 2;
	private static final int MIN_CANOPY_BLOCKS = 3;

	private ChoppingTreeTraversal() {
	}

	static TraversalResult findTree(TreeAccess access, BlockPos origin, BlockState brokenState) {
		if (access == null || origin == null || !isEligibleLog(brokenState)) {
			return TraversalResult.invalid(List.of());
		}

		String family = blockFamily(brokenState);
		if (family == null || access.isPlayerPlaced(origin)) {
			return TraversalResult.invalid(List.of());
		}

		LinkedHashSet<BlockPos> visited = new LinkedHashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		visited.add(origin.immutable());
		queue.add(origin.immutable());

		int expansions = 0;
		while (!queue.isEmpty() && visited.size() < MAX_VALIDATION_LOGS && expansions < MAX_EXPANSIONS) {
			BlockPos current = queue.removeFirst();
			expansions++;

			for (BlockPos neighbor : neighbors(current)) {
				if (visited.contains(neighbor) || access.isPlayerPlaced(neighbor) || !access.isLoaded(neighbor)) {
					continue;
				}

				BlockState state = access.getBlockState(neighbor);
				if (!isEligibleLog(state) || !family.equals(blockFamily(state))) {
					continue;
				}

				BlockPos immutableNeighbor = neighbor.immutable();
				visited.add(immutableNeighbor);
				queue.addLast(immutableNeighbor);
				if (visited.size() >= MAX_VALIDATION_LOGS) {
					break;
				}
			}
		}

		if (!isValidTree(access, visited, family)) {
			return TraversalResult.invalid(List.copyOf(visited));
		}

		List<BlockPos> orderedLogs = new ArrayList<>(visited.size());
		for (BlockPos pos : visited) {
			orderedLogs.add(pos.immutable());
		}
		orderedLogs.sort((left, right) -> {
			int yCompare = Integer.compare(left.getY(), right.getY());
			if (yCompare != 0) {
				return yCompare;
			}

			int xCompare = Integer.compare(Math.abs(left.getX() - origin.getX()), Math.abs(right.getX() - origin.getX()));
			if (xCompare != 0) {
				return xCompare;
			}

			return Integer.compare(Math.abs(left.getZ() - origin.getZ()), Math.abs(right.getZ() - origin.getZ()));
		});
		return new TraversalResult(true, List.copyOf(orderedLogs.subList(0, Math.min(MAX_LOGS_PER_TREE, orderedLogs.size()))));
	}

	static boolean isEligibleLog(BlockState state) {
		if (state == null) {
			return false;
		}

		if (state.is(BlockTags.LOGS)) {
			return true;
		}

		String family = blockFamily(state);
		return family != null && !family.isBlank() && !family.equals(blockPath(state));
	}

	static String blockFamily(BlockState state) {
		if (state == null) {
			return null;
		}

		Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		String path = id.getPath().toLowerCase(Locale.ROOT);
		for (String suffix : List.of("_log", "_wood", "_stem", "_hyphae")) {
			if (path.endsWith(suffix) && path.length() > suffix.length()) {
				return path.substring(0, path.length() - suffix.length());
			}
		}
		return path;
	}

	private static boolean isValidTree(TreeAccess access, Set<BlockPos> logs, String family) {
		if (logs.size() < 2) {
			return false;
		}

		int minY = logs.stream().mapToInt(BlockPos::getY).min().orElse(Integer.MIN_VALUE);
		int maxY = logs.stream().mapToInt(BlockPos::getY).max().orElse(Integer.MAX_VALUE);
		if (maxY - minY + 1 < MIN_TREE_HEIGHT) {
			return false;
		}

		int canopyFloor = Math.max(minY + 1, maxY - 2);
		Set<Long> canopyBlocks = new HashSet<>();
		for (BlockPos log : logs) {
			if (log.getY() < canopyFloor) {
				continue;
			}

			for (int dx = -CANOPY_RADIUS; dx <= CANOPY_RADIUS; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -CANOPY_RADIUS; dz <= CANOPY_RADIUS; dz++) {
						BlockPos nearby = log.offset(dx, dy, dz);
						if (!access.isLoaded(nearby) || logs.contains(nearby)) {
							continue;
						}

						if (isNaturalCanopyBlock(access.getBlockState(nearby), family)) {
							canopyBlocks.add(nearby.asLong());
							if (canopyBlocks.size() >= MIN_CANOPY_BLOCKS) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean isNaturalCanopyBlock(BlockState state, String family) {
		if (state == null) {
			return false;
		}

		if (state.is(BlockTags.LEAVES) || blockPath(state).endsWith("_leaves")) {
			return true;
		}

		return switch (family) {
			case "crimson" -> state.is(Blocks.NETHER_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT);
			case "warped" -> state.is(Blocks.WARPED_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT);
			default -> false;
		};
	}

	private static String blockPath(BlockState state) {
		return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().toLowerCase(Locale.ROOT);
	}

	private static List<BlockPos> neighbors(BlockPos pos) {
		List<BlockPos> neighbors = new ArrayList<>(26);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dy == 0 && dz == 0) {
						continue;
					}
					neighbors.add(pos.offset(dx, dy, dz));
				}
			}
		}
		return neighbors;
	}

	interface TreeAccess {
		boolean isLoaded(BlockPos pos);

		BlockState getBlockState(BlockPos pos);

		boolean isPlayerPlaced(BlockPos pos);
	}

	record TraversalResult(boolean validTree, List<BlockPos> logs) {
		static TraversalResult invalid(List<BlockPos> logs) {
			return new TraversalResult(false, List.copyOf(logs));
		}
	}
}
