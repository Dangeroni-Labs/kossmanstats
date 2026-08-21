package net.infstudio.gokistats.fabric.network;

import net.infstudio.gokistats.KossmanStats;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatDowngradeRequestPayload(String statId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<StatDowngradeRequestPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "downgrade_stat")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, StatDowngradeRequestPayload> CODEC =
			CustomPacketPayload.codec(StatDowngradeRequestPayload::write, StatDowngradeRequestPayload::read);

	private static StatDowngradeRequestPayload read(RegistryFriendlyByteBuf buf) {
		return new StatDowngradeRequestPayload(buf.readUtf(128));
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeUtf(statId, 128);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
