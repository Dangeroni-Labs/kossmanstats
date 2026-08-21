package net.infstudio.gokistats.fabric.state;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class KossmanPlayerStateStorage {
	private static final Map<StatDefinition, AttachmentType<Integer>> LEVEL_ATTACHMENTS = KossmanStatDefinitions.ALL.stream()
			.collect(Collectors.toUnmodifiableMap(stat -> stat, KossmanPlayerStateStorage::levelAttachment));

	private KossmanPlayerStateStorage() {
	}

	public static void register() {
		// Loads this class during mod initialization so Fabric knows the attachment before player data is read.
	}

	public static int getLevel(ServerPlayer player, StatDefinition stat) {
		return Math.max(0, player.getAttachedOrSet(attachmentFor(stat), 0));
	}

	public static void setLevel(ServerPlayer player, StatDefinition stat, int level) {
		player.setAttached(attachmentFor(stat), Math.max(0, level));
	}

	public static void incrementLevel(ServerPlayer player, StatDefinition stat) {
		setLevel(player, stat, getLevel(player, stat) + 1);
	}

	public static void decrementLevel(ServerPlayer player, StatDefinition stat) {
		setLevel(player, stat, getLevel(player, stat) - 1);
	}

	public static void decrementLevel(ServerPlayer player, StatDefinition stat, int amount, int minimumLevel) {
		int targetLevel = Math.max(minimumLevel, getLevel(player, stat) - Math.max(0, amount));
		setLevel(player, stat, targetLevel);
	}

	private static AttachmentType<Integer> levelAttachment(StatDefinition stat) {
		return AttachmentRegistry.create(
				attachmentId(stat),
				builder -> builder
						.initializer(() -> 0)
						.persistent(Codec.INT)
						.copyOnDeath()
		);
	}

	private static Identifier attachmentId(StatDefinition stat) {
		return Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, stat.commandName() + "_level");
	}

	private static AttachmentType<Integer> attachmentFor(StatDefinition stat) {
		AttachmentType<Integer> attachment = LEVEL_ATTACHMENTS.get(stat);
		if (attachment == null) {
			throw new IllegalArgumentException("Unsupported stat: " + stat.id().value());
		}

		return attachment;
	}
}
