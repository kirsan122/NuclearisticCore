package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.IConfigurableBE;
import kirsan122.nuclearisticcore.api.IRedstoneConfigurable;
import kirsan122.nuclearisticcore.api.IUpgradableBE;
import kirsan122.nuclearisticcore.api.RedstoneModes;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Created by kirsan122,
 * Data: 22.10.2026,
 * Time: 19:32
 */
public abstract class UniversalBlockEntity extends BlockEntity implements IRedstoneConfigurable, IUpgradableBE, IConfigurableBE {
    protected RedstoneModes redstoneMode = RedstoneModes.IGNORED;

    protected final ItemStackHandler upgradeSlots = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isItemInUpgradeHandlerValid(stack);
        }
    };

    protected final int[] sideConfigModes = new int[6];

    public UniversalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    public static void drainItemToTank(IFluidHandler tank, ItemStackHandler itemHandler, int inputSlot, int outputSlot, BlockEntity blockEntity) {
        ItemStack inputStack = itemHandler.getStackInSlot(inputSlot);
        if (inputStack.isEmpty()) return;

        ItemStack containerCopy = inputStack.copyWithCount(1);
        var simResult = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);

        if (simResult.isSuccess()) {
            ItemStack emptyContainer = simResult.getResult();

            boolean canInsert = false;

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
                    blockEntity.setChanged();
                }
            }
        }
    }

    public static void fillItemFromTank(IFluidHandler tank, ItemStackHandler itemHandler, int inputSlot, int outputSlot, BlockEntity blockEntity) {
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
                    blockEntity.setChanged();
                }
            }
        }
    }

    @Override
    public RedstoneModes getRedstoneMode() {
        return this.redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneModes mode) {
        this.redstoneMode = mode;
    }

    @Override
    public ItemStackHandler getUpgradeSlots() {
        return this.upgradeSlots;
    }

    @Override
    public int getDeviceSideMode(int sideId) {
        if (sideId < 0 || sideId >= 6) return 0;
        return this.sideConfigModes[sideId];
    }

    @Override
    public void setDeviceSideMode(int sideId, int mode) {
        if (sideId >= 0 && sideId < 6) {
            this.sideConfigModes[sideId] = mode;
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("upgrade_slots", upgradeSlots.serializeNBT());
        tag.putString("RedstoneMode", this.redstoneMode.getSerializedName());
        tag.putIntArray("SideConfigs", this.sideConfigModes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("RedstoneMode")) {
            this.redstoneMode = RedstoneModes.valueOf(tag.getString("RedstoneMode").toUpperCase());
        }
        if (tag.contains("SideConfigs", net.minecraft.nbt.Tag.TAG_INT_ARRAY)) {
            int[] savedModes = tag.getIntArray("SideConfigs");
            if (savedModes.length == 6) {
                System.arraycopy(savedModes, 0, this.sideConfigModes, 0, 6);
            }
        }
        upgradeSlots.deserializeNBT(tag.getCompound("upgrade_slots"));
    }
}
