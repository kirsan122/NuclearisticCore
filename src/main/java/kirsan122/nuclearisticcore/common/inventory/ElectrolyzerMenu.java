package kirsan122.nuclearisticcore.common.inventory;

import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.blockEntity.machines.ElectrolyzerBlockEntity;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.init.NCMenues;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerMenu extends UniversalMenu<ElectrolyzerBlockEntity> implements IFluidScreenRender<ElectrolyzerBlockEntity> {
    public ElectrolyzerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    public ElectrolyzerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(NCMenues.ELECTROLYZER_MENU.get(), containerId, (ElectrolyzerBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 8, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });
        this.addSlot(new SlotItemHandler(handler, 1, 80, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });
        this.addSlot(new SlotItemHandler(handler, 2, 116, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });
        this.addSlot(new SlotItemHandler(handler, 3, 152, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();}
        });

        layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, NCMachineBlocks.ELECTROLYZER.get());
    }
}
