package net.infstudio.gokistats.fabric.perk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.infstudio.gokistats.fabric.perk.MomentumPerkHandler.MomentumState;
import org.junit.jupiter.api.Test;

class MomentumPerkHandlerTest {
	@Test
	void firstEligibleBreakStartsWithoutBonusStacks() {
		MomentumState momentum = MomentumPerkHandler.recordEligibleBreak(null, 100L);

		assertEquals(0, momentum.stacks());
		assertEquals(0.0D, MomentumPerkHandler.bonusForStacks(momentum.stacks()));
	}

	@Test
	void consecutiveEligibleBreaksIncreaseStacksUpToMaximum() {
		MomentumState momentum = null;

		for (int index = 0; index < 10; index++) {
			momentum = MomentumPerkHandler.recordEligibleBreak(momentum, 100L + index);
		}

		assertEquals(5, momentum.stacks());
		assertEquals(0.20D, MomentumPerkHandler.bonusForStacks(momentum.stacks()));
	}

	@Test
	void expiredMomentumResetsNextEligibleBreakToZeroStacks() {
		MomentumState momentum = MomentumPerkHandler.recordEligibleBreak(null, 100L);
		momentum = MomentumPerkHandler.recordEligibleBreak(momentum, 101L);

		assertTrue(MomentumPerkHandler.isExpired(momentum, 142L, MomentumPerkHandler.RESET_WINDOW_TICKS));

		MomentumState reset = MomentumPerkHandler.recordEligibleBreak(momentum, 142L);
		assertEquals(0, reset.stacks());
	}
}
