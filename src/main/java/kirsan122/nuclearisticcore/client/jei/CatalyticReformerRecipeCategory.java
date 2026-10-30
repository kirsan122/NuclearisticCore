package kirsan122.nuclearisticcore.client.jei;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.screens.CatalyticReformerScreen;
import kirsan122.nuclearisticcore.client.gui.screens.UniversalScreen;
import kirsan122.nuclearisticcore.common.blockEntity.machines.CatalyticReformerBlockEntity;
import kirsan122.nuclearisticcore.common.recipe.CatalyticReformerRecipe;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Created by kirsan122,
 * Data: 27.10.2026,
 * Time: 11:31
 */
@SuppressWarnings("removal")
public class CatalyticReformerRecipeCategory implements IRecipeCategory<CatalyticReformerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(NuclearisticCore.MOD_ID, "catalytic_reformer");
    public static final RecipeType<CatalyticReformerRecipe> TYPE = new RecipeType<>(UID, CatalyticReformerRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/catalytic_reformer_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public CatalyticReformerRecipeCategory(IGuiHelper helper, ItemStack icon) {
        this.background = helper.createDrawable(TEXTURE, 32, 15, 113, 56);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }

    @Override
    public @NotNull RecipeType<CatalyticReformerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.nuclearisticcore.catalytic_reforming");
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(@NotNull CatalyticReformerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        RenderUtils.drawTankDivisions(graphics, 4, 5);
        RenderUtils.drawTankDivisions(graphics, 66, 5);
        RenderUtils.drawTankDivisions(graphics, 93, 5);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CatalyticReformerRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 2)
                .addFluidStack(recipe.inputFluid().getFluid(), recipe.inputFluid().getAmount())
                .setFluidRenderer(recipe.inputFluid().getAmount(), false, 16, 52);

        if (recipe.outputFluids()[0] != null && !recipe.outputFluids()[0].isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 2)
                    .addFluidStack(recipe.outputFluids()[0].getFluid(), recipe.outputFluids()[0].getAmount())
                    .setFluidRenderer(recipe.outputFluids()[0].getAmount(), false, 16, 52);
        }

        if (recipe.outputFluids()[1] != null && !recipe.outputFluids()[1].isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 2)
                    .addFluidStack(recipe.outputFluids()[1].getFluid(), recipe.outputFluids()[1].getAmount())
                    .setFluidRenderer(recipe.outputFluids()[1].getAmount(), false, 16, 52);
        }

        int tier = recipe.catalystTier();
        List<ItemStack> validCatalysts = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> {
                    if (item instanceof kirsan122.nuclearisticcore.common.item.CatalystItem catalyst) {
                        return catalyst.getTier() == tier;
                    }
                    return false;
                })
                .map(ItemStack::new)
                .toList();

        if (!validCatalysts.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 36, 26).addItemStacks(validCatalysts);
        }
    }


    public static class GuiHandler implements IGuiContainerHandler<CatalyticReformerScreen> {

        @Override
        public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(CatalyticReformerScreen screen, double mouseX, double mouseY) {
            CatalyticReformerBlockEntity entity = screen.getMenu().getBlockEntity();

            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int x = (int) (mouseX - guiLeft);
            int y = (int) (mouseY - guiTop);

            if (isMouseOver(x, y, 36)) {
                FluidStack fluid = entity.getTankCapability(0).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 36, guiTop + 17, 16, 52);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }
            if (isMouseOver(x, y, 98)) {
                FluidStack fluid = entity.getTankCapability(1).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 98, guiTop + 17, 16, 52);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }
            if (isMouseOver(x, y, 125)) {
                FluidStack fluid = entity.getTankCapability(1).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 125, guiTop + 17, 16, 52);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }

            return Optional.empty();
        }

        private boolean isMouseOver(int mouseX, int mouseY, int x) {
            return mouseX >= x && mouseX <= x + 16 && mouseY >= 17 && mouseY < 17 + 52;
        }

        private <T> IClickableIngredient<T> createClickableIngredient(IIngredientType<T> type, T value, Rect2i area) {
            return new IClickableIngredient<T>() {
                @Override
                public @NotNull ITypedIngredient<T> getTypedIngredient() {
                    return new ITypedIngredient<T>() {
                        @Override
                        public @NotNull IIngredientType<T> getType() {
                            return type;
                        }

                        @Override
                        public @NotNull T getIngredient() {
                            return value;
                        }
                    };
                }

                @Override
                public @NotNull Rect2i getArea() {
                    return area;
                }
            };
        }
    }
}