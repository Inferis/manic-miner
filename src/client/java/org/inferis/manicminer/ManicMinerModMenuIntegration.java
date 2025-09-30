package org.inferis.manicminer;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class ManicMinerModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> configScreen(parent);
    }

    public Screen configScreen(Screen parentScreen) {
        var builder = ConfigBuilder.create()
            .setParentScreen(parentScreen)
            .setTitle(Text.translatable("manic-miner.config.title"));

        var entryBuilder = builder.entryBuilder();

        var category = builder.getOrCreateCategory(Text.translatable("manic-miner.config.common"));
        category
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.must_sneak"), ManicMiner.CONFIG.mustSneak)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("manic-miner.config.must_sneak.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.mustSneak = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.requires_hotkey"), ManicMiner.CONFIG.requiresHotKey)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("manic-miner.config.requires_hotkey.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.requiresHotKey = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.requires_enchantment"), ManicMiner.CONFIG.requireEnchantment)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("manic-miner.config.requires_enchantment.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.requireEnchantment = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.allow_common"), ManicMiner.CONFIG.allowCommonBlocks)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("manic-miner.config.allow_common.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.allowCommonBlocks = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.remove_common_around_ore"), ManicMiner.CONFIG.removeCommonBlocksAroundOre)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("manic-miner.config.remove_common_around_ore.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.removeCommonBlocksAroundOre = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startBooleanToggle(Text.translatable("manic-miner.config.summon_items"), ManicMiner.CONFIG.summonItems)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("manic-miner.config.summon_items.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.summonItems = value; ManicMiner.CONFIG.save(); })
                .build());

        category
            .addEntry(entryBuilder.startTextDescription(Text.translatable("manic-miner.config.limits")).build())
            .addEntry(entryBuilder.startIntField(Text.translatable("manic-miner.config.orelimit"), ManicMiner.CONFIG.maxVeinSize)
                .setMin(0)
                .setMax(256)
                .setDefaultValue(128)
                .setTooltip(Text.translatable("manic-miner.config.orelimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxVeinSize = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startIntField(Text.translatable("manic-miner.config.commonlimit"), ManicMiner.CONFIG.maxCommonSize)
                .setMin(0)
                .setMax(512)
                .setDefaultValue(128)
                .setTooltip(Text.translatable("manic-miner.config.commonlimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxCommonSize = value; ManicMiner.CONFIG.save(); })
                .build())
            .addEntry(entryBuilder.startIntField(Text.translatable("manic-miner.config.woodlimit"), ManicMiner.CONFIG.maxWoodSize)
                .setMin(0)
                .setMax(256)
                .setDefaultValue(192)
                .setTooltip(Text.translatable("manic-miner.config.woodlimit.tooltip"))
                .setSaveConsumer(value -> { ManicMiner.CONFIG.maxWoodSize = value; ManicMiner.CONFIG.save(); })
                .build());

        return builder.build();
    }
}
