package net.infstudio.gokistats.core.definition;

public record StatId(String value) {
	public StatId {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Stat id must not be blank");
		}
	}

	public String path() {
		int separator = value.indexOf(':');
		return separator >= 0 ? value.substring(separator + 1) : value;
	}
}
