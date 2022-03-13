package ru.timeconqueror.tcneiadditions.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;
import ru.timeconqueror.tcneiadditions.util.TCNAConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TCNAGuiConfig extends GuiConfig {
    public TCNAGuiConfig(GuiScreen parentScreen) {
        super(
            parentScreen,
            getConfigElements(),
            TCNEIAdditions.MODID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(TCNAConfig.config.toString())
        );
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        list.add(new ConfigElement<ConfigCategory>(TCNAConfig.config.getCategory(TCNAConfig.GENERAL.toLowerCase(Locale.US))));

        return list;
    }
}
