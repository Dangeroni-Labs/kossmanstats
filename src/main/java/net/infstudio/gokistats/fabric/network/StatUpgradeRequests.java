package net.infstudio.gokistats.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.definition.StatId;
import net.infstudio.gokistats.core.result.StatChangeResult;
import net.infstudio.gokistats.fabric.effect.StatEffects;
import net.infstudio.gokistats.fabric.progression.StatUpgradeService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class StatUpgradeRequests {
	private StatUpgradeRequests() {
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(StatUpgradeRequestPayload.ID, StatUpgradeRequestPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(StatDowngradeRequestPayload.ID, StatDowngradeRequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(StatUpgradeRequestPayload.ID, StatUpgradeRequests::handleUpgrade);
		ServerPlayNetworking.registerGlobalReceiver(StatDowngradeRequestPayload.ID, StatUpgradeRequests::handleDowngrade);
	}

	private static void handleUpgrade(StatUpgradeRequestPayload payload, ServerPlayNetworking.Context context) {
		context.server().execute(() -> upgrade(context.player(), payload));
	}

	private static void handleDowngrade(StatDowngradeRequestPayload payload, ServerPlayNetworking.Context context) {
		context.server().execute(() -> downgrade(context.player(), payload));
	}

	private static void upgrade(ServerPlayer player, StatUpgradeRequestPayload payload) {
		StatDefinition stat = resolveStat(player, payload.statId(), "upgrade");
		if (stat == null) {
			return;
		}

		StatChangeResult result = StatUpgradeService.upgrade(player, stat);
		applyResult(player, stat, result);
	}

	private static void downgrade(ServerPlayer player, StatDowngradeRequestPayload payload) {
		StatDefinition stat = resolveStat(player, payload.statId(), "downgrade");
		if (stat == null) {
			return;
		}

		StatChangeResult result = StatUpgradeService.downgrade(player, stat);
		applyResult(player, stat, result);
	}

	private static StatDefinition resolveStat(ServerPlayer player, String rawStatId, String action) {
		StatId statId;
		try {
			statId = new StatId(rawStatId);
		} catch (IllegalArgumentException exception) {
			player.sendSystemMessage(Component.literal("Invalid stat " + action + " request."));
			return null;
		}

		StatDefinition stat = KossmanStatDefinitions.byId(statId).orElse(null);
		if (stat == null) {
			player.sendSystemMessage(Component.literal("Unknown stat: " + statId.value()));
			return null;
		}

		return stat;
	}

	private static void applyResult(ServerPlayer player, StatDefinition stat, StatChangeResult result) {
		if (!result.changed()) {
			player.sendSystemMessage(Component.literal(result.message()));
			return;
		}

		StatEffects.afterProgressionChange(player, stat);
		StatSnapshotSync.send(player);
	}
}
