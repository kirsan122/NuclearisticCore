package kirsan122.nuclearisticcore.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.util.RecipeUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:18
 */
public record FluidPyrolysisOvenRecipe(ResourceLocation id, FluidStack input, FluidStack output) implements Recipe<Container>, PyrolysisRecipe {

    @Override
    public boolean matches(Container container, Level level) { return false; }
    @Override
    public ItemStack assemble(Container container, RegistryAccess access) { return ItemStack.EMPTY; }
    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override
    public ItemStack getResultItem(RegistryAccess access) { return ItemStack.EMPTY; }
    @Override
    public ResourceLocation getId() { return id; }
    @Override
    public RecipeSerializer<?> getSerializer() { return NCRecipes.FLUID_PYROLYSIS_OVEN_SERIALIZER.get(); }
    @Override
    public RecipeType<?> getType() {return NCRecipes.FLUID_PYROLYSIS_OVEN_TYPE.get();}

    public static class Serializer implements RecipeSerializer<FluidPyrolysisOvenRecipe> {
        @Override
        public FluidPyrolysisOvenRecipe fromJson(ResourceLocation id, JsonObject json) {

            FluidStack input = RecipeUtils.parseFluid(json, "input");
            FluidStack output = RecipeUtils.parseFluid(json, "output");

            return new FluidPyrolysisOvenRecipe(id, input, output);
        }

        @Override
        public @Nullable FluidPyrolysisOvenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            FluidStack input = buffer.readFluidStack();
            FluidStack output = buffer.readFluidStack();

            return new FluidPyrolysisOvenRecipe(id, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidPyrolysisOvenRecipe recipe) {
            buffer.writeFluidStack(recipe.input);
            buffer.writeFluidStack(recipe.output);
        }
    }
}

