package kirsan122.nuclearisticcore.common.util;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by kirsan122,
 * Data: 01.11.2026,
 * Time: 13:09
 */
public final class JeiUtils {
    public static final IIngredientRenderer<ItemStack> ITEM_WITH_COUNT_RENDERER = new IIngredientRenderer<>() {
        @Override
        public void render(GuiGraphics guiGraphics, ItemStack ingredient) {
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.renderItem(ingredient, 0, 0);
            guiGraphics.renderItemDecorations(mc.font, ingredient, 0, 0);
        }

        @Override
        @SuppressWarnings("removal")
        public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            return Screen.getTooltipFromItem(Minecraft.getInstance(), ingredient);
        }
    };

    public static void applyCountRendererIfNeeded(IRecipeSlotBuilder slotBuilder, int count) {
        if (count > 1) {
            slotBuilder.setCustomRenderer(VanillaTypes.ITEM_STACK, ITEM_WITH_COUNT_RENDERER);
        }
    }

    public static <T extends BlockEntity> Optional<IClickableIngredient<?>> func_111(
            T blockEntity,
            Function<T, LazyOptional<IFluidHandler>> function,
            int tankIndex,
            int guiLeft, int guiTop,
            int startX, int startY, int height
    ) {
        FluidStack fluid = function.apply(blockEntity).map(t -> t.getFluidInTank(tankIndex)).orElse(FluidStack.EMPTY);
        if (!fluid.isEmpty()) {
            Rect2i area = new Rect2i(guiLeft + startX, guiTop + startY, 16, height);
            return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
        }

        return Optional.empty();
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    private static <T> IClickableIngredient<T> createClickableIngredient(IIngredientType<T> type, T value, Rect2i area) {
        return new IClickableIngredient<T>() {
            @Override
            @SuppressWarnings("removal")
            public ITypedIngredient<T> getTypedIngredient() {

                return new ITypedIngredient<T>() {
                    @Override
                    public IIngredientType<T> getType() {
                        return type;
                    }

                    @Override
                    public T getIngredient() {
                        return value;
                    }
                };
            }

            @Override
            public Rect2i getArea() {
                return area;
            }
        };
    }
}
