package net.infstudio.gokistats.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.client.state.ClientStatSnapshotCache;
import net.infstudio.gokistats.core.config.KossmanBalance;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.infstudio.gokistats.core.progression.StatProgression;
import net.infstudio.gokistats.fabric.network.StatDowngradeRequestPayload;
import net.infstudio.gokistats.fabric.network.StatUpgradeRequestPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class KossmanStatsScreen extends Screen {
	private static final int PANEL_MAX_WIDTH = 760;
	private static final int BUTTON_SIZE = 16;
	private static final int ROW_HEIGHT = 32;
	private static final int ROW_PADDING = 8;
	private static final int ICON_TEXTURE_SIZE = 32;
	private static final int ICON_RENDER_SIZE = 24;
	private static final int PANEL_TOP = 34;
	private static final int PANEL_BOTTOM_PADDING = 12;
	private static final int PANEL_HORIZONTAL_PADDING = 18;
	private static final int PANEL_VERTICAL_PADDING = 12;
	private static final int FOOTER_BOTTOM_MARGIN = 20;
	private static final int FOOTER_GAP = 10;
	private static final int BUTTON_GAP = 5;
	private static final int COLUMN_GAP = 18;
	private static final int ICON_NAME_GAP = 7;
	private static final int LEVEL_WIDTH = 42;
	private static final int XP_WIDTH = 62;
	private static final int SCROLLBAR_WIDTH = 4;
	private static final int SCROLLBAR_GUTTER = 10;
	private static final int TITLE_COLOR = 0xFFFFFFFF;
	private static final int TEXT_COLOR = 0xFFD8D8D8;
	private static final int MUTED_COLOR = 0xFFA0A0A0;
	private static final int PANEL_COLOR = 0xB0202020;
	private static final int MISSING_ICON_BG = 0x80303030;
	private static final int MISSING_ICON_BORDER = 0xFFA0A0A0;
	private static final int SCROLLBAR_TRACK_COLOR = 0x50303030;
	private static final int SCROLLBAR_THUMB_COLOR = 0xCCB8B8B8;
	private final Map<StatDefinition, Button> downgradeButtons = new LinkedHashMap<>();
	private final Map<StatDefinition, Button> upgradeButtons = new LinkedHashMap<>();
	private int scrollOffset;
	private boolean draggingScrollbar;
	private int scrollbarDragOffset;

	public KossmanStatsScreen() {
		super(Component.translatable("screen.gokistats.stats"));
	}

	@Override
	protected void init() {
		downgradeButtons.clear();
		upgradeButtons.clear();

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			Button downgradeButton = Button.builder(Component.literal("-"), pressed -> requestDowngrade(stat))
					.bounds(0, 0, BUTTON_SIZE, BUTTON_SIZE)
					.build();
			Button button = Button.builder(Component.literal("+"), pressed -> requestUpgrade(stat))
					.bounds(0, 0, BUTTON_SIZE, BUTTON_SIZE)
					.build();
			downgradeButtons.put(stat, addRenderableWidget(downgradeButton));
			upgradeButtons.put(stat, addRenderableWidget(button));
		}

		scrollOffset = clampScroll(scrollOffset);
		updateButtonStatesAndLayout();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(0, 0, width, height, 0xC0101010);
		int left = panelLeft();
		int right = panelRight();
		int panelBottom = panelBottomY();
		int listTop = listTopY();
		int listBottom = listBottomY();

		graphics.centeredText(font, title, width / 2, 14, TITLE_COLOR);
		updateButtonStatesAndLayout();

		if (!ClientStatSnapshotCache.hasSnapshot()) {
			graphics.centeredText(
					font,
					Component.translatable("screen.gokistats.stats.waiting"),
					width / 2,
					listTop + 8,
					MUTED_COLOR
			);
			super.extractRenderState(graphics, mouseX, mouseY, partialTick);
			return;
		}

		graphics.fill(left, PANEL_TOP, right, panelBottom, PANEL_COLOR);

		int contentY = listTop + ROW_PADDING - scrollOffset;
		graphics.enableScissor(listLeft(), listTop, listContentRight(), listBottom);
		for (int index = 0; index < KossmanStatDefinitions.ALL.size(); index++) {
			StatDefinition stat = KossmanStatDefinitions.ALL.get(index);
			int rowY = contentY + (index / 2 * ROW_HEIGHT);
			if (!isRowFullyVisible(rowY, listTop, listBottom)) {
				continue;
			}

			int columnLeft = columnLeft(index % 2);
			renderStatEntry(graphics, stat, columnLeft, rowY);
			if (isMouseOverButton(downgradeButtons.get(stat), mouseX, mouseY)) {
				graphics.setComponentTooltipForNextFrame(
						font,
						StatTooltipContent.forDowngrade(stat, ClientStatSnapshotCache.level(stat)),
						mouseX,
						mouseY
				);
			} else if (isMouseOverEntry(mouseX, mouseY, columnLeft, rowY)) {
				graphics.setComponentTooltipForNextFrame(
						font,
						StatTooltipContent.forStat(stat, ClientStatSnapshotCache.level(stat)),
						mouseX,
						mouseY
				);
			}
		}
		graphics.disableScissor();
		renderScrollbar(graphics);

		graphics.centeredText(
				font,
				Component.translatable("screen.gokistats.stats.close_hint"),
				width / 2,
				footerY(),
				MUTED_COLOR
		);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	private void renderStatEntry(GuiGraphicsExtractor graphics, StatDefinition stat, int left, int y) {
		int level = ClientStatSnapshotCache.level(stat);
		Component name = Component.literal(stat.displayName());
		Component levelText = Component.literal("Lv. " + level);
		Component nextText = Component.literal(nextCostText(stat, level));
		int textY = centeredTextY(y);
		int iconY = centeredIconY(y);

		renderIcon(graphics, stat, level, left, iconY);
		graphics.text(font, name, nameX(left), textY, TEXT_COLOR);
		graphics.text(font, levelText, levelX(left), textY, TEXT_COLOR);
		graphics.text(font, nextText, nextX(left), textY, MUTED_COLOR);
	}

	private void updateButtonStatesAndLayout() {
		int left = panelLeft();
		int listTop = listTopY();
		int listBottom = listBottomY();
		int contentY = listTop + ROW_PADDING - scrollOffset;

		for (int index = 0; index < KossmanStatDefinitions.ALL.size(); index++) {
			StatDefinition stat = KossmanStatDefinitions.ALL.get(index);
			int level = ClientStatSnapshotCache.level(stat);
			Button downgradeButton = downgradeButtons.get(stat);
			Button upgradeButton = upgradeButtons.get(stat);
			boolean enabled = KossmanBalance.current().isEnabled(stat);
			int columnLeft = columnLeft(index % 2);
			int buttonX = upgradeButtonX(columnLeft);
			int downgradeX = buttonX - BUTTON_SIZE - BUTTON_GAP;
			int rowY = contentY + (index / 2 * ROW_HEIGHT);
			boolean visible = ClientStatSnapshotCache.hasSnapshot() && isRowFullyVisible(rowY, listTop, listBottom);
			int buttonY = rowY + ((ROW_HEIGHT - BUTTON_SIZE) / 2);

			downgradeButton.setX(downgradeX);
			downgradeButton.setY(buttonY);
			upgradeButton.setX(buttonX);
			upgradeButton.setY(buttonY);
			downgradeButton.visible = visible;
			downgradeButton.active = enabled && level > 0;
			upgradeButton.visible = visible;
			upgradeButton.active = enabled && level < KossmanBalance.current().maxStatLevel();
		}
	}

	private void requestUpgrade(StatDefinition stat) {
		if (ClientPlayNetworking.canSend(StatUpgradeRequestPayload.ID)) {
			ClientPlayNetworking.send(new StatUpgradeRequestPayload(stat.id().value()));
		}
	}

	private void requestDowngrade(StatDefinition stat) {
		if (ClientPlayNetworking.canSend(StatDowngradeRequestPayload.ID)) {
			ClientPlayNetworking.send(new StatDowngradeRequestPayload(stat.id().value()));
		}
	}

	private boolean isMouseOverEntry(int mouseX, int mouseY, int left, int y) {
		return mouseX >= left
				&& mouseX <= left + columnWidth()
				&& mouseY >= y
				&& mouseY <= y + ROW_HEIGHT;
	}

	private boolean isMouseOverButton(Button button, int mouseX, int mouseY) {
		return button != null
				&& button.visible
				&& mouseX >= button.getX()
				&& mouseX <= button.getX() + button.getWidth()
				&& mouseY >= button.getY()
				&& mouseY <= button.getY() + button.getHeight();
	}

	private String nextCostText(StatDefinition stat, int level) {
		if (!KossmanBalance.current().isEnabled(stat)) {
			return "disabled";
		}

		if (level >= KossmanBalance.current().maxStatLevel()) {
			return "MAX";
		}

		return StatProgression.upgradeCostForLevel(level) + " XP";
	}

	private void renderIcon(GuiGraphicsExtractor graphics, StatDefinition stat, int level, int x, int y) {
		Identifier icon = resolveIcon(stat, level);
		if (icon == null) {
			graphics.fill(x, y, x + ICON_RENDER_SIZE, y + ICON_RENDER_SIZE, MISSING_ICON_BG);
			graphics.fill(x, y, x + ICON_RENDER_SIZE, y + 1, MISSING_ICON_BORDER);
			graphics.fill(x, y + ICON_RENDER_SIZE - 1, x + ICON_RENDER_SIZE, y + ICON_RENDER_SIZE, MISSING_ICON_BORDER);
			graphics.fill(x, y, x + 1, y + ICON_RENDER_SIZE, MISSING_ICON_BORDER);
			graphics.fill(x + ICON_RENDER_SIZE - 1, y, x + ICON_RENDER_SIZE, y + ICON_RENDER_SIZE, MISSING_ICON_BORDER);
			graphics.centeredText(font, Component.literal("?"), x + (ICON_RENDER_SIZE / 2), y + 8, MISSING_ICON_BORDER);
			return;
		}

		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				icon,
				x,
				y,
				0.0F,
				0.0F,
				ICON_RENDER_SIZE,
				ICON_RENDER_SIZE,
				ICON_TEXTURE_SIZE,
				ICON_TEXTURE_SIZE,
				ICON_TEXTURE_SIZE,
				ICON_TEXTURE_SIZE
		);
	}

	private Identifier resolveIcon(StatDefinition stat, int level) {
		String suffix = iconSuffix(level);
		StatDefinition.StatIconMetadata icon = stat.icon();
		Identifier identifier = Identifier.fromNamespaceAndPath(
				KossmanStats.MOD_ID,
				"gui/stats/32/" + icon.folderName() + "/" + icon.fileStem() + suffix + ".png"
		);
		return hasResource(identifier) ? identifier : null;
	}

	private String iconSuffix(int level) {
		if (level <= 0) {
			return "_grey";
		}

		if (level >= KossmanBalance.current().maxStatLevel()) {
			return "_max";
		}

		return "_def";
	}

	private boolean hasResource(Identifier id) {
		Minecraft client = Minecraft.getInstance();
		return client != null && client.getResourceManager().getResource(id).isPresent();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (maxScroll() <= 0 || mouseY < listTopY() || mouseY > listBottomY()) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		scrollOffset = clampScroll(scrollOffset - ((int) Math.signum(verticalAmount) * ROW_HEIGHT));
		updateButtonStatesAndLayout();
		return true;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (maxScroll() > 0 && event.button() == 0 && isMouseOverScrollbar(event.x(), event.y())) {
			draggingScrollbar = true;
			scrollbarDragOffset = (int) event.y() - scrollbarThumbTop();
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (draggingScrollbar) {
			draggingScrollbar = false;
			return true;
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (draggingScrollbar) {
			setScrollFromThumb((int) event.y() - scrollbarDragOffset);
			return true;
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	private int panelWidth() {
		return Math.min(PANEL_MAX_WIDTH, Math.max(240, width - 40));
	}

	private int panelLeft() {
		return (width - panelWidth()) / 2;
	}

	private int panelRight() {
		return panelLeft() + panelWidth();
	}

	private int panelBottomY() {
		return Math.max(PANEL_TOP + PANEL_VERTICAL_PADDING + ROW_HEIGHT + PANEL_BOTTOM_PADDING, footerY() - FOOTER_GAP);
	}

	private int footerY() {
		return height - FOOTER_BOTTOM_MARGIN;
	}

	private int listTopY() {
		return PANEL_TOP + PANEL_VERTICAL_PADDING;
	}

	private int listBottomY() {
		return Math.max(listTopY(), panelBottomY() - PANEL_BOTTOM_PADDING);
	}

	private boolean isRowFullyVisible(int rowY, int listTop, int listBottom) {
		return rowY >= listTop && rowY + ROW_HEIGHT <= listBottom;
	}

	private int centeredTextY(int rowY) {
		return rowY + ((ROW_HEIGHT - font.lineHeight) / 2);
	}

	private int centeredIconY(int rowY) {
		return rowY + ((ROW_HEIGHT - ICON_RENDER_SIZE) / 2);
	}

	private int maxScroll() {
		int visibleRows = Math.max(1, ((listBottomY() - listTopY()) - (ROW_PADDING * 2)) / ROW_HEIGHT);
		return Math.max(0, (statVisualRows() - visibleRows) * ROW_HEIGHT);
	}

	private int clampScroll(int offset) {
		int clamped = Math.max(0, Math.min(offset, maxScroll()));
		return (clamped / ROW_HEIGHT) * ROW_HEIGHT;
	}

	private int statVisualRows() {
		return (KossmanStatDefinitions.ALL.size() + 1) / 2;
	}

	private int listLeft() {
		return panelLeft() + PANEL_HORIZONTAL_PADDING;
	}

	private int listContentRight() {
		int right = panelRight() - PANEL_HORIZONTAL_PADDING;
		return maxScroll() > 0 ? right - SCROLLBAR_GUTTER : right;
	}

	private int columnWidth() {
		return Math.max(0, (listContentRight() - listLeft() - COLUMN_GAP) / 2);
	}

	private int columnLeft(int column) {
		return listLeft() + (column * (columnWidth() + COLUMN_GAP));
	}

	private int nameX(int left) {
		return left + ICON_RENDER_SIZE + ICON_NAME_GAP;
	}

	private int levelX(int left) {
		return nextX(left) - LEVEL_WIDTH;
	}

	private int nextX(int left) {
		return downgradeButtonX(left) - XP_WIDTH;
	}

	private int downgradeButtonX(int left) {
		return upgradeButtonX(left) - BUTTON_SIZE - BUTTON_GAP;
	}

	private int upgradeButtonX(int left) {
		return left + columnWidth() - BUTTON_SIZE;
	}

	private void renderScrollbar(GuiGraphicsExtractor graphics) {
		if (maxScroll() <= 0) {
			return;
		}

		int x = scrollbarX();
		graphics.fill(x, listTopY(), x + SCROLLBAR_WIDTH, listBottomY(), SCROLLBAR_TRACK_COLOR);
		graphics.fill(x, scrollbarThumbTop(), x + SCROLLBAR_WIDTH, scrollbarThumbBottom(), SCROLLBAR_THUMB_COLOR);
	}

	private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
		return mouseX >= scrollbarX()
				&& mouseX <= scrollbarX() + SCROLLBAR_WIDTH
				&& mouseY >= listTopY()
				&& mouseY <= listBottomY();
	}

	private int scrollbarX() {
		return panelRight() - PANEL_HORIZONTAL_PADDING - SCROLLBAR_WIDTH;
	}

	private int scrollbarThumbTop() {
		int trackHeight = listBottomY() - listTopY();
		int range = Math.max(1, maxScroll());
		int thumbTravel = Math.max(0, trackHeight - scrollbarThumbHeight());
		return listTopY() + ((scrollOffset * thumbTravel) / range);
	}

	private int scrollbarThumbBottom() {
		return scrollbarThumbTop() + scrollbarThumbHeight();
	}

	private int scrollbarThumbHeight() {
		int viewportHeight = Math.max(1, listBottomY() - listTopY());
		int contentHeight = Math.max(viewportHeight, (statVisualRows() * ROW_HEIGHT) + (ROW_PADDING * 2));
		return Math.max(20, (viewportHeight * viewportHeight) / contentHeight);
	}

	private void setScrollFromThumb(int thumbTop) {
		int trackHeight = listBottomY() - listTopY();
		int thumbTravel = Math.max(1, trackHeight - scrollbarThumbHeight());
		int clampedThumbTop = Math.max(listTopY(), Math.min(thumbTop, listTopY() + thumbTravel));
		scrollOffset = clampScroll(((clampedThumbTop - listTopY()) * maxScroll()) / thumbTravel);
		updateButtonStatesAndLayout();
	}
}
