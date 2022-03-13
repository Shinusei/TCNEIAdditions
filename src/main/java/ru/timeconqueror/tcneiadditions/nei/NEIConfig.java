package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.AspectRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import ru.timeconqueror.tcneiadditions.HandlerRemover;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;
import ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapedHandler;
import ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapelessHandler;
import thaumcraft.common.config.ConfigItems;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        HandlerRemover.delayRecipeHandlerRemoving(AspectRecipeHandler.class);
        HandlerRemover.delayRecipeHandlerRemoving(ArcaneShapedRecipeHandler.class);
        HandlerRemover.delayRecipeHandlerRemoving(ArcaneShapelessRecipeHandler.class);
        HandlerRemover.delayRecipeHandlerRemoving(CrucibleRecipeHandler.class);
        HandlerRemover.delayRecipeHandlerRemoving(InfusionRecipeHandler.class);

        HandlerRemover.delayUsageHandlerRemoving(AspectRecipeHandler.class);
        HandlerRemover.delayUsageHandlerRemoving(ArcaneShapedRecipeHandler.class);
        HandlerRemover.delayUsageHandlerRemoving(ArcaneShapelessRecipeHandler.class);
        HandlerRemover.delayUsageHandlerRemoving(CrucibleRecipeHandler.class);
        HandlerRemover.delayUsageHandlerRemoving(InfusionRecipeHandler.class);

        API.registerRecipeHandler(new AspectFromItemStackHandler());
        API.registerRecipeHandler(new AspectCombinationHandler());
        API.registerRecipeHandler(new ArcaneCraftingShapedHandler());
        API.registerRecipeHandler(new ArcaneCraftingShapelessHandler());
        API.registerRecipeHandler(new TCNACrucibleRecipeHandler());
        API.registerRecipeHandler(new TCNAInfusionRecipeHandler());

        API.registerUsageHandler(new AspectCombinationHandler());
        API.registerUsageHandler(new ArcaneCraftingShapedHandler());
        API.registerUsageHandler(new ArcaneCraftingShapelessHandler());
        API.registerUsageHandler(new TCNACrucibleRecipeHandler());
        API.registerUsageHandler(new TCNAInfusionRecipeHandler());
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
