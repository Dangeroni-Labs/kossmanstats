package net.infstudio.gokistats.fabric.tag;

import net.infstudio.gokistats.KossmanStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;

public final class KossmanTags {
	public static final TagKey<Item> MINING_TOOLS = itemTag("mining");
	public static final TagKey<Item> DIGGING_TOOLS = itemTag("digging");
	public static final TagKey<Item> CHOPPING_TOOLS = itemTag("chopping");
	public static final TagKey<Item> TRIMMING_TOOLS = itemTag("trimming");
	public static final TagKey<Item> SWORDS = itemTag("sword");
	public static final TagKey<Item> BOWS = itemTag("bow");
	public static final TagKey<Item> MAGICIAN_ITEMS = itemTag("magician_item");
	public static final TagKey<Block> MAGICIAN_ORES = blockTag("magician_ore");
	public static final TagKey<Block> MASTER_MINER_ORES = blockTag("master_miner_ore");
	public static final TagKey<Block> TREASURE_FINDER_BLOCKS = blockTag("treasure_finder");

	private KossmanTags() {
	}

	private static TagKey<Item> itemTag(String path) {
		return TagKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, path)
		);
	}

	private static TagKey<Block> blockTag(String path) {
		return TagKey.create(
				Registries.BLOCK,
				Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, path)
		);
	}
}
