package net.infstudio.gokistats.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.result.UpgradeResult;
import net.infstudio.gokistats.fabric.effect.StatEffects;
import net.infstudio.gokistats.fabric.progression.StatUpgradeService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class StatUpgradeRequests {
	private StatUpgradeRequests() {
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(StatUpgradeRequestPayload.ID, StatUpgradeRequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(StatUpgradeRequestPayload.ID, StatUpgradeRequests::handle);
	}

	private static void handle(StatUpgradeRequestPayload payload, ServerPlayNetworking.Context context) {
		context.server().execute(() -> upgrade(context.player(), payload));
	}

	private static void upgrade(ServerPlayer player, StatUpgradeRequestPayload payload) {
		StatId statId;
		try {
			statId = new StatId(payload.statId());
		} catch (IllegalArgumentException exception) {
			player.sendSystemMessage(Component.literal("Invalid stat upgrade request."));
			return;
		}

		StatDefinition stat = KossmanStatDefinitions.byId(statId).orElse(null);
		if (stat == null) {
			player.sendSystemMessage(Component.literal("Unknown stat: " + statId.value()));
			return;
		}

		UpgradeResult result = StatUpgradeService.upgrade(player, stat);
		if (!result.upgraded()) {
			player.sendSystemMessage(Component.literal(result.message()));
			return;
		}

		StatEffects.afterUpgrade(player, stat);
		StatSnapshotSync.send(player);
	}
}
