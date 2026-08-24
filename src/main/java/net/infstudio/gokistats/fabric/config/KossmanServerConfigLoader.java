package net.infstudio.gokistats.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.infstudio.gokistats.core.config.KossmanBalanceTuning;
import net.infstudio.gokistats.core.config.KossmanDeathPenaltyTuning;
import net.infstudio.gokistats.core.config.KossmanStatTuning;
import net.infstudio.gokistats.core.config.KossmanXpTuning;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;

public final class KossmanServerConfigLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private KossmanServerConfigLoader() {
	}

	public static KossmanBalanceTuning load(Path path, Consumer<String> warningSink) {
		if (Files.notExists(path)) {
			writeDefault(path, warningSink);
			return KossmanBalanceTuning.DEFAULT;
		}

		RawConfig rawConfig;
		try (Reader reader = Files.newBufferedReader(path)) {
			rawConfig = GSON.fromJson(reader, RawConfig.class);
		} catch (IOException | JsonParseException exception) {
			warningSink.accept("Failed to read " + path.getFileName() + "; using defaults. " + exception.getMessage());
			return KossmanBalanceTuning.DEFAULT;
		}

		return validate(rawConfig, warningSink);
	}

	private static void writeDefault(Path path, Consumer<String> warningSink) {
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(RawConfig.defaults(), writer);
			}
		} catch (IOException exception) {
			warningSink.accept("Failed to create default config at " + path + ". " + exception.getMessage());
		}
	}

	private static KossmanBalanceTuning validate(RawConfig raw, Consumer<String> warningSink) {
		if (raw == null) {
			warningSink.accept("Config file was empty; using defaults.");
			return KossmanBalanceTuning.DEFAULT;
		}

		List<String> warnings = new ArrayList<>();
		int maxStatLevel = positiveInt(raw.maxStatLevel, KossmanBalanceTuning.DEFAULT.maxStatLevel(), "maxStatLevel", warnings);
		double downgradeRefundRate = boundedDouble(raw.downgradeRefundRate, 0.0D, 1.0D, KossmanBalanceTuning.DEFAULT.downgradeRefundRate(), "downgradeRefundRate", warnings);
		KossmanXpTuning xp = validateXp(raw.xp, warnings);
		KossmanDeathPenaltyTuning deathPenalty = validateDeathPenalty(raw.deathPenalty, warnings);
		Map<String, KossmanStatTuning> statTunings = validateStats(raw.stats, warnings);

		for (String warning : warnings) {
			warningSink.accept(warning);
		}

		return new KossmanBalanceTuning(maxStatLevel, xp, downgradeRefundRate, deathPenalty, statTunings);
	}

	private static KossmanXpTuning validateXp(RawXpTuning raw, List<String> warnings) {
		RawXpTuning source = raw == null ? RawXpTuning.defaults() : raw;
		KossmanXpTuning defaults = KossmanXpTuning.DEFAULT;
		return new KossmanXpTuning(
				nonNegativeDouble(source.costMultiplier, defaults.costMultiplier(), "xp.costMultiplier", warnings),
				nonNegativeDouble(source.baseOffset, defaults.baseOffset(), "xp.baseOffset", warnings),
				nonNegativeDouble(source.linearScale, defaults.linearScale(), "xp.linearScale", warnings),
				nonNegativeDouble(source.quadraticScale, defaults.quadraticScale(), "xp.quadraticScale", warnings),
				nonNegativeDouble(source.cubicScale, defaults.cubicScale(), "xp.cubicScale", warnings),
				nonNegativeDouble(source.midgameRampStart, defaults.midgameRampStart(), "xp.midgameRampStart", warnings),
				nonNegativeDouble(source.midgameRampScale, defaults.midgameRampScale(), "xp.midgameRampScale", warnings),
				nonNegativeDouble(source.lategameRampStart, defaults.lategameRampStart(), "xp.lategameRampStart", warnings),
				nonNegativeDouble(source.lategameRampScale, defaults.lategameRampScale(), "xp.lategameRampScale", warnings),
				nonNegativeDouble(source.endgameRampStart, defaults.endgameRampStart(), "xp.endgameRampStart", warnings),
				nonNegativeDouble(source.endgameRampScale, defaults.endgameRampScale(), "xp.endgameRampScale", warnings)
		);
	}

	private static KossmanDeathPenaltyTuning validateDeathPenalty(RawDeathPenaltyTuning raw, List<String> warnings) {
		RawDeathPenaltyTuning source = raw == null ? RawDeathPenaltyTuning.defaults() : raw;
		KossmanDeathPenaltyTuning defaults = KossmanDeathPenaltyTuning.DEFAULT;

		double lossRate = boundedDouble(source.lossRate, 0.0D, 1.0D, defaults.lossRate(), "deathPenalty.lossRate", warnings);
		int minimumLoss = nonNegativeInt(source.minimumLoss, defaults.minimumLoss(), "deathPenalty.minimumLoss", warnings);
		int maximumLoss = positiveInt(source.maximumLoss, defaults.maximumLoss(), "deathPenalty.maximumLoss", warnings);
		int minimumRetainedStatLevel = nonNegativeInt(
				source.minimumRetainedStatLevel,
				defaults.minimumRetainedStatLevel(),
				"deathPenalty.minimumRetainedStatLevel",
				warnings
		);

		if (maximumLoss < minimumLoss) {
			warnings.add("deathPenalty.maximumLoss was lower than minimumLoss; using defaults.");
			minimumLoss = defaults.minimumLoss();
			maximumLoss = defaults.maximumLoss();
		}

		return new KossmanDeathPenaltyTuning(lossRate, minimumLoss, maximumLoss, minimumRetainedStatLevel);
	}

	private static Map<String, KossmanStatTuning> validateStats(Map<String, RawStatTuning> rawStats, List<String> warnings) {
		Map<String, KossmanStatTuning> stats = new LinkedHashMap<>();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			KossmanStatTuning defaults = KossmanStatTuning.defaultsFor(stat);
			RawStatTuning raw = rawStats == null ? null : rawStats.get(stat.commandName());
			boolean enabled = raw == null || raw.enabled == null ? defaults.enabled() : raw.enabled;
			double effectMultiplier = raw == null
					? defaults.effectMultiplier()
					: nonNegativeDouble(raw.effectMultiplier, defaults.effectMultiplier(), "stats." + stat.commandName() + ".effectMultiplier", warnings);
			boolean perkEnabled = raw == null || raw.perkEnabled == null ? defaults.perkEnabled() : raw.perkEnabled;
			int perkUnlockLevel = raw == null
					? defaults.perkUnlockLevel()
					: positiveInt(raw.perkUnlockLevel, defaults.perkUnlockLevel(), "stats." + stat.commandName() + ".perkUnlockLevel", warnings);
			stats.put(stat.commandName(), new KossmanStatTuning(enabled, effectMultiplier, perkEnabled, perkUnlockLevel));
		}

		return stats;
	}

	private static int positiveInt(Number raw, int fallback, String field, List<String> warnings) {
		if (raw == null) {
			return fallback;
		}

		if (raw.intValue() <= 0) {
			warnings.add(field + " was invalid; using default " + fallback + ".");
			return fallback;
		}

		return raw.intValue();
	}

	private static int nonNegativeInt(Number raw, int fallback, String field, List<String> warnings) {
		if (raw == null) {
			return fallback;
		}

		if (raw.intValue() < 0) {
			warnings.add(field + " was invalid; using default " + fallback + ".");
			return fallback;
		}

		return raw.intValue();
	}

	private static double nonNegativeDouble(Number raw, double fallback, String field, List<String> warnings) {
		if (raw == null) {
			return fallback;
		}

		if (raw.doubleValue() < 0.0D || Double.isNaN(raw.doubleValue())) {
			warnings.add(field + " was invalid; using default " + fallback + ".");
			return fallback;
		}

		return raw.doubleValue();
	}

	private static double boundedDouble(Number raw, double min, double max, double fallback, String field, List<String> warnings) {
		if (raw == null) {
			return fallback;
		}

		if (Double.isNaN(raw.doubleValue()) || raw.doubleValue() < min || raw.doubleValue() > max) {
			warnings.add(field + " was outside " + min + ".." + max + "; using default " + fallback + ".");
			return fallback;
		}

		return raw.doubleValue();
	}

	static final class RawConfig {
		Number maxStatLevel;
		RawXpTuning xp;
		Number downgradeRefundRate;
		RawDeathPenaltyTuning deathPenalty;
		Map<String, RawStatTuning> stats;

		static RawConfig defaults() {
			RawConfig config = new RawConfig();
			config.maxStatLevel = KossmanBalanceTuning.DEFAULT.maxStatLevel();
			config.xp = RawXpTuning.defaults();
			config.downgradeRefundRate = KossmanBalanceTuning.DEFAULT.downgradeRefundRate();
			config.deathPenalty = RawDeathPenaltyTuning.defaults();
			config.stats = new LinkedHashMap<>();
			for (StatDefinition stat : KossmanStatDefinitions.ALL) {
				config.stats.put(stat.commandName(), RawStatTuning.defaults(stat));
			}
			return config;
		}
	}

	static final class RawXpTuning {
		Number costMultiplier;
		Number baseOffset;
		Number linearScale;
		Number quadraticScale;
		Number cubicScale;
		Number midgameRampStart;
		Number midgameRampScale;
		Number lategameRampStart;
		Number lategameRampScale;
		Number endgameRampStart;
		Number endgameRampScale;

		static RawXpTuning defaults() {
			RawXpTuning tuning = new RawXpTuning();
			tuning.costMultiplier = KossmanXpTuning.DEFAULT.costMultiplier();
			tuning.baseOffset = KossmanXpTuning.DEFAULT.baseOffset();
			tuning.linearScale = KossmanXpTuning.DEFAULT.linearScale();
			tuning.quadraticScale = KossmanXpTuning.DEFAULT.quadraticScale();
			tuning.cubicScale = KossmanXpTuning.DEFAULT.cubicScale();
			tuning.midgameRampStart = KossmanXpTuning.DEFAULT.midgameRampStart();
			tuning.midgameRampScale = KossmanXpTuning.DEFAULT.midgameRampScale();
			tuning.lategameRampStart = KossmanXpTuning.DEFAULT.lategameRampStart();
			tuning.lategameRampScale = KossmanXpTuning.DEFAULT.lategameRampScale();
			tuning.endgameRampStart = KossmanXpTuning.DEFAULT.endgameRampStart();
			tuning.endgameRampScale = KossmanXpTuning.DEFAULT.endgameRampScale();
			return tuning;
		}
	}

	static final class RawDeathPenaltyTuning {
		Number lossRate;
		Number minimumLoss;
		Number maximumLoss;
		Number minimumRetainedStatLevel;

		static RawDeathPenaltyTuning defaults() {
			RawDeathPenaltyTuning tuning = new RawDeathPenaltyTuning();
			tuning.lossRate = KossmanDeathPenaltyTuning.DEFAULT.lossRate();
			tuning.minimumLoss = KossmanDeathPenaltyTuning.DEFAULT.minimumLoss();
			tuning.maximumLoss = KossmanDeathPenaltyTuning.DEFAULT.maximumLoss();
			tuning.minimumRetainedStatLevel = KossmanDeathPenaltyTuning.DEFAULT.minimumRetainedStatLevel();
			return tuning;
		}
	}

	static final class RawStatTuning {
		Boolean enabled;
		Number effectMultiplier;
		Boolean perkEnabled;
		Number perkUnlockLevel;

		static RawStatTuning defaults(StatDefinition stat) {
			KossmanStatTuning defaults = KossmanStatTuning.defaultsFor(stat);
			RawStatTuning tuning = new RawStatTuning();
			tuning.enabled = defaults.enabled();
			tuning.effectMultiplier = defaults.effectMultiplier();
			tuning.perkEnabled = defaults.perkEnabled();
			tuning.perkUnlockLevel = defaults.perkUnlockLevel();
			return tuning;
		}
	}
}
