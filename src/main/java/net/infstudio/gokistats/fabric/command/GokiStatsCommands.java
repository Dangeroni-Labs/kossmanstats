package net.infstudio.gokistats.fabric.command;

import static net.minecraft.commands.Commands.literal;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.infstudio.gokistats.fabric.progression.MiningUpgradeService;
import net.infstudio.gokistats.fabric.state.GokiPlayerStateStorage;
import net.minecraft.network.chat.Component;

public final class GokiStatsCommands {
	private GokiStatsCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				literal("gokistats")
						.then(literal("get")
								.then(literal("mining")
										.executes(context -> {
											var player = context.getSource().getPlayerOrException();
											int level = GokiPlayerStateStorage.getMiningLevel(player);
											int cost = MiningUpgradeService.nextUpgradeCost(player);

											context.getSource().sendSuccess(
													() -> Component.literal("Mining level: " + level + ", next cost: " + cost + " XP"),
													false
											);
											return level;
										})))
						.then(literal("add")
								.then(literal("mining")
										.executes(context -> {
											var player = context.getSource().getPlayerOrException();
											var result = MiningUpgradeService.upgrade(player);

											if (result.upgraded()) {
												context.getSource().sendSuccess(
														() -> Component.literal(result.message() + " Cost: " + result.cost() + " XP."),
														false
												);
												return result.level();
											}

											context.getSource().sendFailure(Component.literal(result.message()));
											return 0;
										})))
		));
	}
}
