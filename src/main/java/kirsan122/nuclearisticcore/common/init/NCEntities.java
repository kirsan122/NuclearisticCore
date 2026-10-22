package kirsan122.nuclearisticcore.common.init;


import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.common.entity.hevea.HeveaBoatEntity;
import kirsan122.nuclearisticcore.common.entity.hevea.HeveaChestBoatEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NCEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NuclearisticCore.MOD_ID);

    public static final RegistryObject<EntityType<HeveaBoatEntity>> HEVEA_BOAT = ENTITIES.register("hevea_boat",
            () -> EntityType.Builder.<HeveaBoatEntity>of(HeveaBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).build("hevea_boat"));

    public static final RegistryObject<EntityType<HeveaChestBoatEntity>> HEVEA_CHEST_BOAT = ENTITIES.register("hevea_chest_boat",
            () -> EntityType.Builder.<HeveaChestBoatEntity>of(HeveaChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).build("hevea_chest_boat"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}

