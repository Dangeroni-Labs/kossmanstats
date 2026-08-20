package net.infstudio.gokistats.fabric.network;

import net.infstudio.gokistats.KossmanStats;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatUpgradeRequestPayload(String statId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<StatUpgradeRequestPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "upgrade_stat")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, StatUpgradeRequestPayload> CODEC =
			CustomPacketPayload.codec(StatUpgradeRequestPayload::write, StatUpgradeRequestPayload::read);

	private static StatUpgradeRequestPayload read(RegistryFriendlyByteBuf buf) {
		return new StatUpgradeRequestPayload(buf.readUtf(128));
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeUtf(statId, 128);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
