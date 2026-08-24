package net.infstudio.gokistats.fabric.perk;

import java.util.Map;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class MomentumPerkHandler {
	static final int MAX_STACKS = 5;
	static final double BONUS_PER_STACK = 0.04D;
	static final long RESET_WINDOW_TICKS = 40L;
	private static final float MIN_EFFECTIVE_TOOL_SPEED = 1.0F;
	private static final Map<UUID, MomentumState> STATES = new ConcurrentHashMap<>();

	private MomentumPerkHandler() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register(MomentumPerkHandler::afterBlockBreak);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> clear(handler.player));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				clear(oldPlayer);
				clear(newPlayer);
			}
		});
	}

	public static float applySpeedBonus(ServerPlayer player, BlockState state, float speed) {
		if (speed <= 0.0F || !canUseMomentum(player, state)) {
			reset(player);
			return speed;
		}

		MomentumState momentum = STATES.get(player.getUUID());
		if (momentum == null) {
			return speed;
		}

		long gameTime = player.level().getGameTime();
		if (isExpired(momentum, gameTime, RESET_WINDOW_TICKS)) {
			reset(player);
			return speed;
		}

		double bonus = bonusForStacks(momentum.stacks());
		return bonus <= 0.0D ? speed : (float) (speed * (1.0D + bonus));
	}

	static MomentumState recordEligibleBreak(MomentumState previous, long gameTime) {
		if (previous == null || isExpired(previous, gameTime, RESET_WINDOW_TICKS)) {
			return new MomentumState(0, gameTime);
		}

		return new MomentumState(Math.min(MAX_STACKS, previous.stacks() + 1), gameTime);
	}

	static double bonusForStacks(int stacks) {
		return Math.max(0, Math.min(MAX_STACKS, stacks)) * BONUS_PER_STACK;
	}

	static boolean isExpired(MomentumState momentum, long gameTime, long resetWindowTicks) {
		return momentum == null || gameTime - momentum.lastEligibleBreakTick() > resetWindowTicks;
	}

	static int stacks(ServerPlayer player) {
		MomentumState momentum = STATES.get(player.getUUID());
		return momentum == null ? 0 : momentum.stacks();
	}

	static void clear(ServerPlayer player) {
		STATES.remove(player.getUUID());
	}

	private static void afterBlockBreak(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (!canUseMomentum(serverPlayer, state)) {
			reset(serverPlayer);
			return;
		}

		long gameTime = serverPlayer.level().getGameTime();
		STATES.compute(serverPlayer.getUUID(), (uuid, previous) -> recordEligibleBreak(previous, gameTime));
	}

	private static boolean canUseMomentum(ServerPlayer player, BlockState state) {
		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.DIGGING);
		return KossmanBalance.current().isPerkUnlocked(KossmanStatDefinitions.DIGGING, level)
				&& isEligibleDigging(player.getMainHandItem(), state);
	}

	private static boolean isEligibleDigging(ItemStack stack, BlockState state) {
		return stack != null
				&& !stack.isEmpty()
				&& stack.is(KossmanTags.DIGGING_TOOLS)
				&& stack.getDestroySpeed(state) > MIN_EFFECTIVE_TOOL_SPEED;
	}

	private static void reset(ServerPlayer player) {
		clear(player);
	}

	record MomentumState(int stacks, long lastEligibleBreakTick) {
	}
}
