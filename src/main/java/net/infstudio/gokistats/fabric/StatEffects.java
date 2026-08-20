package net.infstudio.gokistats.fabric;

import net.infstudio.gokistats.core.definition.GokiStatsDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.fabric.health.HealthAttributeHandler;
import net.minecraft.server.level.ServerPlayer;

public final class StatEffects {
	private StatEffects() {
	}

	public static void afterUpgrade(ServerPlayer player, StatDefinition stat) {
		if (stat.equals(GokiStatsDefinitions.HEALTH)) {
			HealthAttributeHandler.apply(player);
		}
	}
}
