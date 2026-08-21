package net.infstudio.gokistats.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.minecraft.server.level.ServerPlayer;

public final class KossmanBalanceSync {
	private KossmanBalanceSync() {
	}

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(KossmanBalancePayload.ID, KossmanBalancePayload.CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> send(handler.player));
	}

	public static void send(ServerPlayer player) {
		if (!ServerPlayNetworking.canSend(player, KossmanBalancePayload.ID)) {
			return;
		}

		ServerPlayNetworking.send(player, new KossmanBalancePayload(KossmanBalance.current()));
	}
}
