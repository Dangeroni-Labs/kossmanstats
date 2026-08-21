package net.infstudio.gokistats.fabric.command;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.fabric.effect.StatEffects;
import net.infstudio.gokistats.fabric.network.StatSnapshotSync;
import net.infstudio.gokistats.fabric.progression.StatUpgradeService;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class KossmanStatsCommands {
	private KossmanStatsCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			var get = literal("get");
			var add = literal("add");

			for (StatDefinition stat : KossmanStatDefinitions.ALL) {
				get.then(getCommand(stat));
				add.then(addCommand(stat));
			}

			dispatcher.register(literal("gokistats")
					.then(get)
					.then(add));
		});
	}

	private static LiteralArgumentBuilder<CommandSourceStack> getCommand(StatDefinition stat) {
		return literal(stat.commandName())
				.executes(context -> {
					var player = context.getSource().getPlayerOrException();
					int level = KossmanPlayerStateStorage.getLevel(player, stat);
					int cost = StatUpgradeService.nextUpgradeCost(player, stat);

					context.getSource().sendSuccess(
							() -> Component.literal(stat.displayName() + " level: " + level + ", next cost: " + cost + " XP"),
							false
					);
					return level;
				});
	}

	private static LiteralArgumentBuilder<CommandSourceStack> addCommand(StatDefinition stat) {
		return literal(stat.commandName())
				.executes(context -> {
					var player = context.getSource().getPlayerOrException();
					var result = StatUpgradeService.upgrade(player, stat);

					if (result.changed()) {
						StatEffects.afterProgressionChange(player, stat);
						StatSnapshotSync.send(player);
						context.getSource().sendSuccess(
								() -> Component.literal(result.message() + " Cost: " + result.xpAmount() + " XP."),
								false
						);
						return result.currentLevel();
					}

					context.getSource().sendFailure(Component.literal(result.message()));
					return 0;
				});
	}
}
