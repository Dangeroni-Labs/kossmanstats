package net.infstudio.gokistats.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.infstudio.gokistats.client.state.ClientStatSnapshotCache;
import net.infstudio.gokistats.fabric.network.StatSnapshotPayload;

public final class KossmanStatsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(StatSnapshotPayload.ID, (payload, context) ->
				context.client().execute(() -> ClientStatSnapshotCache.replace(payload.snapshot()))
		);
	}
}
