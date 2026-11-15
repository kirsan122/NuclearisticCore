package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.IConfigurableBE;
import kirsan122.nuclearisticcore.api.IRedstoneConfigurable;
import kirsan122.nuclearisticcore.api.IUpgradableBE;
import kirsan122.nuclearisticcore.api.RedstoneModes;
import kirsan122.nuclearisticcore.common.blockEntity.components.RedstoneComponent;
import kirsan122.nuclearisticcore.common.blockEntity.components.SideConfigComponent;
import kirsan122.nuclearisticcore.common.blockEntity.components.UpgradeComponent;
import kirsan122.nuclearisticcore.common.util.NCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by kirsan122,
 * Data: 22.10.2026,
 * Time: 19:32
 */
public abstract class UniversalBlockEntity extends BlockEntity implements IRedstoneConfigurable, IUpgradableBE, IConfigurableBE {
    @Nullable
    protected final RedstoneComponent redstoneComp;
    @Nullable
    protected final UpgradeComponent upgradeComp;
    @Nullable
    protected final SideConfigComponent sideConfigComp;

    public UniversalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean useRedstone, boolean useUpgrades, boolean useSides) {
        super(type, pos, state);
        this.redstoneComp = useRedstone ? new RedstoneComponent(this) : null;
        this.upgradeComp = useUpgrades ? new UpgradeComponent(this) : null;
        this.sideConfigComp = useSides ? new SideConfigComponent(this) : null;
    }

    @Override
    public boolean isRedstoneConfigurable() {
        return redstoneComp != null;
    }

    @Override
    public boolean isUpgradable() {
        return upgradeComp != null;
    }

    @Override
    public boolean isSideConfigurable() {
        return sideConfigComp != null;
    }

    @Override
    public RedstoneModes getRedstoneMode() {
        if (isRedstoneConfigurable()) {
            assert redstoneComp != null;
            return redstoneComp.getRedstoneMode();
        } else {
            return RedstoneModes.IGNORED;
        }
    }

    @Override
    public void setRedstoneMode(RedstoneModes mode) {
        if (isRedstoneConfigurable()) {
            assert redstoneComp != null;
            redstoneComp.setRedstoneMode(mode);
        }
    }

    @Override
    public ItemStackHandler getUpgradeSlots() {
        if (isUpgradable()) {
            assert upgradeComp != null;
            return upgradeComp.getUpgradeSlots();
        } else {
            return null;
        }
    }

    @Override
    public int getDeviceSideMode(int sideId) {
        if (isSideConfigurable()) {
            assert sideConfigComp != null;
            return sideConfigComp.getSideMode(sideId);
        } else {
            return 0;
        }
    }

    @Override
    public void setDeviceSideMode(int sideId, int mode) {
        if (isSideConfigurable()) {
            assert sideConfigComp != null;
            sideConfigComp.setSideMode(sideId, mode);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (isRedstoneConfigurable()) {
            assert redstoneComp != null;
            redstoneComp.save(tag);
        }
        if (isUpgradable()) {
            assert upgradeComp != null;
            upgradeComp.save(tag);
        }
        if (isSideConfigurable()) {
            assert sideConfigComp != null;
            sideConfigComp.save(tag);
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (isRedstoneConfigurable()) {
            assert redstoneComp != null;
            redstoneComp.load(tag);
        }
        if (isUpgradable()) {
            assert upgradeComp != null;
            upgradeComp.load(tag);
        }
        if (isSideConfigurable()) {
            assert sideConfigComp != null;
            sideConfigComp.load(tag);
        }
    }

    public boolean isItemInUpgradeHandlerValid(ItemStack stack) {
        return stack.is(NCTags.Items.ALL_UPGRADES);
    }

    protected IEnergyStorage createEnergyStorageWrapper(EnergyStorage energyStorage) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return energyStorage.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return energyStorage.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    public static void chargeFromEnergySlot(EnergyStorage storage, ItemStackHandler handler, int batterySlot, BlockEntity entity) {
        int spaceLeft = storage.getMaxEnergyStored() - storage.getEnergyStored();
        if (spaceLeft <= 0) return;

        ItemStack batteryStack = handler.getStackInSlot(batterySlot);
        if (batteryStack.isEmpty()) return;

        batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(batteryEnergy -> {
            if (!batteryEnergy.canExtract()) return;

            int maxTransfer = Math.min(spaceLeft, 1000);
            int extracted = batteryEnergy.extractEnergy(maxTransfer, true);

            if (extracted > 0) {
                int accepted = storage.receiveEnergy(extracted, false);

                if (accepted > 0) {
                    batteryEnergy.extractEnergy(accepted, false);
                    entity.setChanged();
                }
            }
        });
    }

    public static void drainItemToTank(IFluidHandler tank, ItemStackHandler itemHandler, int inputSlot, int outputSlot, BlockEntity entity) {
        ItemStack inputStack = itemHandler.getStackInSlot(inputSlot);
        if (inputStack.isEmpty()) return;

        ItemStack containerCopy = inputStack.copyWithCount(1);
        var simResult = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);

        if (simResult.isSuccess()) {
            ItemStack emptyContainer = simResult.getResult();

            boolean canInsert;

            if (inputSlot == outputSlot) {
                canInsert = inputStack.getCount() == 1;
            } else {
                canInsert = itemHandler.insertItem(outputSlot, emptyContainer, true).isEmpty();
            }

            if (canInsert) {

                var realResult = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, true);
                if (realResult.isSuccess()) {

                    itemHandler.extractItem(inputSlot, 1, false);

                    itemHandler.insertItem(outputSlot, realResult.getResult(), false);
                    entity.setChanged();
                }
            }
        }
    }

    public static void fillItemFromTank(IFluidHandler tank, ItemStackHandler itemHandler, int inputSlot, int outputSlot, BlockEntity entity) {
        ItemStack inputStack = itemHandler.getStackInSlot(inputSlot);
        if (inputStack.isEmpty() || tank.getFluidInTank(0).isEmpty()) return;

        ItemStack containerCopy = inputStack.copyWithCount(1);
        var simResult = FluidUtil.tryFillContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);

        if (simResult.isSuccess()) {
            ItemStack filledContainer = simResult.getResult();
            boolean canInsert = false;

            if (inputSlot == outputSlot) {
                canInsert = inputStack.getCount() == 1;
            } else {
                canInsert = itemHandler.insertItem(outputSlot, filledContainer, true).isEmpty();
            }

            if (canInsert) {

                var realResult = FluidUtil.tryFillContainer(containerCopy, tank, Integer.MAX_VALUE, null, true);
                if (realResult.isSuccess()) {

                    itemHandler.extractItem(inputSlot, 1, false);

                    itemHandler.insertItem(outputSlot, realResult.getResult(), false);
                    entity.setChanged();
                }
            }
        }
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
