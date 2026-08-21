package net.infstudio.gokistats.fabric.combat;

import java.util.concurrent.atomic.AtomicBoolean;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public final class CombatDamageHooks {
	private CombatDamageHooks() {
	}

	public static float applyMeleeDamageBonus(ServerPlayer player, float originalDamage) {
		if (originalDamage <= 0.0F) {
			return originalDamage;
		}

		ItemStack stack = player.getMainHandItem();
		double swordsmanshipBonus = swordsmanshipBonus(player, stack);
		if (swordsmanshipBonus > 0.0D) {
			return (float) (originalDamage + Math.round(originalDamage * swordsmanshipBonus));
		}

		double pugilismBonus = pugilismBonus(player, stack);
		if (pugilismBonus <= 0.0D) {
			return originalDamage;
		}

		return (float) (originalDamage + pugilismBonus);
	}

	public static float applyReaper(ServerPlayer player, Entity target, DamageSource source, float damage) {
		if (damage <= 0.0F || !(target instanceof LivingEntity livingTarget) || livingTarget instanceof net.minecraft.world.entity.player.Player) {
			return damage;
		}

		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.REAPER);
		if (!isReaperEligibleTarget(livingTarget, level)) {
			return damage;
		}

		double chance = StatFormulas.reaperChance(level);
		if (chance <= 0.0D || player.getRandom().nextDouble() >= chance) {
			return damage;
		}

		float finishingDamage = livingTarget.getHealth() + livingTarget.getAbsorptionAmount() + 1.0F;
		return Math.max(damage, finishingDamage);
	}

	private static double swordsmanshipBonus(ServerPlayer player, ItemStack stack) {
		if (stack.isEmpty() || !stack.is(KossmanTags.SWORDS)) {
			return 0.0D;
		}

		return StatFormulas.swordsmanshipDamageBonus(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.SWORDSMANSHIP));
	}

	private static double pugilismBonus(ServerPlayer player, ItemStack stack) {
		if (!stack.isEmpty() && hasMainHandAttackDamageModifier(stack)) {
			return 0.0D;
		}

		return StatFormulas.pugilismDamageBonus(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.PUGILISM));
	}

	private static boolean hasMainHandAttackDamageModifier(ItemStack stack) {
		AtomicBoolean hasModifier = new AtomicBoolean(false);
		stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
			if (isAttackDamage(attribute)) {
				hasModifier.set(true);
			}
		});
		return hasModifier.get();
	}

	private static boolean isAttackDamage(Holder<Attribute> attribute) {
		return attribute.equals(Attributes.ATTACK_DAMAGE);
	}

	private static boolean isReaperEligibleTarget(LivingEntity target, int level) {
		if (!target.isAlive()) {
			return false;
		}

		if (target.getMaxHealth() > StatFormulas.reaperMaxTargetHealth()) {
			return false;
		}

		double threshold = StatFormulas.reaperHealthThreshold(level);
		if (threshold <= 0.0D) {
			return false;
		}

		return target.getHealth() <= target.getMaxHealth() * threshold;
	}
}
