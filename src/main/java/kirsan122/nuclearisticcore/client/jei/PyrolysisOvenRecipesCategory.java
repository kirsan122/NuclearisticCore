package kirsan122.nuclearisticcore.client.jei;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.client.gui.screens.PyrolysisOvenScreen;
import kirsan122.nuclearisticcore.common.blockEntity.machines.PyrolysisOvenBlockEntity;
import kirsan122.nuclearisticcore.common.recipe.FluidPyrolysisOvenRecipe;
import kirsan122.nuclearisticcore.common.recipe.PyrolysisRecipe;
import kirsan122.nuclearisticcore.common.recipe.SolidPyrolysisOvenRecipe;
import kirsan122.nuclearisticcore.common.util.JeiUtils;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static kirsan122.nuclearisticcore.client.gui.screens.PyrolysisOvenScreen.GUI_FLUID_MODE;

/**
 * Created by kirsan122,
 * Data: 06.11.2026,
 * Time: 17:49
 */
public class PyrolysisOvenRecipesCategory implements IRecipeCategory<PyrolysisRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(NuclearisticCore.MOD_ID, "pyrolysis");

    public static final RecipeType<PyrolysisRecipe> TYPE = new RecipeType<>(UID, PyrolysisRecipe.class);

    private final IDrawable icon;

    public PyrolysisOvenRecipesCategory(IGuiHelper helper, ItemStack machineIcon) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, machineIcon);
    }

    @Override
    public RecipeType<PyrolysisRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 51;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.nuclearisticcore.pyrolysis");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(PyrolysisRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        if (recipe instanceof FluidPyrolysisOvenRecipe) {
            graphics.blit(GUI_FLUID_MODE, 0, 0, 47, 11, 82, 51);
            RenderUtils.drawTankDivisions(graphics, 6, 4);
            RenderUtils.drawTankDivisions(graphics, 60, 4);
        } else if (recipe instanceof SolidPyrolysisOvenRecipe) {
            graphics.blit(PyrolysisOvenScreen.GUI_SOLID_MODE, 0, 0, 56, 18, 82, 51);
            RenderUtils.drawTankDivisions(graphics, 3, 3);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PyrolysisRecipe recipe, IFocusGroup focuses) {
        if (recipe instanceof FluidPyrolysisOvenRecipe fluidRecipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6, 4)
                    .addIngredient(ForgeTypes.FLUID_STACK, fluidRecipe.input())
                    .setFluidRenderer(fluidRecipe.input().getAmount(), false, 16, 44);

            builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 4)
                    .addIngredient(ForgeTypes.FLUID_STACK, fluidRecipe.output())
                    .setFluidRenderer(fluidRecipe.output().getAmount(), false, 16, 44);

        } else if (recipe instanceof SolidPyrolysisOvenRecipe solidRecipe) {
            FluidStack fluid = solidRecipe.inputFluid();

            if (fluid != null && !fluid.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                        .addIngredient(ForgeTypes.FLUID_STACK, solidRecipe.inputFluid())
                        .setFluidRenderer(solidRecipe.inputFluid().getAmount(), false, 16, 44);
            }

            builder.addSlot(RecipeIngredientRole.INPUT, 24, 17)
                    .addIngredients(solidRecipe.ingredient().getIngredient());

            builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 17)
                    .addItemStack(solidRecipe.result());
        }
    }

    public static class GuiHandler implements IGuiContainerHandler<PyrolysisOvenScreen> {
        @Override
        public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(PyrolysisOvenScreen screen, double mouseX, double mouseY) {
            PyrolysisOvenBlockEntity blockEntity = screen.getMenu().getBlockEntity();

            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();

            if (JeiUtils.isMouseOver(mouseX, mouseY, 53, 15, 68, 58) && blockEntity.getCurrentMode() == 0) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(0), 0, guiLeft, guiTop, 53, 15, 44);
            }

            if (JeiUtils.isMouseOver(mouseX, mouseY, 107, 15, 122, 58) && blockEntity.getCurrentMode() == 0) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(1), 1, guiLeft, guiTop, 107, 15, 44);
            }

            if (JeiUtils.isMouseOver(mouseX, mouseY, 59, 21, 74, 64) && blockEntity.getCurrentMode() == 1) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(2), 2, guiLeft, guiTop, 59, 21, 44);
            }

            return Optional.empty();
        }
    }
}
