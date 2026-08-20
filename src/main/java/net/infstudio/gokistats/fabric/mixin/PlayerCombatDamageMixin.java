package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.combat.CombatDamageHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerCombatDamageMixin {
	@ModifyArg(
			method = "attack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
			),
			index = 1
	)
	private float gokistats$applyCombatDamageBonus(float damage) {
		if ((Object) this instanceof ServerPlayer player) {
			return CombatDamageHooks.applyMeleeDamageBonus(player, damage);
		}

		return damage;
	}
}
