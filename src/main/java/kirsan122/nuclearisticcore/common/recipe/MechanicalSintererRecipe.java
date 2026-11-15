package kirsan122.nuclearisticcore.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.inventory.SizedIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record MechanicalSintererRecipe(ResourceLocation id, SizedIngredient baseInput, SizedIngredient infusorInput, ItemStack result) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) return false;

        return baseInput.test(container.getItem(0)) && infusorInput.test(container.getItem(1));
    }
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) { return result; }
    @Override
    public ResourceLocation getId() { return id; }
    @Override
    public RecipeSerializer<?> getSerializer() { return NCRecipes.MECHANICAL_SINTERER_SERIALIZER.get(); }
    @Override
    public RecipeType<?> getType() { return NCRecipes.MECHANICAL_SINTERER_TYPE.get(); }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> vanillaList = NonNullList.create();

        vanillaList.add(this.baseInput.getIngredient());
        vanillaList.add(this.infusorInput.getIngredient());
        return vanillaList;
    }

    public static class Serializer implements RecipeSerializer<MechanicalSintererRecipe> {
        @Override
        public MechanicalSintererRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            SizedIngredient baseInput = SizedIngredient.fromJson(GsonHelper.getAsJsonObject(json, "base"));
            SizedIngredient infusorInput = SizedIngredient.fromJson(GsonHelper.getAsJsonObject(json, "infusor"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new MechanicalSintererRecipe(recipeId, baseInput, infusorInput, output);
        }

        @Override
        public @Nullable MechanicalSintererRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            SizedIngredient baseInput = SizedIngredient.fromNetwork(buffer);
            SizedIngredient infusorInput = SizedIngredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new MechanicalSintererRecipe(recipeId, baseInput, infusorInput, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MechanicalSintererRecipe recipe) {
            recipe.baseInput.toNetwork(buffer);
            recipe.infusorInput.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
