package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.loot.PlacedLootBlockPlacementResolver;
import net.infstudio.gokistats.fabric.loot.PlayerPlacedLootBlockTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemPlacementMixin {
	@Inject(method = "place", at = @At("RETURN"))
	private void kossmanstats$trackRelevantPlacedBlocks(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
		if (!cir.getReturnValue().consumesAction() || !(context.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		BlockPos placedPos = PlacedLootBlockPlacementResolver.resolve(
				context.getClickedPos(),
				context.getClickedFace(),
				candidatePos -> PlayerPlacedLootBlockTracker.shouldInspectPlacementResult(serverLevel, candidatePos)
		);
		if (placedPos == null) {
			return;
		}

		PlayerPlacedLootBlockTracker.updatePlacedState(serverLevel, placedPos, serverLevel.getBlockState(placedPos));
	}
}
