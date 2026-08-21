package net.infstudio.gokistats.fabric.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.infstudio.gokistats.core.config.KossmanBalanceTuning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KossmanServerConfigLoaderTest {
	@TempDir
	Path tempDir;

	@Test
	void missingConfigCreatesDefaultFile() {
		Path configPath = tempDir.resolve("kossmanstats.json");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertTrue(Files.exists(configPath));
		assertEquals(KossmanBalanceTuning.DEFAULT, tuning);
		assertTrue(warnings.isEmpty());
	}

	@Test
	void validValuesAreLoaded() throws IOException {
		Path configPath = write("""
				{
				  "maxStatLevel": 60,
				  "xp": {
				    "costMultiplier": 1.25,
				    "baseOffset": 9.0
				  },
				  "downgradeRefundRate": 0.5,
				  "deathPenalty": {
				    "lossRate": 0.2,
				    "minimumLoss": 2,
				    "maximumLoss": 4,
				    "minimumRetainedStatLevel": 1
				  },
				  "stats": {
				    "mining": {
				      "enabled": false,
				      "effectMultiplier": 1.5
				    }
				  }
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertEquals(60, tuning.maxStatLevel());
		assertEquals(1.25D, tuning.xp().costMultiplier());
		assertEquals(9.0D, tuning.xp().baseOffset());
		assertEquals(0.5D, tuning.downgradeRefundRate());
		assertEquals(0.2D, tuning.deathPenalty().lossRate());
		assertFalse(tuning.stats().get("mining").enabled());
		assertEquals(1.5D, tuning.stats().get("mining").effectMultiplier());
		assertTrue(warnings.isEmpty());
	}

	@Test
	void invalidRefundRateFallsBackToDefault() throws IOException {
		Path configPath = write("""
				{
				  "downgradeRefundRate": 1.5
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertEquals(KossmanBalanceTuning.DEFAULT.downgradeRefundRate(), tuning.downgradeRefundRate());
		assertFalse(warnings.isEmpty());
	}

	@Test
	void invalidNegativeMultiplierFallsBackToDefault() throws IOException {
		Path configPath = write("""
				{
				  "stats": {
				    "protection": {
				      "effectMultiplier": -2.0
				    }
				  }
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertEquals(1.0D, tuning.stats().get("protection").effectMultiplier());
		assertFalse(warnings.isEmpty());
	}

	@Test
	void invalidMaxLevelFallsBackToDefault() throws IOException {
		Path configPath = write("""
				{
				  "maxStatLevel": 0
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertEquals(KossmanBalanceTuning.DEFAULT.maxStatLevel(), tuning.maxStatLevel());
		assertFalse(warnings.isEmpty());
	}

	@Test
	void invalidDeathPenaltyBoundsFallBackSafely() throws IOException {
		Path configPath = write("""
				{
				  "deathPenalty": {
				    "lossRate": 0.5,
				    "minimumLoss": 5,
				    "maximumLoss": 2,
				    "minimumRetainedStatLevel": -1
				  }
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertEquals(KossmanBalanceTuning.DEFAULT.deathPenalty().minimumLoss(), tuning.deathPenalty().minimumLoss());
		assertEquals(KossmanBalanceTuning.DEFAULT.deathPenalty().maximumLoss(), tuning.deathPenalty().maximumLoss());
		assertEquals(KossmanBalanceTuning.DEFAULT.deathPenalty().minimumRetainedStatLevel(), tuning.deathPenalty().minimumRetainedStatLevel());
		assertFalse(warnings.isEmpty());
	}

	@Test
	void missingStatEntriesFallBackToDefaults() throws IOException {
		Path configPath = write("""
				{
				  "stats": {
				    "mining": {
				      "enabled": false
				    }
				  }
				}
				""");
		List<String> warnings = new ArrayList<>();

		KossmanBalanceTuning tuning = KossmanServerConfigLoader.load(configPath, warnings::add);

		assertFalse(tuning.stats().get("mining").enabled());
		assertEquals(1.0D, tuning.stats().get("health").effectMultiplier());
		assertTrue(tuning.stats().get("health").enabled());
	}

	private Path write(String content) throws IOException {
		Path configPath = tempDir.resolve("kossmanstats.json");
		Files.writeString(configPath, content);
		return configPath;
	}
}
