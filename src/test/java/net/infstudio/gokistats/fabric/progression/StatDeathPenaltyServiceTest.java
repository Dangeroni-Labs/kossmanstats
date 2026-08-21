package net.infstudio.gokistats.fabric.progression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatDeathPenaltyServiceTest {
	@Test
	void finalLevelDoesNotIncreaseStatsBelowRetainedMinimum() {
		assertEquals(1, StatDeathPenaltyService.finalLevelAfterPenalty(1, 0, 2));
	}

	@Test
	void finalLevelStopsAtRetainedMinimumForEligibleStats() {
		assertEquals(1, StatDeathPenaltyService.finalLevelAfterPenalty(4, 3, 1));
	}

	@Test
	void finalLevelCanStillReachZeroFromZeroOrNegativeInputs() {
		assertEquals(0, StatDeathPenaltyService.finalLevelAfterPenalty(0, 3, 1));
		assertEquals(0, StatDeathPenaltyService.finalLevelAfterPenalty(-2, 1, 1));
	}
}
