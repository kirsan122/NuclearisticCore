package kirsan122.nuclearisticcore.common.inventory;

import kirsan122.nuclearisticcore.common.blockEntity.machines.MechanicalSintererBlockEntity;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.init.NCMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MechanicalSintererMenu extends UniversalMenu<MechanicalSintererBlockEntity> {
    public MechanicalSintererMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()) instanceof MechanicalSintererBlockEntity tile
                        ? tile
                        : new MechanicalSintererBlockEntity(extraData.readBlockPos(), inv.player.level().getBlockState(extraData.readBlockPos())),
                new SimpleContainerData(4)
        );
    }

    public MechanicalSintererMenu(int containerId, Inventory inv, MechanicalSintererBlockEntity entity, ContainerData data) {
        super(NCMenus.MECHANICAL_SINTERER_MENU.get(), containerId, entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 53, 35));

        this.addSlot(new SlotItemHandler(handler, 1, 32, 35));

        this.addSlot(new SlotItemHandler(handler, 2, 113, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 143, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getCapability(ForgeCapabilities.ENERGY).isPresent(); }
        });

        layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, NCMachineBlocks.MECHANICAL_SINTERER.get());
    }
}
