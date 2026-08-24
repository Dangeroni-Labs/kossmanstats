package net.infstudio.gokistats.fabric.tool;

import java.util.List;
import java.util.function.ToIntFunction;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.perk.MomentumPerkHandler;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class ToolSpeedHooks {
	private static final List<ToolSpeedStat> TOOL_SPEED_STATS = List.of(
			new ToolSpeedStat(KossmanStatDefinitions.MINING, KossmanTags.MINING_TOOLS, level -> StatFormulas.toolSpeedBonus(KossmanStatDefinitions.MINING, level)),
			new ToolSpeedStat(KossmanStatDefinitions.DIGGING, KossmanTags.DIGGING_TOOLS, level -> StatFormulas.toolSpeedBonus(KossmanStatDefinitions.DIGGING, level)),
			new ToolSpeedStat(KossmanStatDefinitions.CHOPPING, KossmanTags.CHOPPING_TOOLS, level -> StatFormulas.toolSpeedBonus(KossmanStatDefinitions.CHOPPING, level)),
			new ToolSpeedStat(KossmanStatDefinitions.TRIMMING, KossmanTags.TRIMMING_TOOLS, level -> StatFormulas.toolSpeedBonus(KossmanStatDefinitions.TRIMMING, level))
	);

	private ToolSpeedHooks() {
	}

	public static float applyServerToolSpeedBonus(ServerPlayer player, BlockState state, float originalSpeed) {
		float statSpeed = applyToolSpeedBonus(
				player.getMainHandItem(),
				originalSpeed,
				stat -> KossmanPlayerStateStorage.getLevel(player, stat)
		);
		return MomentumPerkHandler.applySpeedBonus(player, state, statSpeed);
	}

	public static float applyToolSpeedBonus(
			ItemStack stack,
			float originalSpeed,
			ToIntFunction<StatDefinition> levelProvider
	) {
		if (originalSpeed <= 0.0F) {
			return originalSpeed;
		}

		double bonus = 0.0D;

		for (ToolSpeedStat stat : TOOL_SPEED_STATS) {
			if (stack.is(stat.toolTag())) {
				int level = Math.max(0, levelProvider.applyAsInt(stat.stat()));
				bonus += stat.bonusForLevel().applyAsDouble(level);
			}
		}

		if (bonus <= 0.0D) {
			return originalSpeed;
		}

		return (float) (originalSpeed * (1.0D + bonus));
	}
}
