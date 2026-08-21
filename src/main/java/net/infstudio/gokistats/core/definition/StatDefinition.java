package net.infstudio.gokistats.core.definition;

public record StatDefinition(StatId id, String commandName, String displayName, StatIconMetadata icon) {
	public StatDefinition {
		if (commandName == null || commandName.isBlank()) {
			throw new IllegalArgumentException("Command name must not be blank");
		}

		if (displayName == null || displayName.isBlank()) {
			throw new IllegalArgumentException("Display name must not be blank");
		}

		if (icon == null) {
			throw new IllegalArgumentException("Icon metadata must not be null");
		}
	}

	public record StatIconMetadata(String folderName, String fileStem) {
		public StatIconMetadata {
			if (folderName == null || folderName.isBlank()) {
				throw new IllegalArgumentException("Icon folder must not be blank");
			}

			if (fileStem == null || fileStem.isBlank()) {
				throw new IllegalArgumentException("Icon file stem must not be blank");
			}
		}
	}
}
