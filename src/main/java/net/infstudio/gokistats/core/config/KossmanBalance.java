package net.infstudio.gokistats.core.config;

public final class KossmanBalance {
	private static volatile KossmanBalanceTuning current = KossmanBalanceTuning.DEFAULT;

	private KossmanBalance() {
	}

	public static KossmanBalanceTuning current() {
		return current;
	}

	public static void setCurrent(KossmanBalanceTuning tuning) {
		current = tuning == null ? KossmanBalanceTuning.DEFAULT : tuning;
	}

	public static void reset() {
		current = KossmanBalanceTuning.DEFAULT;
	}
}
