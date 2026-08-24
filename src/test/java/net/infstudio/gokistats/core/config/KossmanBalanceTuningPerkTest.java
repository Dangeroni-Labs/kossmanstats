package net.infstudio.gokistats.core.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import org.junit.jupiter.api.Test;

class KossmanBalanceTuningPerkTest {
	@Test
	void miningPerkUnlocksAtLevelFortyFiveByDefault() {
		KossmanBalanceTuning tuning = KossmanBalanceTuning.DEFAULT;

		assertFalse(tuning.isPerkUnlocked(KossmanStatDefinitions.MINING, 44));
		assertTrue(tuning.isPerkUnlocked(KossmanStatDefinitions.MINING, 45));
	}

	@Test
	void diggingPerkUnlocksAtLevelTwentyFiveByDefault() {
		KossmanBalanceTuning tuning = KossmanBalanceTuning.DEFAULT;

		assertFalse(tuning.isPerkUnlocked(KossmanStatDefinitions.DIGGING, 24));
		assertTrue(tuning.isPerkUnlocked(KossmanStatDefinitions.DIGGING, 25));
	}
}
