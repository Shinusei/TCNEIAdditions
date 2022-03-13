package ru.timeconqueror.tcneiadditions.util;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class TCNAConfig {
    public static Configuration config;

    public static final String GENERAL = "General";

    public static boolean showLockedRecipes;

    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }

    public static void syncConfig() {
        config.setCategoryComment(GENERAL, "General config");

        showLockedRecipes = config.get(GENERAL, "showLockedRecipes", false, "Show recipes even if the research is not completed").getBoolean();

        if (config.hasChanged()) {
            config.save();
        }
    }
}
