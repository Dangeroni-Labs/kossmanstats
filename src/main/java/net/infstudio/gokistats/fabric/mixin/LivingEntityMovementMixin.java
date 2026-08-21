package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.movement.MovementHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMovementMixin {
	@Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
	private void kossmanstats$applyLeaperV(CallbackInfoReturnable<Float> cir) {
		if ((Object) this instanceof Player player) {
			cir.setReturnValue(MovementHooks.applyJumpPowerBonus(player, cir.getReturnValueF()));
		}
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	private void kossmanstats$applyLeaperH(CallbackInfo ci) {
		if ((Object) this instanceof Player player) {
			MovementHooks.applyHorizontalJumpBonus(player);
		}
	}

	@Inject(method = "travel", at = @At("RETURN"))
	private void kossmanstats$applyClimbingBonus(Vec3 movementInput, CallbackInfo ci) {
		if ((Object) this instanceof Player player) {
			MovementHooks.applyClimbingBonus(player);
		}
	}
}
