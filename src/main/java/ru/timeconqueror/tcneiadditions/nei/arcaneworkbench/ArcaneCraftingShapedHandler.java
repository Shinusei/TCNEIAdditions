package ru.timeconqueror.tcneiadditions.nei.arcaneworkbench;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;
import ru.timeconqueror.tcneiadditions.util.TCNAConfig;
import ru.timeconqueror.tcneiadditions.util.TCUtil;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ArcaneCraftingShapedHandler extends ArcaneShapedRecipeHandler {
    private final String userName = Minecraft.getMinecraft().getSession().getUsername();

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getOverlayIdentifier())) {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof ShapedArcaneRecipe) {
                    ShapedArcaneRecipe tcRecipe = (ShapedArcaneRecipe) o;
                    boolean isResearchComplete = TCUtil.shouldShowRecipe(this.userName, tcRecipe.getResearch());
                    ArcaneShapedCachedRecipe recipe = new ArcaneShapedCachedRecipe(tcRecipe, isResearchComplete);
                    if (recipe.isValid()) {
                        recipe.computeVisuals();
                        this.arecipes.add(recipe);
                        this.aspectsAmount.add(getAmounts(tcRecipe));
                    }
                }
            }
        } else if (outputId.equals("item")) {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) result.getItem();
            WandRod rod = wand.getRod(result);
            WandCap cap = wand.getCap(result);
            boolean isResearchComplete = false;
            if (!wand.isSceptre(result) || TCUtil.shouldShowRecipe(userName, "SCEPTRE")) {
                if (TCUtil.shouldShowRecipe(userName, cap.getResearch())
                        && TCUtil.shouldShowRecipe(userName, rod.getResearch())) {
                    isResearchComplete = true;
                }
            }
            if (!TCNAClient.getInstance().areWandRecipesDeleted()) {
                ArcaneWandCachedRecipe recipe =
                        new ArcaneWandCachedRecipe(rod, cap, result, wand.isSceptre(result), isResearchComplete);
                recipe.computeVisuals();
                this.arecipes.add(recipe);
                this.aspectsAmount.add(NEIHelper.getWandAspectsWandCost(result));
            }

            loadShapedRecipesForWands(result, isResearchComplete);
        } else {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof ShapedArcaneRecipe) {
                    ShapedArcaneRecipe shapedArcaneRecipe = (ShapedArcaneRecipe) o;
                    boolean isResearchComplete = TCUtil.shouldShowRecipe(userName, shapedArcaneRecipe.getResearch());

                    ArcaneShapedCachedRecipe recipe =
                            new ArcaneShapedCachedRecipe(shapedArcaneRecipe, isResearchComplete);

                    if (recipe.isValid()
                            && NEIServerUtils.areStacksSameTypeCraftingWithNBT(
                                    shapedArcaneRecipe.getRecipeOutput(), result)) {
                        recipe.computeVisuals();
                        this.arecipes.add(recipe);
                        this.aspectsAmount.add(getAmounts(shapedArcaneRecipe));
                    }
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (Object o : ThaumcraftApi.getCraftingRecipes()) {
            if (o instanceof ShapedArcaneRecipe) {
                ShapedArcaneRecipe tcRecipe = (ShapedArcaneRecipe) o;
                ArcaneShapedCachedRecipe recipe = new ArcaneShapedCachedRecipe(tcRecipe, true);
                if (recipe.isValid()
                        && recipe.containsWithNBT(recipe.ingredients, ingredient)
                        && TCUtil.shouldShowRecipe(this.userName, tcRecipe.getResearch())) {
                    recipe.computeVisuals();
                    recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                    this.arecipes.add(recipe);
                    this.aspectsAmount.add(getAmounts(tcRecipe));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadShapedRecipesForWands(ItemStack wandStack, boolean isResearchComplete) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            throw new RuntimeException("This method works only for Thaumcraft Wands! Provided: " + wandStack);
        }

        ItemWandCasting wand = ((ItemWandCasting) wandStack.getItem());
        WandRod rod = wand.getRod(wandStack);
        WandCap cap = wand.getCap(wandStack);
        boolean isSceptre = wand.isSceptre(wandStack);

        ((List<Object>) ThaumcraftApi.getCraftingRecipes())
                .stream()
                        .filter(o -> o instanceof ShapedArcaneRecipe)
                        .filter(r -> {
                            ItemStack output = ((ShapedArcaneRecipe) r).output;
                            if (!(output.getItem() instanceof ItemWandCasting)) return false;

                            if (isSceptre != wand.isSceptre(output)) return false;

                            if (output.getItem().getClass()
                                    != wandStack.getItem().getClass()) return false;

                            WandRod outputRod = wand.getRod(output);
                            WandCap outputCap = wand.getCap(output);

                            return outputRod.getTag().equals(rod.getTag())
                                    && outputCap.getTag().equals(cap.getTag());
                        })
                        .forEach(o -> {
                            ShapedArcaneRecipe arcaneRecipe = (ShapedArcaneRecipe) o;
                            // this needs to be ArcaneShapedCachedRecipe instead of ArcaneWandCachedRecipe
                            // because of modified recipe
                            ArcaneShapedCachedRecipe recipe =
                                    new ArcaneShapedCachedRecipe(arcaneRecipe, isResearchComplete);
                            recipe.computeVisuals();
                            this.arecipes.add(recipe);
                            this.aspectsAmount.add(getAmounts(arcaneRecipe));
                        });
    }

    @Override
    public void drawBackground(int recipeIndex) {
        boolean isResearchComplete;
        CachedRecipe cRecipe = arecipes.get(recipeIndex);
        if (cRecipe instanceof ArcaneShapedCachedRecipe) {
            ArcaneShapedCachedRecipe recipe = (ArcaneShapedCachedRecipe) cRecipe;
            isResearchComplete = recipe.isResearchComplete;
        } else if (cRecipe instanceof ArcaneWandCachedRecipe) {
            ArcaneWandCachedRecipe recipe = (ArcaneWandCachedRecipe) cRecipe;
            isResearchComplete = recipe.isResearchComplete;
        } else {
            throw new RuntimeException("Incompatible recipe type found: " + cRecipe.getClass());
        }

        if (isResearchComplete) {
            super.drawBackground(recipeIndex);
            return;
        }

        int x = 34;
        int y = -15;
        UtilsFX.bindTexture("textures/gui/gui_researchbook_overlay.png");
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glTranslatef((float) x, (float) y, 0.0F);
        GL11.glScalef(1.7F, 1.7F, 1.0F);
        GuiDraw.drawTexturedModalRect(20, 7, 20, 3, 16, 16);
        GL11.glPopMatrix();
    }

    @Override
    public List<PositionedStack> getIngredientStacksForOverlay(int recipeIndex) {
        CachedRecipe recipe = arecipes.get(recipeIndex);
        return recipe instanceof IArcaneOverlayProvider
                ? ((IArcaneOverlayProvider) recipe).getPositionedStacksForOverlay()
                : null;
    }

    @Override
    public void drawExtras(int recipeIndex) {
        boolean isResearchComplete;
        String researchKeyNormal = null;
        String researchKeyRod = null;
        String researchKeyCap = null;
        CachedRecipe cRecipe = arecipes.get(recipeIndex);
        ItemStack result = cRecipe.getResult().item;
        if (result.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) result.getItem();
            WandRod rod = wand.getRod(result);
            WandCap cap = wand.getCap(result);
            if (cRecipe instanceof ArcaneShapedCachedRecipe) {
                isResearchComplete = ((ArcaneShapedCachedRecipe) cRecipe).isResearchComplete;
            } else if (cRecipe instanceof ArcaneWandCachedRecipe) {
                isResearchComplete = ((ArcaneWandCachedRecipe) cRecipe).isResearchComplete;
            } else {
                throw new RuntimeException("Incompatible recipe type found: " + cRecipe.getClass());
            }
            researchKeyRod = rod.getResearch();
            researchKeyCap = cap.getResearch();
        } else if (cRecipe instanceof ArcaneShapedCachedRecipe) {
            ArcaneShapedCachedRecipe recipe = (ArcaneShapedCachedRecipe) cRecipe;
            isResearchComplete = recipe.isResearchComplete;
            researchKeyNormal = recipe.researchKey;
        } else {
            // ArcaneWandCachedRecipe with result stack not being wand cannot happen
            throw new RuntimeException("Incompatible recipe type found: " + cRecipe.getClass());
        }

        if (isResearchComplete) {
            super.drawExtras(recipeIndex);
        } else {
            String textToDraw = I18n.format("tcneiadditions.research.missing");
            int y = 28;
            for (Object text : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDraw, 162)) {
                GuiDraw.drawStringC((String) text, 82, y, Color.BLACK.getRGB(), false);
                y += 11;
            }
        }

        if (TCNAConfig.showResearchKey) {
            int y = 135;
            if (researchKeyNormal != null) {
                String textToDraw = I18n.format("tcneiadditions.research.researchKey", researchKeyNormal);
                for (Object text : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDraw, 162)) {
                    GuiDraw.drawStringC((String) text, 82, y, Color.BLACK.getRGB(), false);
                    y += 11;
                }
            } else {
                String textToDrawWand = I18n.format("tcneiadditions.research.researchKey_rod", researchKeyRod);
                for (Object text :
                        Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDrawWand, 162)) {
                    GuiDraw.drawStringC((String) text, 82, y, Color.BLACK.getRGB(), false);
                    y += 11;
                }
                String textToDrawCap = I18n.format("tcneiadditions.research.researchKey_cap", researchKeyCap);
                for (Object text :
                        Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDrawCap, 162)) {
                    GuiDraw.drawStringC((String) text, 82, y, Color.BLACK.getRGB(), false);
                    y += 11;
                }
            }
        }
    }

    private class ArcaneShapedCachedRecipe extends ShapedRecipeHandler.CachedShapedRecipe
            implements IArcaneOverlayProvider {
        protected AspectList aspects;
        protected Object[] overlay;
        protected int width;
        protected int height;
        private final boolean isResearchComplete;
        private final String researchKey;

        public ArcaneShapedCachedRecipe(ShapedArcaneRecipe recipe, boolean isResearchComplete) {
            super(recipe.width, recipe.height, recipe.getInput(), recipe.getRecipeOutput());
            this.result = new PositionedStack(recipe.getRecipeOutput(), 74, 2);
            this.aspects = recipe.getAspects();
            this.overlay = recipe.getInput();
            this.width = recipe.width;
            this.height = recipe.height;
            this.isResearchComplete = isResearchComplete;
            this.researchKey = recipe.getResearch();
            NEIHelper.addAspectsToIngredients(this.aspects, this.ingredients, 0);
        }

        public boolean isValid() {
            return !this.ingredients.isEmpty() && this.result != null;
        }

        @Override
        public void setIngredients(int width, int height, Object[] items) {
            if (items != null && items.length > 0) {
                int y;
                int x;
                int[][][] positions2 = new int[width][height][2];
                int shiftX = 0;
                int shiftY = 0;
                for (x = 0; x < width && x < 3; ++x) {
                    for (y = 0; y < height && y < 3; ++y) {
                        positions2[x][y][0] = positions[y][x][0];
                        positions2[x][y][1] = positions[y][x][1];
                    }
                }
                for (x = 0; x < width && x < 3; ++x) {
                    for (y = 0; y < height && y < 3; ++y) {
                        if (items[y * width + x] == null
                                || !(items[y * width + x] instanceof ItemStack)
                                        && !(items[y * width + x] instanceof ItemStack[])
                                        && !(items[y * width + x] instanceof String)
                                        && !(items[y * width + x] instanceof List)
                                || items[y * width + x] instanceof List && ((List) items[y * width + x]).isEmpty())
                            continue;
                        PositionedStack stack = new PositionedStack(
                                items[y * width + x],
                                positions2[x][y][0] + shiftX,
                                positions2[x][y][1] + shiftY,
                                false);
                        stack.setMaxSize(1);
                        this.ingredients.add(stack);
                    }
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            if (!this.isResearchComplete) return Collections.emptyList();
            return super.getIngredients();
        }

        @Override
        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) return;
            super.setIngredientPermutation(ingredients, ingredient);
        }

        @Override
        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(ingredient).getAspects()[0];
                return this.aspects.aspects.containsKey(aspect);
            }
            return super.contains(ingredients, ingredient);
        }

        @Override
        public ArrayList<PositionedStack> getPositionedStacksForOverlay() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (overlay != null && overlay.length > 0) {
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        Object object = overlay[y * width + x];
                        if ((object instanceof ItemStack
                                || object instanceof ItemStack[]
                                || object instanceof String
                                || (object instanceof List && !((List<?>) object).isEmpty()))) {
                            stacks.add(new PositionedStack(object, 40 + x * 24, 40 + y * 24));
                        }
                    }
                }
            }

            return stacks;
        }
    }

    private class ArcaneWandCachedRecipe extends ShapedRecipeHandler.CachedShapedRecipe
            implements IArcaneOverlayProvider {
        protected AspectList aspects;
        protected Object[] overlay;
        private final boolean isResearchComplete;
        private final String rodResearchKey;
        private final String capResearchKey;

        public ArcaneWandCachedRecipe(
                WandRod rod, WandCap cap, ItemStack result, boolean isScepter, boolean isResearchComplete) {
            super(3, 3, isScepter ? NEIHelper.buildScepterInput(rod, cap) : NEIHelper.buildWandInput(rod, cap), result);
            this.overlay = isScepter ? NEIHelper.buildScepterInput(rod, cap) : NEIHelper.buildWandInput(rod, cap);
            this.result = new PositionedStack(result, 74, 2);
            this.aspects = NEIHelper.getPrimalAspectListFromAmounts(NEIHelper.getWandAspectsWandCost(result));
            this.isResearchComplete = isResearchComplete;
            this.rodResearchKey = rod.getResearch() != null ? rod.getResearch() : EnumChatFormatting.ITALIC + "null";
            this.capResearchKey = cap.getResearch() != null ? cap.getResearch() : EnumChatFormatting.ITALIC + "null";
            NEIHelper.addAspectsToIngredients(this.aspects, this.ingredients, 0);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            if (!this.isResearchComplete) return Collections.emptyList();
            return super.getIngredients();
        }

        @Override
        public void setIngredients(int width, int height, Object[] items) {
            if (items != null && items.length > 0) {
                int[][] positions = new int[][] {
                    {48, 32}, {75, 33}, {103, 33}, {49, 60}, {76, 60}, {103, 60}, {49, 87}, {76, 87}, {103, 87}
                };
                int[][] positions2 = new int[][] {{48, 32}, {75, 33}, {49, 60}, {76, 60}};
                int shiftX = 0;
                int shiftY = 0;
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        Object object = items[y * width + x];
                        if (!(object instanceof ItemStack)
                                        && !(object instanceof ItemStack[])
                                        && !(object instanceof String)
                                        && !(object instanceof List)
                                || object instanceof List && ((List<?>) object).isEmpty()) continue;
                        if (width == 2 && height == 2) {
                            positions = positions2;
                        }
                        PositionedStack stack = new PositionedStack(
                                object,
                                positions[y * width + x][0] + shiftX,
                                positions[y * width + x][1] + shiftY,
                                false);
                        stack.setMaxSize(1);
                        this.ingredients.add(stack);
                    }
                }
            }
        }

        @Override
        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) return;
            super.setIngredientPermutation(ingredients, ingredient);
        }

        @Override
        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(ingredient).getAspects()[0];
                return this.aspects.aspects.containsKey(aspect);
            }
            return super.contains(ingredients, ingredient);
        }

        @Override
        public ArrayList<PositionedStack> getPositionedStacksForOverlay() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (overlay != null && overlay.length > 0) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        Object object = overlay[y * 3 + x];
                        if ((object instanceof ItemStack
                                || object instanceof ItemStack[]
                                || object instanceof String
                                || (object instanceof List && !((List<?>) object).isEmpty()))) {
                            stacks.add(new PositionedStack(object, 40 + x * 24, 40 + y * 24));
                        }
                    }
                }
            }

            return stacks;
        }
    }
}
