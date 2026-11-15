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

public record ElectrolyzerRecipe(ResourceLocation id, FluidStack inputFluid, FluidStack[] outputFluids) implements Recipe<Container> {
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }
    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }
    @Override
    public ResourceLocation getId() {
        return id;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return NCRecipes.ELECTROLYZER_SERIALIZER.get();
    }
    @Override
    public RecipeType<?> getType() {
        return NCRecipes.ELECTROLYZER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ElectrolyzerRecipe> {
        @Override
        public ElectrolyzerRecipe fromJson(ResourceLocation id, JsonObject json) {
            FluidStack input = RecipeUtils.parseFluid(json, "input");

            FluidStack[] outputs = RecipeUtils.parseFluidArray(json, 2, "outputs", "output_");
            return new ElectrolyzerRecipe(id, input, outputs);
        }

        @Override
        public @Nullable ElectrolyzerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            FluidStack input = buffer.readFluidStack();
            FluidStack[] outputs = RecipeUtils.readFluidArray(buffer);
            return new ElectrolyzerRecipe(id, input, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ElectrolyzerRecipe recipe) {
            buffer.writeFluidStack(recipe.inputFluid);
            RecipeUtils.writeFluidArray(buffer, recipe.outputFluids);
        }
    }
}