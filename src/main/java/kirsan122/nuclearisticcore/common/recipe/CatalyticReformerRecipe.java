package kirsan122.nuclearisticcore.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.util.RecipeUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
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
 * Data: 25.10.2026,
 * Time: 17:12
 */
public record CatalyticReformerRecipe(ResourceLocation id, FluidStack inputFluid, FluidStack[] outputFluids, int catalystTier) implements Recipe<Container> {
    @Override
    public boolean matches(Container pContainer, Level pLevel) { return false; }
    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override
    public ResourceLocation getId() { return id; }
    @Override
    public RecipeSerializer<?> getSerializer() { return NCRecipes.CATALYTIC_REFORMER_SERIALIZER.get(); }
    @Override
    public RecipeType<?> getType() { return NCRecipes.CATALYTIC_REFORMER_TYPE.get(); }

    public static class Serializer implements RecipeSerializer<CatalyticReformerRecipe> {
        @Override
        public CatalyticReformerRecipe fromJson(ResourceLocation id, JsonObject json) {

            FluidStack input = RecipeUtils.parseFluid(json, "input");

            FluidStack[] outputs = RecipeUtils.parseFluidArray(json, 2, "outputs", "output");

            int catalyticTier = GsonHelper.getAsInt(json, "catalyticTier");

            if (catalyticTier == 0) {
                throw new IllegalArgumentException("Catalytic tier cannot be '0' for this recipe!");
            }
            return new CatalyticReformerRecipe(id, input, outputs, catalyticTier);
        }

        @Override
        public @Nullable CatalyticReformerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            FluidStack input = buffer.readFluidStack();
            FluidStack[] outputs = RecipeUtils.readFluidArray(buffer);
            int catalyticTier = buffer.readInt();
            return new CatalyticReformerRecipe(id, input, outputs, catalyticTier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CatalyticReformerRecipe recipe) {
            buffer.writeFluidStack(recipe.inputFluid);
            RecipeUtils.writeFluidArray(buffer, recipe.outputFluids);
            buffer.writeInt(recipe.catalystTier());
        }
    }
}
