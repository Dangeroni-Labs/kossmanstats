package net.infstudio.gokistats.core.config;

public record KossmanStatTuning(boolean enabled, double effectMultiplier) {
	public static final KossmanStatTuning DEFAULT = new KossmanStatTuning(true, 1.0D);
}
