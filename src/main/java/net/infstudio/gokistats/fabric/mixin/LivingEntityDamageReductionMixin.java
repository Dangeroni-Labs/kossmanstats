package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.combat.IncomingDamageReductionHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageReductionMixin {
	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float kossmanstats$applyIncomingDamageReduction(float amount, ServerLevel level, DamageSource source, float originalAmount) {
		if ((Object) this instanceof ServerPlayer player) {
			return IncomingDamageReductionHooks.applyIncomingDamageReduction(player, source, amount);
		}

		return amount;
	}

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private double kossmanstats$applyFeatherFallSafeDistance(double fallDistance) {
		if ((Object) this instanceof ServerPlayer player && fallDistance < IncomingDamageReductionHooks.fallSafeDistance(player)) {
			return 0.0D;
		}

		return fallDistance;
	}
}
