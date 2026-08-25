package net.infstudio.gokistats.fabric.perk;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ChoppingPerkHandler {
	private static final float MIN_EFFECTIVE_TOOL_SPEED = 1.0F;
	private static final Set<UUID> ACTIVE_PLAYERS = ConcurrentHashMap.newKeySet();

	private ChoppingPerkHandler() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register(ChoppingPerkHandler::afterBlockBreak);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> clear(handler.player));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				clear(oldPlayer);
				clear(newPlayer);
			}
		});
	}

	private static void afterBlockBreak(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (!canUseLumberjack(serverPlayer, state) || !ACTIVE_PLAYERS.add(serverPlayer.getUUID())) {
			return;
		}

		try {
			ChoppingTreeTraversal.TraversalResult tree = ChoppingTreeTraversal.findTree(new ServerTreeAccess(serverLevel), pos, state);
			if (!tree.validTree()) {
				return;
			}

			fellTree(serverPlayer, pos, tree.logs());
		} finally {
			ACTIVE_PLAYERS.remove(serverPlayer.getUUID());
		}
	}

	private static void fellTree(ServerPlayer player, BlockPos origin, List<BlockPos> treeLogs) {
		for (BlockPos pos : treeLogs) {
			if (pos.equals(origin)) {
				continue;
			}

			if (!player.level().hasChunkAt(pos)) {
				return;
			}

			BlockState state = player.level().getBlockState(pos);
			if (!canContinueBreaking(player, state)) {
				return;
			}

			if (!player.gameMode.destroyBlock(pos)) {
				return;
			}
		}
	}

	private static boolean canUseLumberjack(ServerPlayer player, BlockState state) {
		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.CHOPPING);
		return KossmanBalance.current().isPerkUnlocked(KossmanStatDefinitions.CHOPPING, level)
				&& canContinueBreaking(player, state);
	}

	private static boolean canContinueBreaking(ServerPlayer player, BlockState state) {
		ItemStack stack = player.getMainHandItem();
		return stack != null
				&& !stack.isEmpty()
				&& stack.getItem() instanceof AxeItem
				&& stack.is(KossmanTags.CHOPPING_TOOLS)
				&& ChoppingTreeTraversal.isEligibleLog(state)
				&& stack.getDestroySpeed(state) > MIN_EFFECTIVE_TOOL_SPEED;
	}

	private static void clear(ServerPlayer player) {
		ACTIVE_PLAYERS.remove(player.getUUID());
	}

	private record ServerTreeAccess(ServerLevel level) implements ChoppingTreeTraversal.TreeAccess {
		@Override
		public boolean isLoaded(BlockPos pos) {
			return level.hasChunkAt(pos);
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return level.getBlockState(pos);
		}

		@Override
		public boolean isPlayerPlaced(BlockPos pos) {
			return false;
		}
	}
}
