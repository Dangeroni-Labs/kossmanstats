package net.infstudio.gokistats.fabric.progression;

import net.infstudio.gokistats.core.MiningStat;
import net.infstudio.gokistats.core.StatProgression;
import net.infstudio.gokistats.core.UpgradeResult;
import net.infstudio.gokistats.fabric.state.GokiPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;

public final class MiningUpgradeService {
	private MiningUpgradeService() {
	}

	public static int nextUpgradeCost(ServerPlayer player) {
		return StatProgression.upgradeCostForLevel(GokiPlayerStateStorage.getMiningLevel(player));
	}

	public static UpgradeResult upgrade(ServerPlayer player) {
		int level = GokiPlayerStateStorage.getMiningLevel(player);
		int cost = StatProgression.upgradeCostForLevel(level);

		if (level >= MiningStat.MAX_LEVEL) {
			return UpgradeResult.failure(level, cost, "Mining is already at max level " + MiningStat.MAX_LEVEL + ".");
		}

		if (totalExperience(player) < cost) {
			return UpgradeResult.failure(level, cost, "Not enough XP. Need " + cost + " XP.");
		}

		player.giveExperiencePoints(-cost);
		GokiPlayerStateStorage.incrementMiningLevel(player);

		return UpgradeResult.success(level + 1, cost);
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
