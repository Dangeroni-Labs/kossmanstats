package net.infstudio.gokistats;

import net.fabricmc.api.ModInitializer;
import net.infstudio.gokistats.fabric.command.KossmanStatsCommands;
import net.infstudio.gokistats.fabric.combat.SteadyGuardAttributeHandler;
import net.infstudio.gokistats.fabric.config.KossmanServerConfigManager;
import net.infstudio.gokistats.fabric.death.StatDeathPenaltyHandler;
import net.infstudio.gokistats.fabric.health.HealthAttributeHandler;
import net.infstudio.gokistats.fabric.loot.BlockLootStatHooks;
import net.infstudio.gokistats.fabric.network.StatSnapshotSync;
import net.infstudio.gokistats.fabric.network.StatUpgradeRequests;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KossmanStats implements ModInitializer {
	public static final String MOD_ID = "gokistats";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		KossmanPlayerStateStorage.register();
		KossmanServerConfigManager.register();
		StatSnapshotSync.register();
		StatUpgradeRequests.register();
		HealthAttributeHandler.register();
		SteadyGuardAttributeHandler.register();
		StatDeathPenaltyHandler.register();
		BlockLootStatHooks.register();
		KossmanStatsCommands.register();
		LOGGER.info("Kossman Stats initialized.");
	}
}
