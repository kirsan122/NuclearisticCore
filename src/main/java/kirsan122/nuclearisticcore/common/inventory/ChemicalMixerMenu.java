package kirsan122.nuclearisticcore.common.inventory;

import kirsan122.nuclearisticcore.common.blockEntity.machines.ChemicalMixerBlockEntity;
import kirsan122.nuclearisticcore.common.blockEntity.machines.FluidCoolerBlockEntity;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.init.NCMenues;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ChemicalMixerMenu extends UniversalMenu<ChemicalMixerBlockEntity> implements IFluidScreenRender<ChemicalMixerBlockEntity> {

    public ChemicalMixerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }

    public ChemicalMixerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(NCMenues.CHEMICAL_MIXER_MENU.get(), containerId, (ChemicalMixerBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 32, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 1, 50, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 2, 122, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 32, 72));

        this.addSlot(new SlotItemHandler(handler, 4, 122, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(handler, 5, 156, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }
        });

        layoutPlayerInventory(inv, 14, 98, this::addSlot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, NCMachineBlocks.CHEMICAL_MIXER.get());
    }
}