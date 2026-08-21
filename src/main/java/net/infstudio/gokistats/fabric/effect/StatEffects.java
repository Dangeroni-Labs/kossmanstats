package net.infstudio.gokistats.fabric.effect;

import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.fabric.health.HealthAttributeHandler;
import net.minecraft.server.level.ServerPlayer;

public final class StatEffects {
	private StatEffects() {
	}

	public static void afterProgressionChange(ServerPlayer player, StatDefinition stat) {
		if (stat.equals(KossmanStatDefinitions.HEALTH)) {
			HealthAttributeHandler.apply(player);
		}
	}
}
