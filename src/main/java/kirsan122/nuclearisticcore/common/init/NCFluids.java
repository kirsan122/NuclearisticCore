package kirsan122.nuclearisticcore.common.init;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.common.liquid.FluidObject;
import kirsan122.nuclearisticcore.common.liquid.FluidProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

import static kirsan122.nuclearisticcore.NuclearisticCore.MOD_ID;
public class NCFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID);
    public static final ArrayList<FluidObject> ALL_NC_FLUIDS = new ArrayList<>();

    public static final FluidObject LATEX = register("latex", new FluidProperties()
            .fallDistanceModifier(0.0f)
            .motionScale(0.008)
            .viscosity(1500)
            .slopeFindDistance(3)
            .tickRate(20)
            .fogDistance(2.0f, 7.0f)
            .hasBlock()
            .hasBucket()
            .setRenderType(FluidProperties.Render.CUTOUT));

    public static final FluidObject OIL = register("oil", new FluidProperties()
            .density(950)
            .viscosity(3000)
            .motionScale(0.01)
            .tickRate(15)
            .setColor("0xFF2A2A2A")
            .slopeFindDistance(3)
            .levelDecreasePerBlock(1)
            .fogDistance(0.0f, 1.5f)
            .setRenderType(FluidProperties.Render.CUTOUT)
            .setNewTexture(
                    new ResourceLocation(NuclearisticCore.MOD_ID, "liquid/oil_still"),
                    new ResourceLocation(NuclearisticCore.MOD_ID, "liquid/oil_flow"))
            .hasBlock()
            .hasBucket());

    public static final FluidObject MAZUT = register("mazut", new FluidProperties()
            .density(1100)
            .viscosity(8000)
            .motionScale(0.005)
            .tickRate(35)
            .fogDistance(0.0f, 0.9f)
            .canSwim(false)
            .setRenderType(FluidProperties.Render.CUTOUT)
            .setColor("0xFF1A1105")
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .temperature(360)
            .hasBlock()
            .hasBucket());

    public static final FluidObject DIESEL = register("diesel", new FluidProperties()
            .density(850)
            .viscosity(1200)
            .setColor("0xFFC2A661")
            .slopeFindDistance(3)
            .fogDistance(0.5f, 8.0f)
            .tickRate(7)
            .hasBlock()
            .hasBucket());

    public static final FluidObject GASOLINE = register("gasoline", new FluidProperties()
            .density(750)
            .viscosity(600)
            .motionScale(0.016)
            .setColor("0xFFF7E79C")
            .fogDistance(1.0f, 18.0f)
            .slopeFindDistance(5)
            .tickRate(3)
            .hasBlock()
            .hasBucket());

    public static final FluidObject PROPANE = register("propane", new FluidProperties()
            .density(-1000)
            .viscosity(10)
            .setColor("0xFFD6DCDB"));

    public static final FluidObject OXYGEN = register("oxygen", new FluidProperties()
            .density(-750)
            .viscosity(9)
            .setColor("0xFF8BD3DF"));

    public static final FluidObject HYDROGEN = register("hydrogen", new FluidProperties()
            .density(-40000)
            .viscosity(1)
            .setColor("0xFFC6C6C6"));

    public static final FluidObject NITROGEN = register("nitrogen", new FluidProperties()
            .density(-2500)
            .viscosity(8)
            .setColor("0xFF4F76CC"));

    public static final FluidObject LIQUEFIED_AIR = register("liquefied_air", new FluidProperties()
            .density(870)
            .viscosity(300)
            .temperature(-192)
            .setColor("0xDDFEFEFF"));

    public static final FluidObject AIR = register("air", new FluidProperties()
            .density(-100)
            .viscosity(10)
            .setColor("0xDDFDFDFF"));

    public static final FluidObject ASSOCIATED_PETROLEUM_GAS = register("associated_petroleum_gas", new FluidProperties()
            .density(-800)
            .viscosity(15)
            .setColor("0xDFE0DCD3"));

    public static final FluidObject NAPHTHA = register("naphtha", new FluidProperties()
            .density(730)
            .viscosity(600)
            .setColor("0xFFC58B3F")
            .setRenderType(FluidProperties.Render.CUTOUT)
            .fogDistance(1.0f, 15.0f)
            .slopeFindDistance(6)
            .tickRate(4)
            .hasBlock()
            .hasBucket());

    public static final FluidObject HEAVY_NAPHTHA = register("heavy_naphtha", new FluidProperties()
            .density(800)
            .viscosity(1100)
            .setColor("0xFF835817")
            .setRenderType(FluidProperties.Render.CUTOUT)
            .fogDistance(0.5f, 12.0f)
            .slopeFindDistance(6)
            .tickRate(3)
            .hasBlock()
            .hasBucket());

    public static final FluidObject KEROSENE = register("kerosene", new FluidProperties()
            .density(852)
            .viscosity(1805)
            .setColor("0xFF9BE3DE")
            .setRenderType(FluidProperties.Render.CUTOUT)
            .fogDistance(1.0f, 18.0f)
            .slopeFindDistance(6)
            .tickRate(4)
            .hasBlock()
            .hasBucket());

    public static final FluidObject METHANE = register("methane", new FluidProperties()
            .density(-600)
            .viscosity(20)
            .setColor("0xDED0F0FF"));

    public static final FluidObject ETHANE = register("ethane", new FluidProperties()
            .density(-700)
            .viscosity(30)
            .setColor("0xDEE6F5FF"));

    public static final FluidObject ETHYLENE = register("ethylene", new FluidProperties()
            .density(-700)
            .viscosity(31)
            .setColor("0xEAF0FFF0"));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    private static FluidObject register(String prefix, @org.jetbrains.annotations.NotNull FluidProperties properties) {
        FluidObject fluid = new FluidObject(prefix, properties);
        ALL_NC_FLUIDS.add(fluid);
        return fluid;
    }
}
