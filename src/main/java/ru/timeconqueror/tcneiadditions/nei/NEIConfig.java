package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.AspectRecipeHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import ru.timeconqueror.tcneiadditions.HandlerRemover;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;
import ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapedHandler;
import thaumcraft.common.config.ConfigItems;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        HandlerRemover.delayRecipeHandlerRemoving(AspectRecipeHandler.class);
        HandlerRemover.delayRecipeHandlerRemoving(ArcaneShapedRecipeHandler.class);

        HandlerRemover.delayUsageHandlerRemoving(AspectRecipeHandler.class);

        API.registerRecipeHandler(new AspectFromItemStackHandler());
        API.registerRecipeHandler(new AspectCombinationHandler());
        API.registerRecipeHandler(new ArcaneCraftingShapedHandler());

        API.registerUsageHandler(new AspectCombinationHandler());
    }

    @SubscribeEvent
    public void regHandlers(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(AspectCombinationHandler.class, TCNEIAdditions.NAME, TCNEIAdditions.MODID, builder -> {
            builder.setHeight(20);
            builder.setDisplayStack(new ItemStack(ConfigItems.itemResearchNotes));
        });
    }

    @Override
    public String getName() {
        return TCNEIAdditions.NAME;
    }

    @Override
    public String getVersion() {
        return TCNEIAdditions.VERSION;
    }
}