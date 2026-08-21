package net.infstudio.gokistats.fabric.progression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatUpgradeServiceTest {
	@Test
	void totalExperienceIncludesPartialProgressWithinLevel() {
		assertEquals(12, StatUpgradeService.totalExperience(1, 0.5F, 9));
	}

	@Test
	void totalExperienceUsesExactVanillaThresholdsAcrossCurves() {
		assertEquals(0, StatUpgradeService.totalExperience(0, 0.0F, 7));
		assertEquals(352, StatUpgradeService.totalExperience(16, 0.0F, 37));
		assertEquals(1507, StatUpgradeService.totalExperience(31, 0.0F, 121));
	}
}
