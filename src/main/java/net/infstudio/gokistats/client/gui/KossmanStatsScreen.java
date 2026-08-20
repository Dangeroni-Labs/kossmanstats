package net.infstudio.gokistats.client.gui;

import net.infstudio.gokistats.client.state.ClientStatSnapshotCache;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.progression.StatProgression;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class KossmanStatsScreen extends Screen {
	private static final int ROW_HEIGHT = 18;
	private static final int PANEL_WIDTH = 320;
	private static final int TITLE_COLOR = 0xFFFFFFFF;
	private static final int TEXT_COLOR = 0xFFD8D8D8;
	private static final int MUTED_COLOR = 0xFFA0A0A0;
	private static final int PANEL_COLOR = 0xB0202020;

	public KossmanStatsScreen() {
		super(Component.translatable("screen.gokistats.stats"));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(0, 0, width, height, 0xC0101010);
		int left = (width - PANEL_WIDTH) / 2;
		int top = 28;
		int y = top + 28;

		graphics.centeredText(font, title, width / 2, 14, TITLE_COLOR);

		if (!ClientStatSnapshotCache.hasSnapshot()) {
			graphics.centeredText(
					font,
					Component.translatable("screen.gokistats.stats.waiting"),
					width / 2,
					y,
					MUTED_COLOR
			);
			super.extractRenderState(graphics, mouseX, mouseY, partialTick);
			return;
		}

		graphics.fill(left - 10, top, left + PANEL_WIDTH + 10, y + KossmanStatDefinitions.ALL.size() * ROW_HEIGHT + 6, PANEL_COLOR);

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			renderStatRow(graphics, stat, left, y);
			y += ROW_HEIGHT;
		}

		graphics.centeredText(
				font,
				Component.translatable("screen.gokistats.stats.close_hint"),
				width / 2,
				height - 24,
				MUTED_COLOR
		);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	private void renderStatRow(GuiGraphicsExtractor graphics, StatDefinition stat, int left, int y) {
		int level = ClientStatSnapshotCache.level(stat);
		Component name = Component.literal(stat.displayName());
		Component levelText = Component.literal("Level " + level);
		Component nextText = Component.literal("Next: " + nextCostText(stat, level));

		graphics.text(font, name, left, y, TEXT_COLOR);
		graphics.text(font, levelText, left + 140, y, TEXT_COLOR);
		graphics.text(font, nextText, left + 220, y, MUTED_COLOR);
	}

	private String nextCostText(StatDefinition stat, int level) {
		if (level >= stat.maxLevel()) {
			return "max";
		}

		return StatProgression.upgradeCostForLevel(level) + " XP";
	}
}
