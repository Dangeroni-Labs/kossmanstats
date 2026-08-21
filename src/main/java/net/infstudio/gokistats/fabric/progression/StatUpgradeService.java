package net.infstudio.gokistats.fabric.progression;

import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.progression.StatProgression;
import net.infstudio.gokistats.core.result.StatChangeResult;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;

public final class StatUpgradeService {
	private StatUpgradeService() {
	}

	public static int nextUpgradeCost(ServerPlayer player, StatDefinition stat) {
		return StatProgression.upgradeCostForLevel(KossmanPlayerStateStorage.getLevel(player, stat));
	}

	public static StatChangeResult upgrade(ServerPlayer player, StatDefinition stat) {
		int level = KossmanPlayerStateStorage.getLevel(player, stat);
		int cost = StatProgression.upgradeCostForLevel(level);

		if (level >= stat.maxLevel()) {
			return StatChangeResult.failure(level, cost, stat.displayName() + " is already at max level " + stat.maxLevel() + ".");
		}

		if (totalExperience(player) < cost) {
			return StatChangeResult.failure(level, cost, "Not enough XP. Need " + cost + " XP.");
		}

		player.giveExperiencePoints(-cost);
		KossmanPlayerStateStorage.incrementLevel(player, stat);

		return StatChangeResult.upgradeSuccess(stat, level, level + 1, cost);
	}

	public static StatChangeResult downgrade(ServerPlayer player, StatDefinition stat) {
		int level = KossmanPlayerStateStorage.getLevel(player, stat);
		int refund = StatProgression.downgradeRefundForLevel(level);

		if (level <= 0) {
			return StatChangeResult.failure(level, refund, stat.displayName() + " is already at level 0.");
		}

		KossmanPlayerStateStorage.decrementLevel(player, stat);
		player.giveExperiencePoints(refund);
		return StatChangeResult.downgradeSuccess(stat, level, level - 1, refund);
	}

	private static int totalExperience(ServerPlayer player) {
		int level = player.experienceLevel;

		if (level >= 0 && level <= 15) {
			return (int) Math.round(Math.pow(level, 2) + 6 * level);
		}

		if (level <= 30) {
			return (int) Math.round(2.5D * Math.pow(level, 2) - 40.5D * level + 360.0D);
		}

		return (int) Math.round(4.5D * Math.pow(level, 2) - 162.5D * level + 2220.0D);
	}
}
