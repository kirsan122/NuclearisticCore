package kirsan122.nuclearisticcore.common.init;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.common.block.FluidTankBlock;
import kirsan122.nuclearisticcore.common.block.machines.*;
import kirsan122.nuclearisticcore.common.block.machines.distillation_column.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class NCMachineBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NuclearisticCore.MOD_ID);

    public static final RegistryObject<Block> CRUSHER = register("crusher",
            () -> new CrusherBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> GENERATOR = register("generator",
            () -> new GeneratorBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> ELECTRIC_FURNACE = register("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> MECHANICAL_SINTERER = register("mechanical_sinterer",
            () -> new MechanicalSintererBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_BASE = register("distillation_column_base",
            () -> new DistillationColumnBaseBlock(BlockBehaviour.Properties.copy(CRUSHER.get()).strength(4.5F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_SECTION = register("distillation_column_section",
            () -> new DistillationComponentBlock(DistillationComponentBlockType.SECTION, BlockBehaviour.Properties.copy(DISTILLATION_COLUMN_BASE.get())));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_TOP = register("distillation_column_top",
            () -> new DistillationComponentBlock(DistillationComponentBlockType.TOP, BlockBehaviour.Properties.copy(DISTILLATION_COLUMN_BASE.get())));

    public static final RegistryObject<FluidTankBlock> FLUID_TANK = register("fluid_tank",
            () -> new FluidTankBlock(10000, BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS).strength(3.0F, 5.0F)));

    public static final RegistryObject<Block> ELECTROLYZER = register("electrolyzer",
            () -> new ElectrolyzerBlock(BlockBehaviour.Properties.copy(CRUSHER.get()).noOcclusion()));

    public static final RegistryObject<Block> AIR_COLLECTOR = register("air_collector",
            () -> new AirCollectorBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> FLUID_COOLER = register("fluid_cooler",
            () -> new FluidCoolerBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> CATALYTIC_REFORMER = register("catalytic_reformer",
            () -> new CatalyticReformerBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> CHEMICAL_MIXER = register("chemical_mixer",
            () -> new ChemicalMixerBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> PYROLYSIS_OVEN = register("pyrolysis_oven",
            () -> new PyrolysisOvenBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> supplier) {
        return BLOCKS.register(id, supplier);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
