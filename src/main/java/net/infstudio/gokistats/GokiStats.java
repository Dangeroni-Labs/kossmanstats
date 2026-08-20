package net.infstudio.gokistats;

import net.fabricmc.api.ModInitializer;
import net.infstudio.gokistats.fabric.command.GokiStatsCommands;
import net.infstudio.gokistats.fabric.state.GokiPlayerStateStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GokiStats implements ModInitializer {
	public static final String MOD_ID = "gokistats";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GokiPlayerStateStorage.register();
		GokiStatsCommands.register();
		LOGGER.info("GokiStats initialized.");
	}
}
