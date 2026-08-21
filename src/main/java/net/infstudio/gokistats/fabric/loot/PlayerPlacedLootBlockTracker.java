package net.infstudio.gokistats.fabric.loot;

import com.mojang.serialization.Codec;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class PlayerPlacedLootBlockTracker {
	private static final Codec<Set<Long>> TRACKED_POSITIONS_CODEC = Codec.LONG_STREAM.xmap(
			PlayerPlacedLootBlockTracker::decodePositions,
			PlayerPlacedLootBlockTracker::encodePositions
	);
	private static final AttachmentType<Set<Long>> TRACKED_POSITIONS = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "player_placed_loot_blocks"),
			builder -> builder
					.initializer(Set::of)
					.persistent(TRACKED_POSITIONS_CODEC)
	);

	private PlayerPlacedLootBlockTracker() {
	}

	public static void register() {
		ServerChunkEvents.CHUNK_LOAD.register((level, chunk, generated) -> pruneChunk(level, chunk));
	}

	public static boolean isRelevant(BlockState state) {
		return state.is(KossmanTags.TREASURE_FINDER_BLOCKS) || state.is(KossmanTags.MAGICIAN_ORES);
	}

	public static void updatePlacedState(ServerLevel level, BlockPos pos, BlockState state) {
		if (isRelevant(state)) {
			add(level, pos);
			return;
		}

		remove(level, pos);
	}

	public static boolean consume(ServerLevel level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		long packedPos = pos.asLong();
		Set<Long> trackedPositions = chunk.getAttachedOrElse(TRACKED_POSITIONS, Set.of());
		if (!trackedPositions.contains(packedPos)) {
			return false;
		}

		setTrackedPositions(chunk, TrackedPositionSetOps.withoutTrackedPosition(trackedPositions, packedPos));
		return true;
	}

	public static boolean shouldInspectPlacementResult(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return !state.isAir() && (isRelevant(state) || level.getChunkAt(pos).hasAttached(TRACKED_POSITIONS));
	}

	private static void add(ServerLevel level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		setTrackedPositions(chunk, TrackedPositionSetOps.withTrackedPosition(chunk.getAttachedOrElse(TRACKED_POSITIONS, Set.of()), pos.asLong()));
	}

	private static void remove(ServerLevel level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		setTrackedPositions(chunk, TrackedPositionSetOps.withoutTrackedPosition(chunk.getAttachedOrElse(TRACKED_POSITIONS, Set.of()), pos.asLong()));
	}

	private static void pruneChunk(ServerLevel level, LevelChunk chunk) {
		Set<Long> trackedPositions = chunk.getAttached(TRACKED_POSITIONS);
		if (trackedPositions == null || trackedPositions.isEmpty()) {
			return;
		}

		setTrackedPositions(chunk, TrackedPositionSetOps.retainTrackedPositions(trackedPositions, packedPos -> isRelevant(level.getBlockState(BlockPos.of(packedPos)))));
	}

	private static void setTrackedPositions(LevelChunk chunk, Set<Long> trackedPositions) {
		chunk.setAttached(TRACKED_POSITIONS, trackedPositions.isEmpty() ? null : trackedPositions);
	}

	private static Set<Long> decodePositions(LongStream stream) {
		return stream.boxed().collect(Collectors.toUnmodifiableSet());
	}

	private static LongStream encodePositions(Set<Long> positions) {
		return positions.stream().mapToLong(Long::longValue);
	}
}
