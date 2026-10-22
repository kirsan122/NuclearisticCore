package kirsan122.nuclearisticcore.common.init;


import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.common.structures.HeveaFoliagePlaced;
import kirsan122.nuclearisticcore.common.structures.HeveaTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class NCPlacers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, NuclearisticCore.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<HeveaFoliagePlaced>> HEVEA_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("hevea_foliage_placer", () -> new FoliagePlacerType<>(HeveaFoliagePlaced.CODEC));

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, NuclearisticCore.MOD_ID);

    public static final RegistryObject<TrunkPlacerType<HeveaTrunkPlacer>> HEVEA_TRUNK_PLACER =
            TRUNK_PLACERS.register("hevea_trunk_placer", () -> new TrunkPlacerType<>(HeveaTrunkPlacer.CODEC));


    public static void register(IEventBus eventBus) {
        FOLIAGE_PLACERS.register(eventBus);
        TRUNK_PLACERS.register(eventBus);
    }
}

