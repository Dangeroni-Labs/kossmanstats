package net.infstudio.gokistats.fabric.loot;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MiningPerkDropProcessorTest {
	@BeforeAll
	static void bootstrapMinecraft() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void resolvesExpectedSmeltedItemsForEligibleMetalOreDrops() {
		assertSame(Items.IRON_INGOT, MiningPerkDropProcessor.smeltedResult(Items.RAW_IRON));
		assertSame(Items.GOLD_INGOT, MiningPerkDropProcessor.smeltedResult(Items.RAW_GOLD));
		assertSame(Items.COPPER_INGOT, MiningPerkDropProcessor.smeltedResult(Items.RAW_COPPER));
	}

	@Test
	void leavesNonMetalItemsWithoutSmeltedMapping() {
		assertNull(MiningPerkDropProcessor.smeltedResult(Items.COBBLESTONE));
		assertNull(MiningPerkDropProcessor.smeltedResult(Items.DIAMOND));
	}
}
