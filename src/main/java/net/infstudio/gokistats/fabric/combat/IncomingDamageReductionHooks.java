package net.infstudio.gokistats.fabric.combat;

import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;

public final class IncomingDamageReductionHooks {
	private static final double VANILLA_FALL_SAFE_DISTANCE = 3.0D;

	private IncomingDamageReductionHooks() {
	}

	public static float applyIncomingDamageReduction(ServerPlayer player, DamageSource source, float amount) {
		if (amount <= 0.0F) {
			return amount;
		}

		if (shouldRoll(player, source)) {
			return 0.0F;
		}

		double reduction = damageReduction(player, source);
		return StatFormulas.applyDamageReduction(amount, reduction);
	}

	public static double fallSafeDistance(ServerPlayer player) {
		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.FEATHER_FALL);
		return VANILLA_FALL_SAFE_DISTANCE + jumpBoostSafeDistanceBonus(player) + StatFormulas.featherFallSafeDistanceBonus(level);
	}

	private static double damageReduction(ServerPlayer player, DamageSource source) {
		if (isMobMelee(source)) {
			return StatFormulas.protectionDamageReduction(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.PROTECTION));
		}

		if (isLegacyFireOrLava(source)) {
			return StatFormulas.temperingDamageReduction(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.TEMPERING));
		}

		if (source.is(DamageTypeTags.IS_EXPLOSION)) {
			return StatFormulas.toughSkinDamageReduction(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.TOUGH_SKIN));
		}

		if (source.is(DamageTypes.FALL)) {
			return StatFormulas.featherFallDamageReduction(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.FEATHER_FALL));
		}

		return 0.0D;
	}

	private static boolean isMobMelee(DamageSource source) {
		return source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO);
	}

	private static boolean isLegacyFireOrLava(DamageSource source) {
		return source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA);
	}

	private static double jumpBoostSafeDistanceBonus(ServerPlayer player) {
		var effect = player.getEffect(MobEffects.JUMP_BOOST);
		return effect == null ? 0.0D : effect.getAmplifier() + 1.0D;
	}

	private static boolean shouldRoll(ServerPlayer player, DamageSource source) {
		if (!isRollEligible(source)) {
			return false;
		}

		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.ROLL);
		double chance = StatFormulas.rollEvadeChance(level);
		return chance > 0.0D && player.getRandom().nextDouble() < chance;
	}

	private static boolean isRollEligible(DamageSource source) {
		if (source.getEntity() == null && source.getDirectEntity() == null) {
			return false;
		}

		if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypes.FALL)) {
			return false;
		}

		return !source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.INDIRECT_MAGIC);
	}
}
