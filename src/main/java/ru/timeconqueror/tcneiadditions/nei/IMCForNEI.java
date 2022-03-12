package ru.timeconqueror.tcneiadditions.nei;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

public class IMCForNEI {
    public static void IMCSender() {
        setNBTAndSend("ru.timeconqueror.tcneiadditions.nei.arcaneworkbench.ArcaneCraftingShapelessHandler", "Thaumcraft:blockTable:15");
        setNBTAndSend("ru.timeconqueror.tcneiadditions.nei.TCNACrucibleRecipeHandler", "Thaumcraft:blockMetalDevice");
        setNBTAndSend("ru.timeconqueror.tcneiadditions.nei.TCNAInfusionRecipeHandler", "Thaumcraft:blockStoneDevice:2");
    }

    private static void setNBTAndSend(String name, String stack) {
        NBTTagCompound NBT = new NBTTagCompound();
        NBT.setString("handler", name);
        NBT.setString("modName", "Thaumcraft");
        NBT.setString("modId", "Thaumcraft");
        NBT.setBoolean("modRequired", true);
        NBT.setString("itemName", stack);
        NBT.setInteger("handlerHeight", 140);
        NBT.setInteger("maxRecipesPerPage", 2);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", NBT);
    }
}
