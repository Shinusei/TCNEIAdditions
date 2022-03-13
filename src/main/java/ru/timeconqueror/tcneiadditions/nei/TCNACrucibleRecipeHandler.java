package ru.timeconqueror.tcneiadditions.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.tcneiadditions.util.TCUtil;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TCNACrucibleRecipeHandler extends CrucibleRecipeHandler {
    private final String userName = Minecraft.getMinecraft().getSession().getUsername();

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getOverlayIdentifier())) {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof CrucibleRecipe) {
                    CrucibleRecipe tcRecipe = (CrucibleRecipe) o;
                    boolean isResearchComplete = TCUtil.isResearchComplete(this.userName, tcRecipe.key);
                    CrucibleCachedRecipe recipe = new CrucibleCachedRecipe(tcRecipe, isResearchComplete);
                    if (recipe.isValid()) {
                        recipe.computeVisuals();
                        this.arecipes.add(recipe);
                        this.aspectsAmount.add(recipe.aspects);
                    }
                }
            }
        } else if (outputId.equals("item")) {
            this.loadCraftingRecipes((ItemStack)results[0]);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (CrucibleRecipe tcRecipe : TCUtil.getCrucibleRecipes(result)) {
            boolean isResearchComplete = TCUtil.isResearchComplete(this.userName, tcRecipe.key);
            CrucibleCachedRecipe recipe = new CrucibleCachedRecipe(tcRecipe, isResearchComplete);
            recipe.computeVisuals();
            this.arecipes.add(recipe);
            this.aspectsAmount.add(recipe.aspects);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<CrucibleRecipe> tcRecipeList = TCUtil.getCrucibleRecipesByInput(ingredient);

        for (CrucibleRecipe tcRecipe : tcRecipeList) {
            if (tcRecipe != null && TCUtil.isResearchComplete(this.userName, tcRecipe.key)) {
                // recipe input is invisible unless complete research
                CrucibleCachedRecipe recipe = new CrucibleCachedRecipe(tcRecipe, true);
                recipe.computeVisuals();
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                this.arecipes.add(recipe);
                this.aspectsAmount.add(recipe.aspects);
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        CrucibleCachedRecipe recipe = (CrucibleCachedRecipe) arecipes.get(recipeIndex);
        if (recipe.isResearchComplete) {
            super.drawBackground(recipeIndex);
            return;
        }

        int x = 30;
        int y = 3;
        GL11.glPushMatrix();
        UtilsFX.bindTexture("textures/gui/gui_researchbook_overlay.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glTranslatef((float)x, (float)y, 0.0F);
        GL11.glScalef(1.75F, 1.75F, 1.0F);
        GuiDraw.drawTexturedModalRect(0, 0, 0, 3, 56, 17);
        GL11.glPopMatrix();
    }

    @Override
    public void drawExtras(int recipeIndex) {
        CrucibleCachedRecipe recipe = (CrucibleCachedRecipe) arecipes.get(recipeIndex);
        if (recipe.isResearchComplete) {
            super.drawExtras(recipeIndex);
            return;
        }

        String textToDraw = I18n.format("tcneiadditions.research.missing");
        int y = 28;
        for (Object text : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDraw, 162)) {
            GuiDraw.drawStringC((String) text, 82, y, Color.BLACK.getRGB(), false);
            y += 11;
        }
    }

    private class CrucibleCachedRecipe extends CachedRecipe {
        public List<PositionedStack> ingredients;
        public PositionedStack result;
        private AspectList aspects;
        private final boolean isResearchComplete;

        public CrucibleCachedRecipe(CrucibleRecipe recipe, boolean isResearchComplete) {
            this.setIngredient(recipe.catalyst);
            this.setResult(recipe.getRecipeOutput());
            this.setAspectList(recipe.aspects);
            this.isResearchComplete = isResearchComplete;
            NEIHelper.addAspectsToIngredients(this.aspects, this.ingredients, 2);
        }

        protected void setIngredient(Object in) {
            if (in != null && NEIServerUtils.extractRecipeItems(in).length > 0) {
                PositionedStack stack = new PositionedStack(in, 51, 30, false);
                stack.setMaxSize(1);
                this.ingredients = new ArrayList<>(Collections.singletonList(stack));
            }
        }

        @Override
        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) return;
            super.setIngredientPermutation(ingredients, ingredient);
        }

        protected void setResult(ItemStack out) {
            if (out != null) {
                this.result = new PositionedStack(out, 71, 8, false);
            }

        }

        protected void setAspectList(AspectList aspects) {
            this.aspects = aspects;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        public AspectList getAspectList() {
            return this.aspects;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            if (!this.isResearchComplete) return Collections.emptyList();
            return new ArrayList<PositionedStack>(this.ingredients);
        }

        public void computeVisuals() {
            for (PositionedStack p : this.ingredients) {
                p.generatePermutations();
            }
        }

        public boolean isValid() {
            return !this.ingredients.isEmpty() && this.result != null;
        }

        @Override
        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) {
                return false;
            }
            return super.contains(ingredients, ingredient);
        }
    }
}
