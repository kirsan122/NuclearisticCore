package kirsan122.nuclearisticcore.common.item;

import kirsan122.nuclearisticcore.common.blockEntity.machines.UniversalBlockEntity;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirsan122,
 * Data: 02.11.2026,
 * Time: 20:03
 */
public class MachineBlockItem extends BlockItem {
    private final String ctrlDescription;
    private final int baseMaxEnergy;
    private final int howManyTanks;
    private final boolean hasFluids; 

    public MachineBlockItem(Block block, String ctrlDescription, int baseMaxEnergy, Properties properties) {
        this(block, ctrlDescription, baseMaxEnergy, 0, false, properties);
    }

    public MachineBlockItem(Block block, String ctrlDescription, int baseMaxEnergy, int howManyTanks, Properties properties) {
        this(block, ctrlDescription, baseMaxEnergy, howManyTanks, true, properties);
    }

    private MachineBlockItem(Block block, String ctrlDescription, int baseMaxEnergy, int howManyTanks, boolean hasFluids, Properties properties) {
        super(block, properties);
        this.ctrlDescription = ctrlDescription;
        this.baseMaxEnergy = baseMaxEnergy;
        this.howManyTanks = howManyTanks;
        this.hasFluids = hasFluids;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasControlDown()) {
            tooltip.add(Component.translatable(ctrlDescription));
        } else if (Screen.hasShiftDown()) {
            int currentEnergy = 0;
            int maxEnergy = baseMaxEnergy;
            String inventorySummary = "tooltip.nuclearisticcore.machines.inv.empty";

            CompoundTag mainTag = stack.getTag();
            if (mainTag != null && mainTag.contains("BlockEntityTag")) {
                CompoundTag beTag = mainTag.getCompound("BlockEntityTag");

                
                if (beTag.contains("energy")) {
                    CompoundTag energyTag = beTag.getCompound("energy");
                    if (energyTag.contains("energy")) {
                        currentEnergy = energyTag.getInt("energy");
                    }
                }

                
                CompoundTag upgradeSlotsTag = beTag.contains("upgrade_slots") ? beTag.getCompound("upgrade_slots") : null;
                maxEnergy = EnergyUpgradeItem.getMaxEnergy(this.baseMaxEnergy, upgradeSlotsTag);

                
                if (beTag.contains("inventory")) {
                    CompoundTag invTag = beTag.getCompound("inventory");
                    if (invTag.contains("Items", Tag.TAG_LIST)) {
                        ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);
                        List<String> foundItems = new ArrayList<>();

                        for (int i = 0; i < itemsList.size(); i++) {
                            CompoundTag itemTag = itemsList.getCompound(i);
                            ItemStack itemInSlot = ItemStack.of(itemTag);
                            if (!itemInSlot.isEmpty()) {
                                foundItems.add(itemInSlot.getHoverName().getString() + " x" + itemInSlot.getCount());
                            }
                        }
                        if (!foundItems.isEmpty()) {
                            inventorySummary = String.join(", ", foundItems);
                        }
                    }
                }
            }

            
            tooltip.add(Component.translatable("tooltip.nuclearisticcore.battery_info.energy", currentEnergy, maxEnergy));
            if (inventorySummary.equals("tooltip.nuclearisticcore.machines.inv.empty")) {
                tooltip.add(Component.translatable("tooltip.nuclearisticcore.machines.info.inv").append(Component.translatable(inventorySummary)));
            } else {
                tooltip.add(Component.translatable("tooltip.nuclearisticcore.machines.info.inv").append(Component.literal(inventorySummary)));
            }

            
            if (this.hasFluids && mainTag != null && mainTag.contains("BlockEntityTag")) {
                CompoundTag beTag = mainTag.getCompound("BlockEntityTag");
                List<Component> fluidTooltips = new ArrayList<>();

                checkAndAddFluid(beTag, "inputTank", fluidTooltips, ChatFormatting.AQUA);
                checkAndAddFluid(beTag, "Tank", fluidTooltips, ChatFormatting.AQUA);
                checkAndAddFluid(beTag, "outputTank", fluidTooltips, ChatFormatting.DARK_AQUA);

                for (int i = 0; i < this.howManyTanks; i++) {
                    checkAndAddFluid(beTag, "inputTank" + i, fluidTooltips, ChatFormatting.AQUA);
                    checkAndAddFluid(beTag, "outputTank" + i, fluidTooltips, ChatFormatting.DARK_AQUA);
                }

                if (!fluidTooltips.isEmpty()) {
                    tooltip.addAll(fluidTooltips);
                }
            }

        } else {
            tooltip.add(Component.translatable("tooltip.nuclearisticcore.universal.shift"));
            tooltip.add(Component.translatable("tooltip.nuclearisticcore.universal.ctrl"));
        }
    }

    private static void checkAndAddFluid(CompoundTag beTag, String key, List<Component> fluidTooltips, ChatFormatting color) {
        if (beTag.contains(key, Tag.TAG_COMPOUND)) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(beTag.getCompound(key));
            if (!fluid.isEmpty()) {
                fluidTooltips.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY)
                        .append(fluid.getDisplayName().copy().withStyle(color))
                        .append(Component.literal(": " + fluid.getAmount() + " mB").withStyle(ChatFormatting.WHITE)));
            }
        }
    }
}
