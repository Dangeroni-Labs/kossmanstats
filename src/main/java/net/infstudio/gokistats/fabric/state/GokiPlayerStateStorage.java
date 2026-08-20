package net.infstudio.gokistats.fabric.state;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.infstudio.gokistats.GokiStats;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class GokiPlayerStateStorage {
	private static final AttachmentType<Integer> MINING_LEVEL = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(GokiStats.MOD_ID, "mining_level"),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private GokiPlayerStateStorage() {
	}

	public static void register() {
		// Loads this class during mod initialization so Fabric knows the attachment before player data is read.
	}

	public static int getMiningLevel(ServerPlayer player) {
		return Math.max(0, player.getAttachedOrSet(MINING_LEVEL, 0));
	}

	public static void setMiningLevel(ServerPlayer player, int level) {
		player.setAttached(MINING_LEVEL, Math.max(0, level));
	}

	public static void incrementMiningLevel(ServerPlayer player) {
		setMiningLevel(player, getMiningLevel(player) + 1);
	}
}
