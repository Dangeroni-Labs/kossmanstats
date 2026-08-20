package net.infstudio.gokistats.fabric.state;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class KossmanPlayerStateStorage {
	private static final AttachmentType<Integer> MINING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.MINING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> HEALTH_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.HEALTH),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> DIGGING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.DIGGING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> CHOPPING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.CHOPPING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> TRIMMING_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.TRIMMING),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> SWORDSMANSHIP_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.SWORDSMANSHIP),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private static final AttachmentType<Integer> PUGILISM_LEVEL_ATTACHMENT = AttachmentRegistry.create(
			attachmentId(KossmanStatDefinitions.PUGILISM),
			builder -> builder
					.initializer(() -> 0)
					.persistent(Codec.INT)
					.copyOnDeath()
	);

	private KossmanPlayerStateStorage() {
	}

	public static void register() {
		// Loads this class during mod initialization so Fabric knows the attachment before player data is read.
	}

	public static int getMiningLevel(ServerPlayer player) {
		return getLevel(player, KossmanStatDefinitions.MINING);
	}

	public static int getLevel(ServerPlayer player, StatDefinition stat) {
		return Math.max(0, player.getAttachedOrSet(attachmentFor(stat), 0));
	}

	public static void setMiningLevel(ServerPlayer player, int level) {
		setLevel(player, KossmanStatDefinitions.MINING, level);
	}

	public static void setLevel(ServerPlayer player, StatDefinition stat, int level) {
		player.setAttached(attachmentFor(stat), Math.max(0, level));
	}

	public static void incrementMiningLevel(ServerPlayer player) {
		incrementLevel(player, KossmanStatDefinitions.MINING);
	}

	public static void incrementLevel(ServerPlayer player, StatDefinition stat) {
		setLevel(player, stat, getLevel(player, stat) + 1);
	}

	private static Identifier attachmentId(StatDefinition stat) {
		return Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, stat.commandName() + "_level");
	}

	private static AttachmentType<Integer> attachmentFor(StatDefinition stat) {
		if (stat.equals(KossmanStatDefinitions.MINING)) {
			return MINING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.HEALTH)) {
			return HEALTH_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.DIGGING)) {
			return DIGGING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.CHOPPING)) {
			return CHOPPING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.TRIMMING)) {
			return TRIMMING_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.SWORDSMANSHIP)) {
			return SWORDSMANSHIP_LEVEL_ATTACHMENT;
		}

		if (stat.equals(KossmanStatDefinitions.PUGILISM)) {
			return PUGILISM_LEVEL_ATTACHMENT;
		}

		throw new IllegalArgumentException("Unsupported stat: " + stat.id().value());
	}
}
