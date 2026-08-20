package net.infstudio.gokistats.fabric.combat;

import java.util.concurrent.atomic.AtomicBoolean;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.stat.PugilismStat;
import net.infstudio.gokistats.core.stat.SwordsmanshipStat;
import net.infstudio.gokistats.fabric.KossmanTags;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
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

		return originalDamage + Math.round(originalDamage + pugilismBonus);
	}

	private static double swordsmanshipBonus(ServerPlayer player, ItemStack stack) {
		if (stack.isEmpty() || !stack.is(KossmanTags.SWORDS)) {
			return 0.0D;
		}

		return SwordsmanshipStat.bonusForLevel(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.SWORDSMANSHIP));
	}

	private static double pugilismBonus(ServerPlayer player, ItemStack stack) {
		if (!stack.isEmpty() && hasMainHandAttackDamageModifier(stack)) {
			return 0.0D;
		}

		return PugilismStat.bonusForLevel(KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.PUGILISM));
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
}
