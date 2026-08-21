package net.infstudio.gokistats.fabric.combat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class SteadyGuardAttributeHandler {
	private static final Identifier STEADY_GUARD_MODIFIER_ID = Identifier.fromNamespaceAndPath(
			KossmanStats.MOD_ID,
			"steady_guard_blocking_knockback_resistance"
	);

	private SteadyGuardAttributeHandler() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(SteadyGuardAttributeHandler::apply));
	}

	private static void apply(net.minecraft.server.level.ServerPlayer player) {
		var knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
		if (knockbackResistance == null) {
			return;
		}

		knockbackResistance.removeModifier(STEADY_GUARD_MODIFIER_ID);

		if (!player.isBlocking()) {
			return;
		}

		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.STEADY_GUARD);
		double bonus = StatFormulas.steadyGuardKnockbackResistanceBonus(level);
		if (bonus <= 0.0D) {
			return;
		}

		knockbackResistance.addTransientModifier(new AttributeModifier(
				STEADY_GUARD_MODIFIER_ID,
				bonus,
				AttributeModifier.Operation.ADD_VALUE
		));
	}
}
