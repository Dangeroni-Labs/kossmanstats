package net.infstudio.gokistats.fabric.loot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.formula.StatFormulas;
import net.infstudio.gokistats.fabric.state.KossmanPlayerStateStorage;
import net.infstudio.gokistats.fabric.tag.KossmanTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class BlockLootStatHooks {
	private static final Map<UUID, Set<String>> BROKEN_BLOCK_MARKERS = new ConcurrentHashMap<>();

	private BlockLootStatHooks() {
	}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register(BlockLootStatHooks::rememberBrokenBlock);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> BROKEN_BLOCK_MARKERS.remove(handler.player.getUUID()));
		LootTableEvents.MODIFY_DROPS.register((holder, context, drops) -> {
			ServerPlayer player = player(context);
			if (player == null) {
				return;
			}

			BlockState state = blockState(context);
			if (state == null) {
				return;
			}

			if (!consumeBrokenBlockMarker(player, context, state)) {
				return;
			}

			applyTreasureFinder(player, context, state, drops);
			applyMiningMagician(player, context, state, drops);
		});
	}

	private static void rememberBrokenBlock(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (player instanceof ServerPlayer serverPlayer) {
			BROKEN_BLOCK_MARKERS.computeIfAbsent(serverPlayer.getUUID(), ignored -> new HashSet<>())
					.add(brokenBlockMarker(pos, state));
		}
	}

	private static void applyTreasureFinder(ServerPlayer player, LootContext context, BlockState state, List<ItemStack> drops) {
		if (!state.is(KossmanTags.TREASURE_FINDER_BLOCKS)) {
			return;
		}

		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.TREASURE_FINDER);
		double chance = StatFormulas.treasureFinderProcChance(level);
		if (chance <= 0.0D || context.getRandom().nextDouble() >= chance) {
			return;
		}

		MinecraftServer server = context.getLevel().getServer();
		if (server == null) {
			return;
		}

		LootTable table = server.reloadableRegistries().getLootTable(treasureLootTableKey(state));
		List<ItemStack> extraLoot = table.getRandomItems(blockLootParams(context, player, state));
		if (!extraLoot.isEmpty()) {
			drops.addAll(extraLoot);
		}
	}

	private static void applyMiningMagician(ServerPlayer player, LootContext context, BlockState state, List<ItemStack> drops) {
		int level = KossmanPlayerStateStorage.getLevel(player, KossmanStatDefinitions.MINING_MAGICIAN);
		double chance = StatFormulas.miningMagicianProcChance(level);
		if (chance <= 0.0D || context.getRandom().nextDouble() >= chance) {
			return;
		}

		if (!state.is(KossmanTags.MAGICIAN_ORES) || hasSilkTouch(context)) {
			return;
		}

		ItemStack reward = extraMagicianReward(state.getBlock(), context, drops);
		if (!reward.isEmpty()) {
			drops.add(reward);
		}
	}

	private static ItemStack extraMagicianReward(Block brokenBlock, LootContext context, List<ItemStack> drops) {
		for (ItemStack drop : drops) {
			if (drop.isEmpty()) {
				continue;
			}

			if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == brokenBlock) {
				return randomMagicianOre(context);
			}
		}

		return randomMagicianItem(context);
	}

	private static LootParams blockLootParams(LootContext context, ServerPlayer player, BlockState state) {
		LootParams.Builder builder = new LootParams.Builder(context.getLevel())
				.withParameter(LootContextParams.ORIGIN, context.getParameter(LootContextParams.ORIGIN))
				.withParameter(LootContextParams.THIS_ENTITY, player)
				.withParameter(LootContextParams.BLOCK_STATE, state)
				.withParameter(LootContextParams.TOOL, context.getOptionalParameter(LootContextParams.TOOL));

		Object blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof BlockEntity entity) {
			builder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, entity);
		}

		return builder.create(LootContextParamSets.BLOCK);
	}

	private static boolean hasSilkTouch(LootContext context) {
		ItemInstance toolInstance = context.getOptionalParameter(LootContextParams.TOOL);
		if (!(toolInstance instanceof ItemStack tool)) {
			return false;
		}

		if (tool == null || tool.isEmpty()) {
			return false;
		}

		var silkTouch = context.getLevel().registryAccess()
				.lookupOrThrow(Registries.ENCHANTMENT)
				.getOrThrow(Enchantments.SILK_TOUCH);
		return EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0;
	}

	private static ResourceKey<LootTable> treasureLootTableKey(BlockState state) {
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		Identifier lootId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "treasure_finder/" + blockId.getPath());
		return ResourceKey.create(Registries.LOOT_TABLE, lootId);
	}

	private static ItemStack randomMagicianOre(LootContext context) {
		return context.getLevel().registryAccess()
				.lookupOrThrow(Registries.BLOCK)
				.get(KossmanTags.MAGICIAN_ORES)
				.flatMap(tag -> tag.getRandomElement(context.getRandom()))
				.map(holder -> new ItemStack(holder.value().asItem(), 1))
				.orElse(ItemStack.EMPTY);
	}

	private static ItemStack randomMagicianItem(LootContext context) {
		return context.getLevel().registryAccess()
				.lookupOrThrow(Registries.ITEM)
				.get(KossmanTags.MAGICIAN_ITEMS)
				.flatMap(tag -> tag.getRandomElement(context.getRandom()))
				.map(holder -> new ItemStack(holder.value(), 1))
				.orElse(ItemStack.EMPTY);
	}

	private static ServerPlayer player(LootContext context) {
		Object entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
		return entity instanceof ServerPlayer player ? player : null;
	}

	private static BlockState blockState(LootContext context) {
		Object state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
		return state instanceof BlockState blockState ? blockState : null;
	}

	private static boolean consumeBrokenBlockMarker(ServerPlayer player, LootContext context, BlockState state) {
		Object origin = context.getOptionalParameter(LootContextParams.ORIGIN);
		if (!(origin instanceof net.minecraft.world.phys.Vec3 pos)) {
			return false;
		}

		String marker = brokenBlockMarker(BlockPos.containing(pos), state);
		Set<String> markers = BROKEN_BLOCK_MARKERS.get(player.getUUID());
		if (markers == null || !markers.remove(marker)) {
			return false;
		}

		if (markers.isEmpty()) {
			BROKEN_BLOCK_MARKERS.remove(player.getUUID());
		}

		return true;
	}

	private static String brokenBlockMarker(BlockPos pos, BlockState state) {
		Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		return KossmanStats.MOD_ID + ":broken_block:" + id + ":" + pos.asLong();
	}
}
