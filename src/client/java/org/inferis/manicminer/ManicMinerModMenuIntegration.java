package org.inferis.manicminer;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ManicMinerModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::configScreen;
    }

    public Screen configScreen(Screen parentScreen) {
        var builder = ConfigBuilder.create()
            .setParentScreen(parentScreen)
            .setTitle(Component.translatable("manicminer.config.title"));

        var entryBuilder = builder.entryBuilder();

        var category = builder.getOrCreateCategory(Component.translatable("manicminer.config.common"));
        category
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.must_sneak"), ManicMiner.CONFIG.mustSneak)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("manicminer.config.must_sneak.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.mustSneak = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.requires_hotkey"), ManicMiner.CONFIG.requiresHotKey)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("manicminer.config.requires_hotkey.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.requiresHotKey = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.requires_enchantment"), ManicMiner.CONFIG.requireEnchantment)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("manicminer.config.requires_enchantment.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.requireEnchantment = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.allow_common"), ManicMiner.CONFIG.allowCommonBlocks)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("manicminer.config.allow_common.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.allowCommonBlocks = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.remove_common_around_ore"), ManicMiner.CONFIG.removeCommonBlocksAroundOre)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("manicminer.config.remove_common_around_ore.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.removeCommonBlocksAroundOre = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Component.translatable("manicminer.config.summon_items"), ManicMiner.CONFIG.summonItems)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("manicminer.config.summon_items.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.summonItems = value; ManicMiner.CONFIG.save(); })
                .build());

        category
            .addEntry(entryBuilder.startTextDescription(Component.translatable("manicminer.config.limits")).build())
            .addEntry(entryBuilder.startIntField(Component.translatable("manicminer.config.orelimit"), ManicMiner.CONFIG.maxVeinSize)
                .setMin(0)
                .setMax(256)
                .setDefaultValue(128)
                .setTooltip(Component.translatable("manicminer.config.orelimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxVeinSize = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startIntField(Component.translatable("manicminer.config.commonlimit"), ManicMiner.CONFIG.maxCommonSize)
                .setMin(0)
                .setMax(512)
                .setDefaultValue(128)
                .setTooltip(Component.translatable("manicminer.config.commonlimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxCommonSize = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startIntField(Component.translatable("manicminer.config.woodlimit"), ManicMiner.CONFIG.maxWoodSize)
                .setMin(0)
                .setMax(256)
                .setDefaultValue(192)
                .setTooltip(Component.translatable("manicminer.config.woodlimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxWoodSize = value; ManicMiner.CONFIG.save(); })
                .build());

        return builder.build();
    }
}
