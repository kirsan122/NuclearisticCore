package kirsan122.nuclearisticcore.common.inventory;

import kirsan122.nuclearisticcore.common.blockEntity.machines.CatalyticReformerBlockEntity;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.init.NCMenus;
import kirsan122.nuclearisticcore.common.util.NCTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created by kirsan122,
 * Data: 24.10.2026,
 * Time: 13:43
 */
public class CatalyticReformerMenu extends UniversalMenu<CatalyticReformerBlockEntity> implements IFluidScreenRender<CatalyticReformerBlockEntity> {
    public CatalyticReformerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()) instanceof CatalyticReformerBlockEntity tile
                        ? tile
                        : new CatalyticReformerBlockEntity(extraData.readBlockPos(), inv.player.level().getBlockState(extraData.readBlockPos())),
                new SimpleContainerData(4)
        );    }

    public CatalyticReformerMenu(int containerId, Inventory inv, CatalyticReformerBlockEntity entity, ContainerData data) {
        super(NCMenus.CATALYTIC_REFORMER_MENU.get(), containerId, entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 68, 41) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(NCTags.Items.CATALYTIC_REFORMER_CATALYSTS);
            }
        });
        this.addSlot(new SlotItemHandler(handler, 1, 36, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 98, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 125, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 4, 152, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }
        });

        layoutPlayerInventory(inv, 8, 92, this::addSlot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, NCMachineBlocks.CATALYTIC_REFORMER.get());
    }
}
