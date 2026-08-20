package net.infstudio.gokistats.fabric.tag;

import net.infstudio.gokistats.KossmanStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class KossmanTags {
	public static final TagKey<Item> MINING_TOOLS = itemTag("mining");
	public static final TagKey<Item> DIGGING_TOOLS = itemTag("digging");
	public static final TagKey<Item> CHOPPING_TOOLS = itemTag("chopping");
	public static final TagKey<Item> TRIMMING_TOOLS = itemTag("trimming");
	public static final TagKey<Item> SWORDS = itemTag("sword");

	private KossmanTags() {
	}

	private static TagKey<Item> itemTag(String path) {
		return TagKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, path)
		);
	}
}
