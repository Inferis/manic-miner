package org.inferis.manicminer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.mojang.datafixers.types.Type.FieldNotFoundException;

import net.fabricmc.loader.api.FabricLoader;

public class ManicMinerConfig {
    public boolean mustSneak = true;
    public boolean allowCommonBlocks = true;
    public boolean removeCommonBlocksAroundOre = true;
    public int maxVeinSize = 128; // 2 stacks
    public int maxCommonSize = 128; // 2 stacks
    public int maxWoodSize = 192;

    public void save() {
        var filename = FabricLoader.getInstance().getConfigDir().resolve("manicminer.cfg");
        var file = new File(filename.toString());
        try {
            var writer = new FileWriter(file);
            writer.write((mustSneak ? "true" : "false") + " # mustSneak\n");
            writer.write((allowCommonBlocks ? "true" : "false") + " # allowCommonBlocks\n");
            writer.write((removeCommonBlocksAroundOre ? "true" : "false") + " # removeCommonBlocksAroundOre\n");
            writer.write(maxVeinSize + " # maxVeinSize\n");
            writer.write(maxCommonSize + " # maxCommonSize\n");
            writer.write(maxWoodSize + " # maxWoodSize\n");
            writer.close();
        } catch (IOException e) {
            ManicMiner.LOGGER.error("Could not save config", e);
        }
    }

    public void load() {
        var filename = FabricLoader.getInstance().getConfigDir().resolve("manicminer.cfg");
        var file = new File(filename.toString());
        if (file.exists()) {
            try {
                var scanner = new Scanner(file);
                mustSneak = scanner.nextBoolean(); scanner.nextLine();
                allowCommonBlocks = scanner.nextBoolean(); scanner.nextLine();
                removeCommonBlocksAroundOre = scanner.nextBoolean(); scanner.nextLine();
                maxVeinSize = scanner.nextInt(); scanner.nextLine();
                maxCommonSize = scanner.nextInt(); scanner.nextLine();
                maxWoodSize = scanner.nextInt(); scanner.nextLine();
                scanner.close();
            }
            catch (FileNotFoundException e) {
                ManicMiner.LOGGER.error("Could not open config", e);
            }
        }
    }
}
