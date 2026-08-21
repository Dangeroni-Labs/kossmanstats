package net.infstudio.gokistats.fabric.movement;

import java.util.function.ToIntFunction;
import net.infstudio.gokistats.client.state.ClientStatSnapshotCache;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class MovementHooks {
	private static final double VANILLA_SPRINT_JUMP_IMPULSE = 0.2D;

	private MovementHooks() {
	}

	public static float applyJumpPowerBonus(Player player, float jumpPower) {
		return withLevelProvider(player, levelProvider -> {
			if (!shouldApplySprintJumpBonus(player)) {
				return jumpPower;
			}

			double bonus = StatFormulas.leaperVerticalBonus(levelProvider.applyAsInt(KossmanStatDefinitions.LEAPER_V));
			if (bonus <= 0.0D) {
				return jumpPower;
			}

			return (float) (jumpPower * (1.0D + bonus));
		}, jumpPower);
	}

	public static void applyHorizontalJumpBonus(Player player) {
		withLevelProvider(player, levelProvider -> {
			if (!shouldApplySprintJumpBonus(player)) {
				return null;
			}

			double bonus = StatFormulas.leaperHorizontalBonus(levelProvider.applyAsInt(KossmanStatDefinitions.LEAPER_H));
			if (bonus <= 0.0D) {
				return null;
			}

			float yawRadians = player.getYRot() * ((float) Math.PI / 180.0F);
			Vec3 updated = player.getDeltaMovement().add(
					-Math.sin(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE * bonus,
					0.0D,
					Math.cos(yawRadians) * VANILLA_SPRINT_JUMP_IMPULSE * bonus
			);
			player.setDeltaMovement(updated);
			return null;
		}, null);
	}

	public static void applyClimbingBonus(Player player) {
		withLevelProvider(player, levelProvider -> {
			if (!shouldApplyClimbingBonus(player)) {
				return null;
			}

			double bonus = StatFormulas.climbingSpeedBonus(levelProvider.applyAsInt(KossmanStatDefinitions.CLIMBING));
			if (bonus <= 0.0D) {
				return null;
			}

			double upwardMovement = player.getDeltaMovement().y;
			player.move(MoverType.SELF, new Vec3(0.0D, upwardMovement * bonus, 0.0D));
			return null;
		}, null);
	}

	private static boolean shouldApplySprintJumpBonus(Player player) {
		return player.isSprinting();
	}

	private static boolean shouldApplyClimbingBonus(Player player) {
		return player.onClimbable()
				&& !player.isShiftKeyDown()
				&& player.getDeltaMovement().y > 0.0D;
	}

	private static <T> T withLevelProvider(Player player, java.util.function.Function<ToIntFunction<StatDefinition>, T> action, T fallback) {
		if (player instanceof ServerPlayer serverPlayer) {
			return action.apply(stat -> KossmanPlayerStateStorage.getLevel(serverPlayer, stat));
		}

		if (player.isLocalPlayer() && ClientStatSnapshotCache.hasSnapshot()) {
			return action.apply(ClientStatSnapshotCache::level);
		}

		return fallback;
	}
}
