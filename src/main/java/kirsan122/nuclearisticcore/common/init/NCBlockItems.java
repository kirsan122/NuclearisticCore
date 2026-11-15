package kirsan122.nuclearisticcore.common.init;

import kirsan122.nuclearisticcore.common.blockEntity.machines.UniversalBlockEntity;
import kirsan122.nuclearisticcore.common.item.FluidTankBlockItem;
import kirsan122.nuclearisticcore.common.item.MachineBlockItem;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static kirsan122.nuclearisticcore.NuclearisticCore.MOD_ID;

public class NCBlockItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> TIN_ORE_ITEM = register(NCBlocks.TIN_ORE);

    public static final RegistryObject<Item> DEEPSLATE_TIN_ORE_ITEM = register(NCBlocks.DEEPSLATE_TIN_ORE);

    public static final RegistryObject<Item> RAW_TIN_BLOCK_ITEM = register(NCBlocks.RAW_TIN_BLOCK);

    public static final RegistryObject<Item> TIN_BLOCK_ITEM = register(NCBlocks.TIN_BLOCK);

    public static final RegistryObject<Item> CRUSHER_ITEM = register("crusher",
            () -> new MachineBlockItem(NCMachineBlocks.CRUSHER.get(),
                    "tooltip.nuclearisticcore.machines.crusher", 8500, new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = register(NCBlocks.MACHINE_FRAME);

    public static final RegistryObject<Item> GENERATOR_ITEM = register("generator",
            () -> new MachineBlockItem(NCMachineBlocks.GENERATOR.get(),
                    "tooltip.nuclearisticcore.machines.generator", 5000,  new Item.Properties()));

    public static final RegistryObject<Item> ELECTRIC_FURNACE_ITEM = register("electric_furnace",
            () -> new MachineBlockItem(NCMachineBlocks.ELECTRIC_FURNACE.get(),
                    "tooltip.nuclearisticcore.machines.electric_furnace", 8500, new Item.Properties()));

    public static final RegistryObject<Item> CLAY_BOWL_ITEM = register(
            "clay_bowl", () -> new BlockItem(NCBlocks.CLAY_BOWL.get(), new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

                    if (stack.hasTag() && stack.getTag() != null) {
                        CompoundTag rootTag = stack.getTag();


                        if (rootTag.contains("BlockEntityTag", 10)) {
                            CompoundTag blockEntityTag = rootTag.getCompound("BlockEntityTag");


                            if (blockEntityTag.contains("Amount")) {
                                int amount = blockEntityTag.getInt("Amount");

                                if (amount > 0) {
                                    tooltip.add(Component.translatable("tooltip.nuclearisticcore.latex_amount")
                                            .append(Integer.toString(amount))
                                            .append(Component.literal(" / 1000 mB"))
                                            .withStyle(ChatFormatting.GRAY));
                                    return;
                                }
                            }
                        }
                    }

                    tooltip.add(Component.translatable("tooltip.nuclearisticcore.empty").withStyle(ChatFormatting.GRAY));
                }

            }
    );
    public static final RegistryObject<Item> MECHANICAL_SINTERER_ITEM = register("mechanical_sinterer",
            () -> new MachineBlockItem(NCMachineBlocks.MECHANICAL_SINTERER.get(),
                    "tooltip.nuclearisticcore.machines.mechanical_sinterer", 8500, new Item.Properties()));

    public static final RegistryObject<Item> ELECTROLYZER_ITEM = register("electrolyzer",
            () -> new MachineBlockItem(NCMachineBlocks.ELECTROLYZER.get(),
                    "tooltip.nuclearisticcore.machines.electrolyzer", 12000, new Item.Properties()));

    public static final RegistryObject<Item> AIR_COLLECTOR_ITEM = register("air_collector",
            () -> new MachineBlockItem(NCMachineBlocks.AIR_COLLECTOR.get(),
                    "tooltip.nuclearisticcore.machines.air_collector", 16000, new Item.Properties()));

    public static final RegistryObject<Item> FLUID_COOLER_ITEM = register("fluid_cooler",
            () -> new MachineBlockItem(NCMachineBlocks.FLUID_COOLER.get(),
                    "tooltip.nuclearisticcore.machines.fluid_cooler", 16000, new Item.Properties()));

    public static final RegistryObject<Item> CATALYTIC_REFORMER_ITEM = register("catalytic_reformer",
            () -> new MachineBlockItem(NCMachineBlocks.CATALYTIC_REFORMER.get(),
                    "tooltip.nuclearisticcore.machines.catalytic_reformer", 12000, new Item.Properties()));

    public static final RegistryObject<Item> FLUID_TANK_ITEM = register("fluid_tank",
            () -> new FluidTankBlockItem(NCMachineBlocks.FLUID_TANK.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHEMICAL_MIXER_ITEM = register("chemical_mixer",
            () -> new MachineBlockItem(NCMachineBlocks.CHEMICAL_MIXER.get(),
                    "tooltip.nuclearisticcore.machines.chemical_mixer", 12000, new Item.Properties()));

    public static final RegistryObject<Item> PYROLYSIS_OVEN_ITEM = register("pyrolysis_oven",
            () -> new MachineBlockItem(NCMachineBlocks.PYROLYSIS_OVEN.get(),
                    "tooltip.nuclearisticcore.machines.pyrolysis_oven", 22000, new Item.Properties()));

    public static final RegistryObject<Item> COPPER_CABLE_ITEM = register(NCBlocks.COPPER_CABLE);
    public static final RegistryObject<Item> INSULATED_COPPER_CABLE = register(NCBlocks.INSULATED_COPPER_CABLE);
    public static final RegistryObject<Item> HEVEA_LOG_ITEM = register(NCBlocks.HEVEA_LOG);
    public static final RegistryObject<Item> HEVEA_PLANKS_ITEM = register(NCBlocks.HEVEA_PLANKS);
    public static final RegistryObject<Item> HEVEA_FENCE_ITEM = register(NCBlocks.HEVEA_FENCE);
    public static final RegistryObject<Item> HEVEA_STAIRS_ITEM = register(NCBlocks.HEVEA_STAIRS);
    public static final RegistryObject<Item> HEVEA_SLAB_ITEM = register(NCBlocks.HEVEA_SLAB);
    public static final RegistryObject<Item> HEVEA_DOOR_ITEM = register("hevea_door", () -> new BlockItem(NCBlocks.HEVEA_DOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_TRAPDOOR_ITEM = register("hevea_trapdoor", () -> new BlockItem(NCBlocks.HEVEA_TRAPDOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_FENCE_GATE_ITEM = register("hevea_fence_gate", () -> new BlockItem(NCBlocks.HEVEA_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_BUTTON_ITEM = register("hevea_button", () -> new BlockItem(NCBlocks.HEVEA_BUTTON.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_PRESSURE_PLATE_ITEM = register("hevea_pressure_plate", () -> new BlockItem(NCBlocks.HEVEA_PRESSURE_PLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_SIGN_ITEM = register("hevea_sign", () -> new SignItem(new Item.Properties().stacksTo(16), NCBlocks.HEVEA_SIGN.get(), NCBlocks.HEVEA_WALL_SIGN.get()));
    public static final RegistryObject<Item> HEVEA_HANGING_SIGN_ITEM = register("hevea_hanging_sign", () -> new HangingSignItem(NCBlocks.HEVEA_HANGING_SIGN.get(), NCBlocks.HEVEA_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HEVEA_WOOD_ITEM= register(NCBlocks.HEVEA_WOOD);
    public static final RegistryObject<Item> STRIPPED_HEVEA_LOG_ITEM= register(NCBlocks.STRIPPED_HEVEA_LOG);
    public static final RegistryObject<Item> STRIPPED_HEVEA_WOOD_ITEM= register(NCBlocks.STRIPPED_HEVEA_WOOD);
    public static final RegistryObject<Item> HEVEA_LEAVES_ITEM = register("hevea_leaves", () -> new BlockItem(NCBlocks.HEVEA_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_SAPLING_ITEM = register("hevea_sapling", () -> new BlockItem(NCBlocks.HEVEA_SAPLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> LEAD_ORE_ITEM = register(NCBlocks.LEAD_ORE);
    public static final RegistryObject<Item> DEEPSLATE_LEAD_ORE_ITEM = register(NCBlocks.DEEPSLATE_LEAD_ORE);
    public static final RegistryObject<Item> RAW_LEAD_BLOCK_ITEM = register(NCBlocks.RAW_LEAD_BLOCK);
    public static final RegistryObject<Item> LEAD_BLOCK_ITEM = register(NCBlocks.LEAD_BLOCK);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_BASE_ITEM = register(NCMachineBlocks.DISTILLATION_COLUMN_BASE);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_SECTION_ITEM = register(NCMachineBlocks.DISTILLATION_COLUMN_SECTION);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_TOP_ITEM = register(NCMachineBlocks.DISTILLATION_COLUMN_TOP);
    public static final RegistryObject<Item> DEEPSLATE_URANIUM_ORE_ITEM = register("deepslate_uranium_ore", () -> new BlockItem(NCBlocks.DEEPSLATE_URANIUM_ORE.get(), new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> ADVANCED_MACHINE_FRAME_ITEM = register(NCBlocks.ADVANCED_MACHINE_FRAME);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    private static RegistryObject<Item> register(String id, Supplier<Item> itemSupplier) {return ITEMS.register(id, itemSupplier);}
    private static RegistryObject<Item> register(RegistryObject<Block> block) {return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));}
}
