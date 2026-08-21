package net.infstudio.gokistats.fabric.progression;

import net.infstudio.gokistats.core.config.KossmanBalance;
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
		if (!KossmanBalance.current().isEnabled(stat)) {
			return StatChangeResult.failure(KossmanPlayerStateStorage.getLevel(player, stat), 0, stat.displayName() + " is disabled on this server.");
		}

		int level = KossmanPlayerStateStorage.getLevel(player, stat);
		int cost = StatProgression.upgradeCostForLevel(level);
		int maxLevel = KossmanBalance.current().maxStatLevel();

		if (level >= maxLevel) {
			return StatChangeResult.failure(level, cost, stat.displayName() + " is already at max level " + maxLevel + ".");
		}

		if (currentTotalExperience(player) < cost) {
			return StatChangeResult.failure(level, cost, "Not enough XP. Need " + cost + " XP.");
		}

		player.giveExperiencePoints(-cost);
		KossmanPlayerStateStorage.incrementLevel(player, stat);

		return StatChangeResult.upgradeSuccess(stat, level, level + 1, cost);
	}

	public static StatChangeResult downgrade(ServerPlayer player, StatDefinition stat) {
		if (!KossmanBalance.current().isEnabled(stat)) {
			return StatChangeResult.failure(KossmanPlayerStateStorage.getLevel(player, stat), 0, stat.displayName() + " is disabled on this server.");
		}

		int level = KossmanPlayerStateStorage.getLevel(player, stat);
		int refund = StatProgression.downgradeRefundForLevel(level);

		if (level <= 0) {
			return StatChangeResult.failure(level, refund, stat.displayName() + " is already at level 0.");
		}

		KossmanPlayerStateStorage.decrementLevel(player, stat);
		player.giveExperiencePoints(refund);
		return StatChangeResult.downgradeSuccess(stat, level, level - 1, refund);
	}

	static int currentTotalExperience(ServerPlayer player) {
		return totalExperience(player.experienceLevel, player.experienceProgress, player.getXpNeededForNextLevel());
	}

	static int totalExperience(int level, float progress, int xpNeededForNextLevel) {
		int clampedLevel = Math.max(0, level);
		float clampedProgress = Math.clamp(progress, 0.0F, 1.0F);
		int currentLevelFloor = experiencePointsForLevel(clampedLevel);
		return currentLevelFloor + Math.max(0, Math.round(clampedProgress * Math.max(0, xpNeededForNextLevel)));
	}

	private static int experiencePointsForLevel(int level) {
		if (level <= 15) {
			return level * level + 6 * level;
		}

		if (level <= 30) {
			return (int) Math.floor(2.5D * level * level - 40.5D * level + 360.0D);
		}

		return (int) Math.floor(4.5D * level * level - 162.5D * level + 2220.0D);
	}
}
