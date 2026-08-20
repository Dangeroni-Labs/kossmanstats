package net.infstudio.gokistats.fabric.state;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.infstudio.gokistats.GokiStats;
import net.infstudio.gokistats.core.definition.GokiStatsDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class GokiPlayerStateStorage {
	private static final AttachmentType<Integer> MINING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(GokiStatsDefinitions.MINING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> HEALTH_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(GokiStatsDefinitions.HEALTH),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> DIGGING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(GokiStatsDefinitions.DIGGING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> CHOPPING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(GokiStatsDefinitions.CHOPPING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> TRIMMING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(GokiStatsDefinitions.TRIMMING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private GokiPlayerStateStorage() {
	}

	public static void register() {
		// Loads this class during mod initialization so Fabric knows the attachment before player data is read.
	}

	public static int getMiningLevel(ServerPlayer player) {
		return getLevel(player, GokiStatsDefinitions.MINING);
	}

	public static int getLevel(ServerPlayer player, StatDefinition stat) {
		return Math.max(0, player.getAttachedOrSet(attachmentFor(stat), 0));
	}

	public static void setMiningLevel(ServerPlayer player, int level) {
		setLevel(player, GokiStatsDefinitions.MINING, level);
	}

	public static void setLevel(ServerPlayer player, StatDefinition stat, int level) {
		player.setAttached(attachmentFor(stat), Math.max(0, level));
	}

	public static void incrementMiningLevel(ServerPlayer player) {
		incrementLevel(player, GokiStatsDefinitions.MINING);
	}

	public static void incrementLevel(ServerPlayer player, StatDefinition stat) {
		setLevel(player, stat, getLevel(player, stat) + 1);
	}

	private static Identifier attachmentId(StatDefinition stat) {
		return Identifier.fromNamespaceAndPath(GokiStats.MOD_ID, stat.commandName() + "_level");
	}

	private static AttachmentType<Integer> attachmentFor(StatDefinition stat) {
		if (stat.equals(GokiStatsDefinitions.MINING)) {
			return MINING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(GokiStatsDefinitions.HEALTH)) {
			return HEALTH_LEVEL_ATTACHMENT;
		}

		if (stat.equals(GokiStatsDefinitions.DIGGING)) {
			return DIGGING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(GokiStatsDefinitions.CHOPPING)) {
			return CHOPPING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(GokiStatsDefinitions.TRIMMING)) {
			return TRIMMING_LEVEL_ATTACHMENT;
		}

		throw new IllegalArgumentException("Unsupported stat: " + stat.id().value());
	}
}
