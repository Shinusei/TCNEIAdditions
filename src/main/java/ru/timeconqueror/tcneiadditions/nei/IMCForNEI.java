package ru.timeconqueror.tcneiadditions.nei;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCForNEI {

    public static void IMCSender() {
        setNBTAndSend(
                "ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapedHandler",
                "Thaumcraft:blockTable:15");
        setNBTAndSend(
                "ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapelessHandler",
                "Thaumcraft:blockTable:15");
        setNBTAndSend("ru.timeconqueror.tcneiadditions.nei.TCNACrucibleRecipeHandler", "Thaumcraft:blockMetalDevice");
        setNBTAndSend("ru.timeconqueror.tcneiadditions.nei.TCNAInfusionRecipeHandler", "Thaumcraft:blockStoneDevice:2");
        setNBTAndSend(
                "ru.timeconqueror.tcneiadditions.nei.AspectCombinationHandler",
                "Thaumcraft:ItemResearchNotes",
                20,
                16);
    }

    private static void setNBTAndSend(String name, String stack, int height, int maxRecipesPerPage) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("handler", name);
        NBT.setString("modName", "Thaumcraft");
        NBT.setString("modId", "Thaumcraft");
        NBT.setBoolean("modRequired", true);
        NBT.setString("itemName", stack);
        NBT.setInteger("handlerHeight", height);
        NBT.setInteger("maxRecipesPerPage", maxRecipesPerPage);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", NBT);
    }

    private static void setNBTAndSend(String name, String stack) {
        setNBTAndSend(name, stack, 140, 1);
    }
}
