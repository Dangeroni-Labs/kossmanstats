package net.infstudio.gokistats.fabric.loot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public final class MiningPerkDropProcessor {
	private static final Map<Item, Item> SMELTED_RESULTS = new LinkedHashMap<>();

	static {
		SMELTED_RESULTS.put(Items.RAW_IRON, Items.IRON_INGOT);
		SMELTED_RESULTS.put(Items.RAW_GOLD, Items.GOLD_INGOT);
		SMELTED_RESULTS.put(Items.RAW_COPPER, Items.COPPER_INGOT);
	}

	private MiningPerkDropProcessor() {
	}

	public static void apply(ServerPlayer player, BlockState state, List<ItemStack> drops) {
		if (!isUnlocked(player) || !state.is(KossmanTags.MASTER_MINER_ORES)) {
			return;
		}

		boolean changed = false;
		for (int index = 0; index < drops.size(); index++) {
			ItemStack original = drops.get(index);
			ItemStack smelted = smeltedStack(original);
			if (smelted == original) {
				continue;
			}

			drops.set(index, smelted);
			changed = true;
		}

		if (!changed) {
			return;
		}

		drops.removeIf(ItemStack::isEmpty);
	}

	public static boolean isUnlocked(ServerPlayer player) {
		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.MINING);
		return KossmanBalance.current().isPerkUnlocked(KossmanStatDefinitions.MINING, level);
	}

	static ItemStack smeltedStack(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		Item smeltedItem = smeltedResult(stack.getItem());
		if (smeltedItem == null) {
			return stack;
		}

		return new ItemStack(smeltedItem, stack.getCount());
	}

	static Item smeltedResult(Item item) {
		return SMELTED_RESULTS.get(item);
	}
}
