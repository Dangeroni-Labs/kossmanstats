package net.infstudio.gokistats.core.config;

public record KossmanDeathPenaltyTuning(
		double lossRate,
		int minimumLoss,
		int maximumLoss,
		int minimumRetainedStatLevel
) {
	public static final KossmanDeathPenaltyTuning DEFAULT = new KossmanDeathPenaltyTuning(0.10D, 1, 5, 1);
}
