package net.infstudio.gokistats.fabric.mining;

import net.infstudio.gokistats.core.MiningStat;
import net.infstudio.gokistats.fabric.GokiTags;
import net.infstudio.gokistats.fabric.state.GokiPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class MiningSpeedHooks {
	private MiningSpeedHooks() {
	}

	public static float applyMiningBonus(ServerPlayer player, BlockState blockState, float originalSpeed) {
		if (originalSpeed <= 0.0F) {
			return originalSpeed;
		}

		ItemStack stack = player.getMainHandItem();
		if (!stack.is(GokiTags.MINING_TOOLS) || !stack.isCorrectToolForDrops(blockState)) {
			return originalSpeed;
		}

		int level = GokiPlayerStateStorage.getMiningLevel(player);
		double bonus = MiningStat.bonusForLevel(level);
		if (bonus <= 0.0D) {
			return originalSpeed;
		}

		return (float) (originalSpeed * (1.0D + bonus));
	}
}
