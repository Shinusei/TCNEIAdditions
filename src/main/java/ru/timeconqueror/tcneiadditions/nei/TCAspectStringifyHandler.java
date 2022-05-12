package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.api.IStackStringifyHandler;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TCAspectStringifyHandler implements IStackStringifyHandler {
    @Override
    public NBTTagCompound convertItemStackToNBT(ItemStack stack, boolean saveStackSize) {
        if (!(stack.getItem() instanceof ItemAspect)) {
            return null;
        }

        AspectList aspectList = ItemAspect.getAspects(stack);
        if (aspectList == null) {
            return null;
        }
        Aspect aspect = aspectList.getAspects()[0];
        NBTTagCompound nbtTag = new NBTTagCompound();
        nbtTag.setString("TCAspect", aspect.getTag());
        return nbtTag;
    }

    @Override
    public ItemStack convertNBTToItemStack(NBTTagCompound nbtTag) {
        if (!nbtTag.hasKey("TCAspect")) {
            return null;
        }

        Aspect aspect = Aspect.getAspect(nbtTag.getString("TCAspect"));
        ItemStack aspectStack = new ItemStack(ModItems.itemAspect);
        ItemAspect.setAspect(aspectStack, aspect);
        return aspectStack;
    }

    @Override
    public FluidStack getFluid(ItemStack stack) {
        return null;
    }
}
