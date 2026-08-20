package net.infstudio.gokistats.fabric.network;

import java.util.LinkedHashMap;
import java.util.Map;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.snapshot.StatSnapshot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatSnapshotPayload(StatSnapshot snapshot) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<StatSnapshotPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "stat_snapshot")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, StatSnapshotPayload> CODEC =
			CustomPacketPayload.codec(StatSnapshotPayload::write, StatSnapshotPayload::read);

	private static StatSnapshotPayload read(RegistryFriendlyByteBuf buf) {
		int count = buf.readVarInt();
		Map<StatId, Integer> levels = new LinkedHashMap<>();

		for (int index = 0; index < count; index++) {
			String id = buf.readUtf(128);
			int level = Math.max(0, buf.readVarInt());
			try {
				levels.put(new StatId(id), level);
			} catch (IllegalArgumentException ignored) {
				// Ignore malformed stat identifiers from the remote side.
			}
		}

		return new StatSnapshotPayload(new StatSnapshot(levels));
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(snapshot.levels().size());

		for (Map.Entry<StatId, Integer> entry : snapshot.levels().entrySet()) {
			buf.writeUtf(entry.getKey().value(), 128);
			buf.writeVarInt(Math.max(0, entry.getValue()));
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
