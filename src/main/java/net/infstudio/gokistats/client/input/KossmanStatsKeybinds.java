package net.infstudio.gokistats.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.client.gui.KossmanStatsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class KossmanStatsKeybinds {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "controls")
	);
	private static final KeyMapping OPEN_STATS_SCREEN = new KeyMapping(
			"key.gokistats.open_stats",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_K,
			CATEGORY
	);

	private KossmanStatsKeybinds() {
	}

	public static void register() {
		KeyMappingHelper.registerKeyMapping(OPEN_STATS_SCREEN);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (OPEN_STATS_SCREEN.consumeClick()) {
				if (client.player != null) {
					client.setScreenAndShow(new KossmanStatsScreen());
				}
			}
		});
	}
}
