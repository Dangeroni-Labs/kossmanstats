package net.infstudio.gokistats.fabric.network;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.snapshot.StatSnapshot;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;

public final class StatSnapshotSync {
	private StatSnapshotSync() {
	}

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(StatSnapshotPayload.ID, StatSnapshotPayload.CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> send(handler.player));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (alive) {
				send(newPlayer);
			}
		});
	}

	public static void send(ServerPlayer player) {
		if (!ServerPlayNetworking.canSend(player, StatSnapshotPayload.ID)) {
			return;
		}

		ServerPlayNetworking.send(player, new StatSnapshotPayload(snapshotFor(player)));
	}

	private static StatSnapshot snapshotFor(ServerPlayer player) {
		Map<StatId, Integer> levels = new LinkedHashMap<>();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			levels.put(stat.id(), KossmanPlayerStateStorage.getLevel(player, stat));
		}

		return new StatSnapshot(levels);
	}
}
