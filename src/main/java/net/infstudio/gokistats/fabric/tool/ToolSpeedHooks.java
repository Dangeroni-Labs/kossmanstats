package net.infstudio.gokistats.fabric.tool;

import java.util.List;
import net.infstudio.gokistats.core.definition.GokiStatsDefinitions;
import net.infstudio.gokistats.core.stat.ChoppingStat;
import net.infstudio.gokistats.core.stat.DiggingStat;
import net.infstudio.gokistats.core.stat.MiningStat;
import net.infstudio.gokistats.core.stat.TrimmingStat;
import net.infstudio.gokistats.fabric.GokiTags;
import net.infstudio.gokistats.fabric.state.GokiPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class ToolSpeedHooks {
	private static final List<ToolSpeedStat> TOOL_SPEED_STATS = List.of(
			new ToolSpeedStat(GokiStatsDefinitions.MINING, GokiTags.MINING_TOOLS, MiningStat::bonusForLevel),
			new ToolSpeedStat(GokiStatsDefinitions.DIGGING, GokiTags.DIGGING_TOOLS, DiggingStat::bonusForLevel),
			new ToolSpeedStat(GokiStatsDefinitions.CHOPPING, GokiTags.CHOPPING_TOOLS, ChoppingStat::bonusForLevel),
			new ToolSpeedStat(GokiStatsDefinitions.TRIMMING, GokiTags.TRIMMING_TOOLS, TrimmingStat::bonusForLevel)
	);

	private ToolSpeedHooks() {
	}

	public static float applyToolSpeedBonus(ServerPlayer player, BlockState blockState, float originalSpeed) {
		if (originalSpeed <= 0.0F) {
			return originalSpeed;
		}

		ItemStack stack = player.getMainHandItem();
		double bonus = 0.0D;

		for (ToolSpeedStat stat : TOOL_SPEED_STATS) {
			if (stack.is(stat.toolTag())) {
				int level = GokiPlayerStateStorage.getLevel(player, stat.stat());
				bonus += stat.bonusForLevel().applyAsDouble(level);
			}
		}

		if (bonus <= 0.0D) {
			return originalSpeed;
		}

		return (float) (originalSpeed * (1.0D + bonus));
	}
}
