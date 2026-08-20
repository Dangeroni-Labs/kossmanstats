package net.infstudio.gokistats.fabric;

import net.infstudio.gokistats.KossmanStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class KossmanTags {
	public static final TagKey<Item> MINING_TOOLS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "mining")
	);
	public static final TagKey<Item> DIGGING_TOOLS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "digging")
	);
	public static final TagKey<Item> CHOPPING_TOOLS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "chopping")
	);
	public static final TagKey<Item> TRIMMING_TOOLS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "trimming")
	);
	public static final TagKey<Item> SWORDS = TagKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "sword")
	);

	private KossmanTags() {
	}
}
