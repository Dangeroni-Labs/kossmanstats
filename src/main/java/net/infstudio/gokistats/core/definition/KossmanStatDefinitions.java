package net.infstudio.gokistats.core.definition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class KossmanStatDefinitions {
	public static final StatDefinition MINING = new StatDefinition(
			new StatId("gokistats:mining"),
			"mining",
			"Mining",
			icon("mining", "mining")
	);

	public static final StatDefinition HEALTH = new StatDefinition(
			new StatId("gokistats:health"),
			"health",
			"Health",
			icon("health", "health")
	);

	public static final StatDefinition DIGGING = new StatDefinition(
			new StatId("gokistats:digging"),
			"digging",
			"Digging",
			icon("digging", "digging")
	);

	public static final StatDefinition CHOPPING = new StatDefinition(
			new StatId("gokistats:chopping"),
			"chopping",
			"Chopping",
			icon("chopping", "chopping")
	);

	public static final StatDefinition TRIMMING = new StatDefinition(
			new StatId("gokistats:trimming"),
			"trimming",
			"Trimming",
			icon("trimming", "trimming")
	);

	public static final StatDefinition SWORDSMANSHIP = new StatDefinition(
			new StatId("gokistats:swordsmanship"),
			"swordsmanship",
			"Swordsmanship",
			icon("swordmanship", "swordmanship")
	);

	public static final StatDefinition PUGILISM = new StatDefinition(
			new StatId("gokistats:pugilism"),
			"pugilism",
			"Pugilism",
			icon("pugilism", "pugilism")
	);

	public static final StatDefinition BOWMANSHIP = new StatDefinition(
			new StatId("gokistats:bowmanship"),
			"bowmanship",
			"Bowmanship",
			icon("bowmaship", "bowmanship")
	);

	public static final StatDefinition PROTECTION = new StatDefinition(
			new StatId("gokistats:protection"),
			"protection",
			"Protection",
			icon("protection", "protection")
	);

	public static final StatDefinition TEMPERING = new StatDefinition(
			new StatId("gokistats:tempering"),
			"tempering",
			"Tempering",
			icon("tempering", "tempering")
	);

	public static final StatDefinition TOUGH_SKIN = new StatDefinition(
			new StatId("gokistats:tough_skin"),
			"tough_skin",
			"Tough Skin",
			icon("toughskin", "toughskin")
	);

	public static final StatDefinition FEATHER_FALL = new StatDefinition(
			new StatId("gokistats:feather_fall"),
			"feather_fall",
			"Feather Fall",
			icon("featherfall", "featherfall")
	);

	public static final StatDefinition LEAPER_H = new StatDefinition(
			new StatId("gokistats:leaper_h"),
			"leaper_h",
			"Leaper H",
			icon("leaperh", "leaperh")
	);

	public static final StatDefinition LEAPER_V = new StatDefinition(
			new StatId("gokistats:leaper_v"),
			"leaper_v",
			"Leaper V",
			icon("leaperv", "leaperv")
	);

	public static final StatDefinition CLIMBING = new StatDefinition(
			new StatId("gokistats:climbing"),
			"climbing",
			"Climbing",
			icon("climbing", "climbing")
	);

	public static final StatDefinition STEADY_GUARD = new StatDefinition(
			new StatId("gokistats:steady_guard"),
			"steady_guard",
			"Steady Guard",
			icon("steadyguard", "steadyguard")
	);

	public static final StatDefinition REAPER = new StatDefinition(
			new StatId("gokistats:reaper"),
			"reaper",
			"Reaper",
			icon("reaper", "reaper")
	);

	public static final StatDefinition ROLL = new StatDefinition(
			new StatId("gokistats:roll"),
			"roll",
			"Roll",
			icon("roll", "roll")
	);

	public static final StatDefinition TREASURE_FINDER = new StatDefinition(
			new StatId("gokistats:treasure_finder"),
			"treasure_finder",
			"Treasure Finder",
			icon("treasurefinder", "treasurefinder")
	);

	public static final StatDefinition MINING_MAGICIAN = new StatDefinition(
			new StatId("gokistats:mining_magician"),
			"mining_magician",
			"Mining Magician",
			icon("miningmagician", "miningmagician")
	);

	public static final List<StatDefinition> ALL = List.of(
			MINING,
			HEALTH,
			DIGGING,
			CHOPPING,
			TRIMMING,
			SWORDSMANSHIP,
			PUGILISM,
			BOWMANSHIP,
			PROTECTION,
			TEMPERING,
			TOUGH_SKIN,
			FEATHER_FALL,
			LEAPER_H,
			LEAPER_V,
			CLIMBING,
			STEADY_GUARD,
			REAPER,
			ROLL,
			TREASURE_FINDER,
			MINING_MAGICIAN
	);
	private static final Map<String, StatDefinition> BY_COMMAND_NAME = ALL.stream()
			.collect(Collectors.toUnmodifiableMap(StatDefinition::commandName, stat -> stat));
	private static final Map<StatId, StatDefinition> BY_ID = ALL.stream()
			.collect(Collectors.toUnmodifiableMap(StatDefinition::id, stat -> stat));

	private KossmanStatDefinitions() {
	}

	public static Optional<StatDefinition> byCommandName(String commandName) {
		return Optional.ofNullable(BY_COMMAND_NAME.get(commandName));
	}

	public static Optional<StatDefinition> byId(StatId id) {
		return Optional.ofNullable(BY_ID.get(id));
	}

	private static StatDefinition.StatIconMetadata icon(String folderName, String fileStem) {
		return new StatDefinition.StatIconMetadata(folderName, fileStem);
	}
}
