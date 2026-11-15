package kirsan122.nuclearisticcore.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsan122,
 * Data: 28.10.2026,
 * Time: 20:56
 */
public final class RecipeUtils {

    /**
     * Parses a fixed-size array of {@link FluidStack} elements from a JSON object.
     * <p>
     * This helper method loops through the specified JSON object to find nested fluid entries
     * formatted with an index-based suffix (e.g., {@code prefix + 0}, {@code prefix + 1}).
     * </p>
     *
     * @param json         the root {@link JsonObject} of the recipe.
     * @param howManyTanks the total number of tanks (the precise length of the returned array).
     * @param parentKey    the JSON key of the parent object containing fluids (e.g., "inputFluids").
     * @param prefix       the prefix for indexed sub-objects (e.g., "input_" becomes "input_0").
     * @return a non-null array of {@link FluidStack} objects.
     * @throws JsonSyntaxException if the fluid specified in the JSON is missing, misspelled,
     *                             or not registered in the game registry.
     * @since Alpha v0.0.5
     */
    public static FluidStack[] parseFluidArray(JsonObject json, int howManyTanks, String parentKey, String prefix) {
        FluidStack[] fluids = new FluidStack[howManyTanks];
        JsonObject inputFluidsJson = GsonHelper.getAsJsonObject(json, parentKey);
        for (int i = 0; i < fluids.length; i++) {
            if (inputFluidsJson.has(prefix + i)) {
                JsonObject object = GsonHelper.getAsJsonObject(inputFluidsJson, prefix + i);
                String fluidName = GsonHelper.getAsString(object, "fluid");

                var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

                if (fluid != null) {
                    if (object.has("amount")) {
                        fluids[i] = new FluidStack(fluid, GsonHelper.getAsInt(object, "amount"));
                    } else {
                        fluids[i] = new FluidStack(fluid, 100);
                    }
                } else {
                    throw new JsonSyntaxException("Unknown or unregistered fluid '" + fluidName + "' in recipe object: " + prefix + i);
                }
            } else {
                fluids[i] = FluidStack.EMPTY;
            }
        }
        return fluids;
    }

    /**
     * Parses a single {@link FluidStack} from the specified JSON object.
     * <p>
     * This method reads the fluid registry name and the required amount directly
     * from the given JSON element. If the fluid cannot be found in the registry,
     * it throws a syntax exception to halt recipe loading.
     * </p>
     *
     * @param json the {@link JsonObject} representing the fluid entry (must contain "fluid" and "amount").
     * @param key  the JSON key of the fluid object (e.g., "fluid_input").
     * @return a non-null {@link FluidStack} with the parsed fluid and amount.
     * @throws JsonSyntaxException if the fluid specified in the JSON is missing, misspelled,
     *                             or not registered in the game registry.
     * @since Alpha v0.0.5
     */
    public static FluidStack parseFluid(JsonObject json, String key) {
        if (!json.has(key)) {
            return FluidStack.EMPTY;
        }

        JsonObject object = GsonHelper.getAsJsonObject(json, key);
        String fluidName = GsonHelper.getAsString(object, "fluid");

        var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

        if (fluid == null) {
            throw new com.google.gson.JsonSyntaxException("Unknown or unregistered fluid '" + fluidName + "' at key: " + key);
        }
        if (!object.has("amount")) {
            return new FluidStack(fluid, 100);
        }
        int amount = GsonHelper.getAsInt(object, "amount");
        return new FluidStack(fluid, amount);
    }

    /**
     * Writes an array of {@link FluidStack} to the network buffer.
     *
     * @param buffer the network {@link FriendlyByteBuf} to write to.
     * @param fluids the array of fluids to serialize.
     * @since Alpha v0.0.5
     */
    public static void writeFluidArray(FriendlyByteBuf buffer, FluidStack[] fluids) {

        buffer.writeVarInt(fluids.length);
        for (FluidStack fluid : fluids) {
            buffer.writeFluidStack(fluid);
        }
    }

    /**
     * Reads an array of {@link FluidStack} from the network buffer.
     *
     * @param buffer the network {@link FriendlyByteBuf} to read from.
     * @return a non-null array of parsed {@link FluidStack} objects.
     * @since Alpha v0.0.5
     */
    public static FluidStack[] readFluidArray(FriendlyByteBuf buffer) {
        int length = buffer.readVarInt();
        FluidStack[] fluids = new FluidStack[length];
        for (int i = 0; i < length; i++) {
            fluids[i] = buffer.readFluidStack();
        }
        return fluids;
    }
}
