package kirsan122.nuclearisticcore.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.inventory.SizedIngredient;
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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:19
 */
public record SolidPyrolysisOvenRecipe(ResourceLocation id, FluidStack inputFluid, SizedIngredient ingredient, ItemStack result) implements Recipe<Container>, PyrolysisRecipe {

    @Override
    public boolean matches(Container container, Level level) {return ingredient.test(container.getItem(0));}
    @Override
    public ItemStack assemble(Container container, RegistryAccess access) { return result.copy(); }
    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override
    public ItemStack getResultItem(RegistryAccess access) { return result; }
    @Override
    public ResourceLocation getId() { return id; }
    @Override
    public RecipeSerializer<?> getSerializer() { return NCRecipes.SOLID_PYROLYSIS_OVEN_SERIALIZER.get(); }
    @Override
    public RecipeType<?> getType() {return NCRecipes.SOLID_PYROLYSIS_OVEN_TYPE.get();}

    public static class Serializer implements RecipeSerializer<SolidPyrolysisOvenRecipe> {
        @Override
        public SolidPyrolysisOvenRecipe fromJson(ResourceLocation id, JsonObject json) {
            SizedIngredient ingredient = json.has("ingredient")
                    ? SizedIngredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"))
                    : SizedIngredient.EMPTY;

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            FluidStack inputFluid = json.has("inputFluid")
                    ? RecipeUtils.parseFluid(json, "inputFluid")
                    : FluidStack.EMPTY;

            return new SolidPyrolysisOvenRecipe(id, inputFluid, ingredient, result);
        }

        @Override
        public @Nullable SolidPyrolysisOvenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            FluidStack inputFluid = buffer.readFluidStack();
            SizedIngredient ingredient = SizedIngredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();

            return new SolidPyrolysisOvenRecipe(id, inputFluid, ingredient, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SolidPyrolysisOvenRecipe recipe) {
            buffer.writeFluidStack(recipe.inputFluid);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}

