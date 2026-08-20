package net.infstudio.gokistats.fabric.tool;

import java.util.List;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class ToolSpeedHooks {
	private static final List<ToolSpeedStat> TOOL_SPEED_STATS = List.of(
			new ToolSpeedStat(KossmanStatDefinitions.MINING, KossmanTags.MINING_TOOLS, StatFormulas::toolSpeedBonus),
			new ToolSpeedStat(KossmanStatDefinitions.DIGGING, KossmanTags.DIGGING_TOOLS, StatFormulas::toolSpeedBonus),
			new ToolSpeedStat(KossmanStatDefinitions.CHOPPING, KossmanTags.CHOPPING_TOOLS, StatFormulas::toolSpeedBonus),
			new ToolSpeedStat(KossmanStatDefinitions.TRIMMING, KossmanTags.TRIMMING_TOOLS, StatFormulas::toolSpeedBonus)
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
				int level = KossmanPlayerStateStorage.getLevel(player, stat.stat());
				bonus += stat.bonusForLevel().applyAsDouble(level);
			}
		}

		if (bonus <= 0.0D) {
			return originalSpeed;
		}

		return (float) (originalSpeed * (1.0D + bonus));
	}
}
