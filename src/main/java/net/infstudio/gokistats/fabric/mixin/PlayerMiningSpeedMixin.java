package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.tool.ToolSpeedHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMiningSpeedMixin {
	@Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
	private void gokistats$applyMiningSpeed(BlockState blockState, CallbackInfoReturnable<Float> cir) {
		if ((Object) this instanceof ServerPlayer player) {
			cir.setReturnValue(ToolSpeedHooks.applyToolSpeedBonus(player, blockState, cir.getReturnValueF()));
		}
	}
}
