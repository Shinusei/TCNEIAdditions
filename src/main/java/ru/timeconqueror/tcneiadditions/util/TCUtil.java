package ru.timeconqueror.tcneiadditions.util;

import codechicken.nei.NEIServerUtils;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCUtil {
    public static List<InfusionRecipe> getInfusionRecipes(ItemStack result) {
        List<InfusionRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof InfusionRecipe && ((InfusionRecipe) r).getRecipeOutput() instanceof ItemStack) {
                ItemStack output = (ItemStack) ((InfusionRecipe) r).getRecipeOutput();
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(output, result)) {
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
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(output, result)) {
                    list.add((CrucibleRecipe) r);
                }
            }
        }
        return list;
    }

    public static List<InfusionRecipe> getInfusionRecipesByInput(ItemStack input) {
        ArrayList<InfusionRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof InfusionRecipe)) continue;
            InfusionRecipe tcRecipe = (InfusionRecipe) r;
            if (tcRecipe.getRecipeInput() == null || TCUtil.getAssociatedItemStack(tcRecipe.getRecipeOutput()) == null) continue;

            if (input.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(input).getAspects()[0];
                if (tcRecipe.getAspects().aspects.containsKey(aspect)) {
                    list.add(tcRecipe);
                }
            } else {
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(tcRecipe.getRecipeInput(), input) || matchInfusionComponents(tcRecipe.getComponents(), input)) {
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

    public static boolean matchInfusionComponents(ItemStack[] components, ItemStack stack) {
        for (ItemStack component : components) {
            for (ItemStack toCompare : getOreDictionaryMatchingItemsForInfusion(component)) {
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(toCompare, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean shouldShowRecipe(String username, String researchKey) {
        return ThaumcraftApiHelper.isResearchComplete(username, researchKey) || TCNAConfig.showLockedRecipes;
    }

    // Fix crash with broken item
    public static ItemStack getAssociatedItemStack(Object o) {
        if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack) o;
            if (stack.getItem() == null) {
                return stack;
            }
        }
        return NEIHelper.getAssociatedItemStack(o);
    }

    @SuppressWarnings("deprecation")
    public static List<ItemStack> getOreDictionaryMatchingItemsForInfusion(ItemStack stack) {
        // TC uses these deprecated methods
        // See thaumcraft.api.crafting.InfusionRecipe#areItemStacksEqual
        int oreID = OreDictionary.getOreID(stack);
        if (oreID != -1) {
            return OreDictionary.getOres(oreID);
        } else {
            return Collections.singletonList(stack);
        }
    }
}
