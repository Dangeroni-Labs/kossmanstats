package net.infstudio.gokistats.core.perk;

import net.infstudio.gokistats.core.definition.StatDefinition;

public record StatPerkDefinition(
		StatDefinition stat,
		String masteryName,
		String description,
		int defaultUnlockLevel
) {
	public StatPerkDefinition {
		if (stat == null) {
			throw new IllegalArgumentException("stat must not be null");
		}

		if (masteryName == null || masteryName.isBlank()) {
			throw new IllegalArgumentException("masteryName must not be blank");
		}

		if (description == null || description.isBlank()) {
			throw new IllegalArgumentException("description must not be blank");
		}

		if (defaultUnlockLevel <= 0) {
			throw new IllegalArgumentException("defaultUnlockLevel must be positive");
		}
	}
}
