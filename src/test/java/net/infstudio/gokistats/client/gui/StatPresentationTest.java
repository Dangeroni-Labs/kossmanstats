package net.infstudio.gokistats.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.config.KossmanBalanceTuning;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class StatPresentationTest {
	@AfterEach
	void resetBalance() {
		KossmanBalance.reset();
	}

	@Test
	void miningDisplayNameSwitchesAtPerkUnlockLevel() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		assertEquals("Mining", StatPresentation.displayName(KossmanStatDefinitions.MINING, 44));
		assertEquals("Master Miner", StatPresentation.displayName(KossmanStatDefinitions.MINING, 45));
	}

	@Test
	void miningTooltipShowsLockedAndUnlockedPerkLines() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		List<Component> locked = StatTooltipContent.forStat(KossmanStatDefinitions.MINING, 44);
		List<Component> unlocked = StatTooltipContent.forStat(KossmanStatDefinitions.MINING, 45);

		assertEquals("Perk unlocks at Lv. 45", locked.get(4).getString());
		assertEquals("Perk: Metal ores drop smelted output.", unlocked.get(4).getString());
	}

	@Test
	void diggingDisplayNameSwitchesAtPerkUnlockLevel() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		assertEquals("Digging", StatPresentation.displayName(KossmanStatDefinitions.DIGGING, 24));
		assertEquals("Momentum", StatPresentation.displayName(KossmanStatDefinitions.DIGGING, 25));
	}

	@Test
	void diggingTooltipShowsLockedAndUnlockedPerkLines() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		List<Component> locked = StatTooltipContent.forStat(KossmanStatDefinitions.DIGGING, 24);
		List<Component> unlocked = StatTooltipContent.forStat(KossmanStatDefinitions.DIGGING, 25);

		assertEquals("Perk unlocks at Lv. 25", locked.get(4).getString());
		assertEquals("Perk: Consecutive digging increases digging speed.", unlocked.get(4).getString());
	}

	@Test
	void choppingDisplayNameSwitchesAtPerkUnlockLevel() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		assertEquals("Chopping", StatPresentation.displayName(KossmanStatDefinitions.CHOPPING, 44));
		assertEquals("Lumberjack", StatPresentation.displayName(KossmanStatDefinitions.CHOPPING, 45));
	}

	@Test
	void choppingTooltipShowsLockedAndUnlockedPerkLines() {
		KossmanBalance.setCurrent(KossmanBalanceTuning.DEFAULT);

		List<Component> locked = StatTooltipContent.forStat(KossmanStatDefinitions.CHOPPING, 44);
		List<Component> unlocked = StatTooltipContent.forStat(KossmanStatDefinitions.CHOPPING, 45);

		assertEquals("Perk unlocks at Lv. 45", locked.get(4).getString());
		assertEquals("Perk: Chopping one log can fell the whole tree.", unlocked.get(4).getString());
	}
}
