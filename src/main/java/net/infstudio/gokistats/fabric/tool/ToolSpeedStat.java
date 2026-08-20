package net.infstudio.gokistats.fabric.tool;

import java.util.function.IntToDoubleFunction;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

record ToolSpeedStat(StatDefinition stat, TagKey<Item> toolTag, IntToDoubleFunction bonusForLevel) {
}
