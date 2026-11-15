package kirsan122.nuclearisticcore.client.jei;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.client.gui.screens.ChemicalMixerScreen;
import kirsan122.nuclearisticcore.common.blockEntity.machines.ChemicalMixerBlockEntity;
import kirsan122.nuclearisticcore.common.inventory.SizedIngredient;
import kirsan122.nuclearisticcore.common.recipe.ChemicalMixerRecipe;
import kirsan122.nuclearisticcore.common.util.JeiUtils;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Created by kirsan122,
 * Data: 31.10.2026,
 * Time: 10:31
 */
@SuppressWarnings("removal")
public class ChemicalMixerRecipeCategory implements IRecipeCategory<ChemicalMixerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(NuclearisticCore.MOD_ID, "chemical_mixing");
    public static final RecipeType<ChemicalMixerRecipe> TYPE = new RecipeType<>(UID, ChemicalMixerRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/jei/chemical_mixer.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ChemicalMixerRecipeCategory(IGuiHelper helper, ItemStack icon) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 135, 42);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }

    @Override
    public RecipeType<ChemicalMixerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.nuclearisticcore.chemical_mixing");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(ChemicalMixerRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(GuiData.DIVISIONS_3, 4, 4, 0, 0, 16, 34, 16, 34);
        graphics.blit(GuiData.DIVISIONS_3, 22, 4, 0, 0, 16, 34, 16, 34);
        graphics.blit(GuiData.DIVISIONS_3, 94, 4, 0, 0, 16, 34, 16, 34);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChemicalMixerRecipe recipe, IFocusGroup focuses) {
        SizedIngredient inputIng = recipe.ingredient();
        if (inputIng != null && inputIng != SizedIngredient.EMPTY && inputIng.getSizedStacks().length > 0) {
            var inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 42, 13)
                    .addItemStacks(java.util.List.of(inputIng.getSizedStacks()));
            JeiUtils.applyCountRendererIfNeeded(inputSlot, inputIng.getCount());
        }

        ItemStack outputItem = recipe.resultItem();
        if (outputItem != null && !outputItem.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 114, 13)
                    .addItemStack(outputItem);
        }

        FluidStack input_1 = recipe.inputFluids()[0];
        FluidStack input_2 = recipe.inputFluids()[1];

        if (input_1 != null && !input_1.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
                    .addIngredient(ForgeTypes.FLUID_STACK, input_1)
                    .setFluidRenderer(input_1.getAmount(), false, 16, 34);
        }
        if (input_2 != null && !input_2.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 22, 4)
                    .addIngredient(ForgeTypes.FLUID_STACK, input_2)
                    .setFluidRenderer(input_2.getAmount(), false, 16, 34);

        }
        FluidStack output = recipe.outputFluid();
        if (output != null && !output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 4)
                    .addIngredient(ForgeTypes.FLUID_STACK, output)
                    .setFluidRenderer(output.getAmount(), false, 16, 34);
        }
    }

    public static class GuiHandler implements IGuiContainerHandler<ChemicalMixerScreen> {

        @Override
        public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(ChemicalMixerScreen screen, double mouseX, double mouseY) {
            ChemicalMixerBlockEntity blockEntity = screen.getMenu().getBlockEntity();


            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();

            if (JeiUtils.isMouseOver(mouseX, mouseY, 32, 16, 47, 49)) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(0), 0, guiLeft, guiTop, 32, 16, 34);
            }

            if (JeiUtils.isMouseOver(mouseX, mouseY, 50, 16, 65, 49)) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(1), 1, guiLeft, guiTop, 50, 16, 34);
            }

            if (JeiUtils.isMouseOver(mouseX, mouseY, 122, 16, 137, 49)) {
                return JeiUtils.func_111(blockEntity, be -> be.getTankCapability(2), 2, guiLeft, guiTop, 122, 16, 34);
            }

            return Optional.empty();
        }

    }
}
