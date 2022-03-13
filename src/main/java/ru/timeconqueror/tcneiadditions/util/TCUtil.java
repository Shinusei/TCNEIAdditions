package ru.timeconqueror.tcneiadditions.util;

import codechicken.nei.NEIServerUtils;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.List;

public class TCUtil {
    public static List<InfusionRecipe> getInfusionRecipes(ItemStack result) {
        List<InfusionRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof InfusionRecipe && ((InfusionRecipe) r).getRecipeOutput() instanceof ItemStack) {
                ItemStack output = (ItemStack) ((InfusionRecipe) r).getRecipeOutput();
                if (NEIServerUtils.areStacksSameTypeCrafting(output, result)) {
                    list.add((InfusionRecipe) r);
                }
            }
        }
        return list;
    }

    public static List<CrucibleRecipe> getCrucibleRecipes(ItemStack result) {
        List<CrucibleRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof CrucibleRecipe && ((CrucibleRecipe) r).getRecipeOutput() != null) {
                ItemStack output = ((CrucibleRecipe) r).getRecipeOutput();
                if (NEIServerUtils.areStacksSameTypeCrafting(output, result)) {
                    list.add((CrucibleRecipe) r);
                }
            }
        }
        return list;
    }

    public static List<InfusionRecipe> getInfusionRecipesByInput(ItemStack input) {
        ArrayList<InfusionRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r == null) continue;
            if (!(r instanceof InfusionRecipe)) continue;
            InfusionRecipe tcRecipe = (InfusionRecipe) r;
            if (tcRecipe.getRecipeInput() == null) continue;

            if (input.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(input).getAspects()[0];
                if (tcRecipe.getAspects().aspects.containsKey(aspect)) {
                    list.add(tcRecipe);
                }
            } else {
                if (NEIServerUtils.areStacksSameTypeCrafting(tcRecipe.getRecipeInput(), input) || containsItemStack(tcRecipe.getComponents(), input)) {
                    list.add(tcRecipe);
                }
            }
        }
        return list;
    }

    public static List<CrucibleRecipe> getCrucibleRecipesByInput(ItemStack input) {
        ArrayList<CrucibleRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r == null) continue;
            if (!(r instanceof CrucibleRecipe)) continue;
            CrucibleRecipe tcRecipe = (CrucibleRecipe) r;

            if (input.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(input).getAspects()[0];
                if (tcRecipe.aspects.aspects.containsKey(aspect)) {
                    list.add(tcRecipe);
                }
            } else {
                if (tcRecipe.catalystMatches(input)) {
                    list.add(tcRecipe);
                }
            }
        }
        return list;
    }

    public static boolean containsItemStack(ItemStack[] array, ItemStack stack) {
        for (ItemStack toCompare : array) {
            if (NEIServerUtils.areStacksSameTypeCrafting(toCompare, stack))
                return true;
        }
        return false;
    }
}
