package net.infstudio.gokistats.core.config;

public record KossmanXpTuning(
		double costMultiplier,
		double baseOffset,
		double linearScale,
		double quadraticScale,
		double cubicScale,
		double midgameRampStart,
		double midgameRampScale,
		double lategameRampStart,
		double lategameRampScale,
		double endgameRampStart,
		double endgameRampScale
) {
	public static final KossmanXpTuning DEFAULT = new KossmanXpTuning(
			1.0D,
			8.0D,
			1.0D,
			0.32D,
			0.003D,
			25.0D,
			1.2D,
			35.0D,
			4.0D,
			45.0D,
			40.0D
	);
}
