package net.infstudio.gokistats.fabric.mixin;

import net.infstudio.gokistats.fabric.combat.CombatDamageHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerCombatDamageMixin {
	@Redirect(
			method = "attack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
			)
	)
	private boolean kossmanstats$applyCombatDamageBonus(Entity target, DamageSource source, float damage) {
		if ((Object) this instanceof ServerPlayer player) {
			float adjustedDamage = CombatDamageHooks.applyMeleeDamageBonus(player, damage);
			adjustedDamage = CombatDamageHooks.applyReaper(player, target, source, adjustedDamage);
			return target.hurtOrSimulate(source, adjustedDamage);
		}

		return target.hurtOrSimulate(source, damage);
	}
}
