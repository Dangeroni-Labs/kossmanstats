package net.infstudio.gokistats.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.infstudio.gokistats.client.input.KossmanStatsKeybinds;
import net.infstudio.gokistats.client.state.ClientStatSnapshotCache;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.fabric.network.KossmanBalancePayload;
import net.infstudio.gokistats.fabric.network.StatSnapshotPayload;

public final class KossmanStatsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KossmanStatsKeybinds.register();
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			ClientStatSnapshotCache.clear();
			KossmanBalance.reset();
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ClientStatSnapshotCache.clear();
			KossmanBalance.reset();
		});
		ClientPlayNetworking.registerGlobalReceiver(KossmanBalancePayload.ID, (payload, context) ->
				context.client().execute(() -> KossmanBalance.setCurrent(payload.tuning()))
		);
		ClientPlayNetworking.registerGlobalReceiver(StatSnapshotPayload.ID, (payload, context) ->
				context.client().execute(() -> ClientStatSnapshotCache.replace(payload.snapshot()))
		);
	}
}
