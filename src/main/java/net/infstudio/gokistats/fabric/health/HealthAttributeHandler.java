package net.infstudio.gokistats.fabric.health;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class HealthAttributeHandler {
	private static final Identifier MAX_HEALTH_MODIFIER_ID = Identifier.fromNamespaceAndPath(
			KossmanStats.MOD_ID,
			"health_stat_max_health"
	);

	private HealthAttributeHandler() {
	}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> apply(handler.player));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> apply(newPlayer));
	}

	public static void apply(ServerPlayer player) {
		var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		if (maxHealth == null) {
			return;
		}

		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.HEALTH);
		double bonus = StatFormulas.healthMaxHealthBonus(level);

		if (bonus <= 0.0D) {
			maxHealth.removeModifier(MAX_HEALTH_MODIFIER_ID);
			player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
			return;
		}

		maxHealth.addOrReplacePermanentModifier(new AttributeModifier(
				MAX_HEALTH_MODIFIER_ID,
				bonus,
				AttributeModifier.Operation.ADD_VALUE
		));
		player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
	}
}
