package net.infstudio.gokistats.core.definition;

public record StatDefinition(StatId id, String commandName, String displayName, int maxLevel) {
	public StatDefinition {
		if (commandName == null || commandName.isBlank()) {
			throw new IllegalArgumentException("Command name must not be blank");
		}

		if (displayName == null || displayName.isBlank()) {
			throw new IllegalArgumentException("Display name must not be blank");
		}

		if (maxLevel < 0) {
			throw new IllegalArgumentException("Max level must not be negative");
		}
	}
}
