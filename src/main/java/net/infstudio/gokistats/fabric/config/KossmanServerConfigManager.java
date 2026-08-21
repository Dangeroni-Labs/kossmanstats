package net.infstudio.gokistats.fabric.config;

import java.nio.file.Path;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.config.KossmanBalance;

public final class KossmanServerConfigManager {
	private static final String FILE_NAME = "kossmanstats.json";

	private KossmanServerConfigManager() {
	}

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> KossmanBalance.setCurrent(
				KossmanServerConfigLoader.load(configPath(), warning -> KossmanStats.LOGGER.warn(warning))
		));
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> KossmanBalance.reset());
	}

	private static Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}
}
