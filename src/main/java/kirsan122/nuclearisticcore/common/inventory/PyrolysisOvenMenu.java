package kirsan122.nuclearisticcore.common.inventory;

import kirsan122.nuclearisticcore.common.blockEntity.machines.PyrolysisOvenBlockEntity;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.init.NCMenus;
import kirsan122.nuclearisticcore.common.inventory.slot.ConditionalModeSlot;
import kirsan122.nuclearisticcore.common.util.NCTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:33
 */
public class PyrolysisOvenMenu extends UniversalMenu<PyrolysisOvenBlockEntity> {
    public PyrolysisOvenMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()) instanceof PyrolysisOvenBlockEntity tile
                        ? tile
                        : new PyrolysisOvenBlockEntity(extraData.readBlockPos(), inv.player.level().getBlockState(extraData.readBlockPos())),
                new SimpleContainerData(6)
        );    }

    public PyrolysisOvenMenu(int containerId, Inventory playerInv, PyrolysisOvenBlockEntity entity, ContainerData data) {
        super(NCMenus.PYROLYSIS_OVEN_MENU.get(), containerId, entity, data);

        Supplier<Boolean> isFluidMode = () -> this.data.get(4) == 0;
        Supplier<Boolean> isSolidMode = () -> this.data.get(4) == 1;

        IItemHandler itemHandler = this.blockEntity.getItemHandler();

        this.addSlot(new ConditionalModeSlot(itemHandler, 0, 53, 63, isFluidMode) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent() && super.mayPlace(stack);
            }
        });
        this.addSlot(new ConditionalModeSlot(itemHandler, 1, 107, 63, isFluidMode) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent() && super.mayPlace(stack);            }
        });
        this.addSlot(new ConditionalModeSlot(itemHandler, 2, 39, 21, isSolidMode) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent() && super.mayPlace(stack);            }
        });
        this.addSlot(new ConditionalModeSlot(itemHandler, 3, 39, 49, isSolidMode) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent() && super.mayPlace(stack);            }
        });
        this.addSlot(new ConditionalModeSlot(itemHandler, 4, 80, 35, isSolidMode));
        this.addSlot(new ConditionalModeSlot(itemHandler, 5, 118, 35, isSolidMode));
        this.addSlot(new SlotItemHandler(itemHandler, 6, 142, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent() && super.mayPlace(stack);            }
        });
        layoutPlayerInventory(playerInv, 8, 84, this::addSlot);
    }

    public boolean isSolidModeActive() {
        return this.data.get(4) == 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, NCMachineBlocks.PYROLYSIS_OVEN.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();

            
            
            int machineSlotsCount = this.upgradeSlotsCount + 7;

            if (index < machineSlotsCount) {
                
                if (!moveItemStackTo(stack, machineSlotsCount, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                

                
                if (stack.is(NCTags.Items.ALL_UPGRADES) && blockEntity.isUpgradable() && isUpgradeTabFullyOpen) {
                    if (moveItemStackTo(stack, 0, 4, false)) {
                        return handleStackMoveCompletion(slot, copy, stack, playerIn);
                    }
                }

                
                if (stack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (moveItemStackTo(stack, 4 + 6, 4 + 7, false)) {
                        return handleStackMoveCompletion(slot, copy, stack, playerIn);
                    }
                }

                
                if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (moveItemStackTo(stack, 4 + 0, 4 + 4, false)) {
                        return handleStackMoveCompletion(slot, copy, stack, playerIn);
                    }
                }

                
                
                if (this.isSolidModeActive()) {
                    if (moveItemStackTo(stack, 4 + 4, 4 + 5, false)) {
                        return handleStackMoveCompletion(slot, copy, stack, playerIn);
                    }
                }

                
                return ItemStack.EMPTY;
            }

            return handleStackMoveCompletion(slot, copy, stack, playerIn);
        }

        return copy;
    }

    private ItemStack handleStackMoveCompletion(Slot slot, ItemStack copy, ItemStack stack, Player playerIn) {
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(playerIn, stack);
        return copy;
    }

}
