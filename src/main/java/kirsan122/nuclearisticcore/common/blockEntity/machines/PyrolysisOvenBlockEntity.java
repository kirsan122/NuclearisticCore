package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.upgrades.IEnergyUpgrade;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.energy.DynamicEnergyStorage;
import kirsan122.nuclearisticcore.common.init.NCBlockEntities;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.inventory.PyrolysisOvenMenu;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.nuclearisticcore.common.recipe.FluidPyrolysisOvenRecipe;
import kirsan122.nuclearisticcore.common.recipe.SolidPyrolysisOvenRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:28
 */
public class PyrolysisOvenBlockEntity extends UniversalBlockEntity implements MenuProvider, IEnergyUpgrade {
    private int progress = 0;
    private int fluidProgress = 0;
    private int solidProgress = 0;
    private int maxProgress = 200;
    private int currentMode = 0;
    protected final ContainerData data;
    private int maxEnergyStorage = 22000;
    private final int energyPerTick = 40;

    private boolean inventoryChanged = true;

    private FluidPyrolysisOvenRecipe cachedFluidRecipe = null;
    private SolidPyrolysisOvenRecipe cachedSolidRecipe = null;

    private Fluid lastCheckedFluid0 = Fluids.EMPTY;
    private int lastCheckedAmount0 = 0;
    private Fluid lastCheckedFluid1 = Fluids.EMPTY;
    private int lastCheckedAmount1 = 0;

    private final FluidTank[] tanks = new FluidTank[3];

    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == 4) {
                inventoryChanged = true;
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 6) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            if (slot < 4) return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
            if (slot == 5) return false;
            return super.isItemValid(slot, stack);
        }
    };

    private final DynamicEnergyStorage energyStorage = new DynamicEnergyStorage(maxEnergyStorage, 1000, 1000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };

    private LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> createEnergyStorageWrapper(this.energyStorage));
    private LazyOptional<IFluidHandler>[] handlers = new LazyOptional[3];

    public PyrolysisOvenBlockEntity(BlockPos pos, BlockState state) {
        super(NCBlockEntities.PYROLYSIS_OVEN_BE.get(), pos, state, true, true, true);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PyrolysisOvenBlockEntity.this.fluidProgress;
                    case 1 -> PyrolysisOvenBlockEntity.this.maxProgress;
                    case 2 -> PyrolysisOvenBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> PyrolysisOvenBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> PyrolysisOvenBlockEntity.this.currentMode;
                    case 5 -> PyrolysisOvenBlockEntity.this.solidProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
        for (int i = 0; i < tanks.length; i++) {
            this.tanks[i] = new FluidTank(8000) {
                @Override
                protected void onContentsChanged() {
                    setChanged();
                    if (level != null && !level.isClientSide) {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    }
                }
            };
        }
        this.handlers[0] = LazyOptional.of(() -> this.tanks[0]);
        this.handlers[1] = LazyOptional.of(() -> this.tanks[1]);
        this.handlers[2] = LazyOptional.of(() -> this.tanks[2]);
    }

    public void toggleMode() {
        currentMode = currentMode == 0 ? 1 : 0;
        progress = 0;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public LazyOptional<IFluidHandler> getTankCapability(int tankIndex) {
        return handlers[tankIndex];
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nuclearisticcore.pyrolysis_oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new PyrolysisOvenMenu(containerId, playerInv, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PyrolysisOvenBlockEntity entity) {
        if (level.isClientSide) return;

        chargeFromEnergySlot(entity.energyStorage, entity.itemHandler, 6, entity);
        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = true;
        if (entity.isRedstoneConfigurable()) {
            assert entity.redstoneComp != null;
            canWork = entity.redstoneComp.getRedstoneMode().canWork(level.hasNeighborSignal(pos));
        }

        if (!canWork) {
            entity.decayFluidProgress();
            entity.decaySolidProgress();
            return;
        }

        entity.tickFluidMode(level, pos, state);
        entity.tickSolidMode(level, pos, state);

        entity.inventoryChanged = false;
    }

    private void decayFluidProgress() {
        if (this.fluidProgress > 0) {
            this.fluidProgress--;
            setChanged();
        }
    }

    private void decaySolidProgress() {
        if (this.solidProgress > 0) {
            this.solidProgress--;
            setChanged();
        }
    }


    private void tickFluidMode(Level level, BlockPos pos, BlockState state) {
        FluidTank inputTank = tanks[0];
        FluidTank outputTank = tanks[1];

        if (inputTank.isEmpty()) {
            decayFluidProgress();
            return;
        }

        FluidStack currentFluid = inputTank.getFluid();

        if (this.inventoryChanged || currentFluid.getFluid() != this.lastCheckedFluid0 || currentFluid.getAmount() < this.lastCheckedAmount0) {
            this.lastCheckedFluid0 = currentFluid.getFluid();
            this.lastCheckedAmount0 = currentFluid.getAmount();
            this.cachedFluidRecipe = level.getRecipeManager()
                    .getAllRecipesFor(NCRecipes.FLUID_PYROLYSIS_OVEN_TYPE.get())
                    .stream()
                    .filter(r -> r.input().isFluidEqual(currentFluid))
                    .findFirst()
                    .orElse(null);
        }

        FluidPyrolysisOvenRecipe recipe = (this.cachedFluidRecipe != null && inputTank.getFluidAmount() >= this.cachedFluidRecipe.input().getAmount()) ? this.cachedFluidRecipe : null;

        if (recipe != null && canOutputFluid(recipe.output(), outputTank)) {

            if (this.energyStorage.getEnergyStored() >= energyPerTick) {

                this.energyStorage.extractEnergy(energyPerTick, false);
                this.fluidProgress++;

                if (this.fluidProgress >= maxProgress) {
                    inputTank.drain(recipe.input().getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    outputTank.fill(recipe.output().copy(), IFluidHandler.FluidAction.EXECUTE);
                    this.fluidProgress = 0;
                }
                setChanged(level, pos, state);
            }
        } else {
            if (this.fluidProgress > 0) {
                this.fluidProgress--;
                setChanged(level, pos, state);
            }
        }
    }

    private void tickSolidMode(Level level, BlockPos pos, BlockState state) {

        ItemStack inputStack = itemHandler.getStackInSlot(4);
        FluidTank inputFluidTank = tanks[2];
        if (inputStack.isEmpty() || inputFluidTank.isEmpty()) {
            decaySolidProgress();
            return;
        }
        FluidStack currentFluid = inputFluidTank.getFluid();

        if (this.inventoryChanged || currentFluid.getFluid() != this.lastCheckedFluid1 || currentFluid.getAmount() < this.lastCheckedAmount1) {
            this.lastCheckedFluid1 = currentFluid.getFluid();
            this.lastCheckedAmount1 = currentFluid.getAmount();
            this.cachedSolidRecipe = level.getRecipeManager()
                    .getAllRecipesFor(NCRecipes.SOLID_PYROLYSIS_OVEN_TYPE.get())
                    .stream()
                    .filter(r -> r.ingredient().test(inputStack) && r.inputFluid().isFluidEqual(currentFluid))
                    .findFirst()
                    .orElse(null);
        }
        SolidPyrolysisOvenRecipe recipe = (this.cachedSolidRecipe != null
                && inputStack.getCount() >= this.cachedSolidRecipe.ingredient().getCount()
                && inputFluidTank.getFluidAmount() >= this.cachedSolidRecipe.inputFluid().getAmount()) ? this.cachedSolidRecipe : null;
        if (recipe != null && canOutputItemStack(recipe.result())) {

            if (this.energyStorage.getEnergyStored() >= energyPerTick) {
                this.energyStorage.extractEnergy(energyPerTick, false);
                this.solidProgress++;

                if (this.solidProgress >= maxProgress) {
                    itemHandler.extractItem(4, recipe.ingredient().getCount(), false);
                    inputFluidTank.drain(recipe.inputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

                    itemHandler.insertItem(5, recipe.result().copy(), false);
                    this.solidProgress = 0;
                }
                setChanged(level, pos, state);
            }
        } else {
            if (this.solidProgress > 0) {
                this.solidProgress--;
                setChanged(level, pos, state);
            }
        }
    }

    private boolean canOutputFluid(FluidStack recipeOutput, FluidTank targetTank) {
        if (recipeOutput.isEmpty()) return true;
        if (!targetTank.getFluid().isEmpty() && !targetTank.getFluid().isFluidEqual(recipeOutput)) return false;
        return targetTank.getFluidAmount() + recipeOutput.getAmount() <= targetTank.getCapacity();
    }

    private boolean canOutputItemStack(ItemStack recipeOutput) {
        ItemStack currentOutput = itemHandler.getStackInSlot(5);
        if (currentOutput.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(currentOutput, recipeOutput)) return false;
        return currentOutput.getCount() + recipeOutput.getCount() <= currentOutput.getMaxStackSize();
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergyStorage;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < tanks.length; i++) handlers[i].invalidate();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("fluidProgress", fluidProgress);
        tag.putInt("solidProgress", solidProgress);
        tag.put("inputTank", tanks[0].writeToNBT(new CompoundTag()));
        tag.put("outputTank", tanks[1].writeToNBT(new CompoundTag()));
        tag.put("inputTank2", tanks[2].writeToNBT(new CompoundTag()));
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("mode", currentMode);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidProgress = tag.getInt("fluidProgress");
        solidProgress = tag.getInt("solidProgress");
        tanks[0].readFromNBT(tag.getCompound("inputTank"));
        tanks[1].readFromNBT(tag.getCompound("outputTank"));
        tanks[2].readFromNBT(tag.getCompound("inputTank2"));

        energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));

        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(tag.get("energy"));
        }
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        currentMode = tag.getInt("mode");
        inventoryChanged = true;
    }

    @Override
    public void refreshCaps() {
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
        for (int i = 0; i < tanks.length; i++) handlers[i].invalidate();

        energyStorageLazyOptional = LazyOptional.of(() -> createEnergyStorageWrapper(energyStorage));

        if (currentMode == 0) {

            itemHandlerLazyOptional = LazyOptional.empty();

            handlers[0] = LazyOptional.of(() -> tanks[0]);
            handlers[1] = LazyOptional.of(() -> tanks[1]);
            handlers[2] = LazyOptional.empty();
        } else {

            itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);

            handlers[0] = LazyOptional.empty();
            handlers[1] = LazyOptional.empty();
            handlers[2] = LazyOptional.of(() -> tanks[2]);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiData.getRelativeSide(blockFacing, side);

        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null) {
                return energyStorageLazyOptional.cast();
            }
            int sideId = relativeSide.get3DDataValue();
            int sideMode = getDeviceSideMode(sideId);

            if (sideMode != 1) return LazyOptional.empty();

            return energyStorageLazyOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == null) {
                return LazyOptional.empty();
            }
            int sideId = relativeSide.get3DDataValue();
            int sideMode = getDeviceSideMode(sideId);

            if (currentMode == 0) {
                switch (sideMode) {
                    case 1 -> {
                        return handlers[0].cast();
                    }
                    case 2 -> {
                        return handlers[1].cast();
                    }
                }
            }

            if (currentMode == 1 && sideMode == 1) {
                return handlers[2].cast();
            }
            return LazyOptional.empty();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER && currentMode == 1) {
            if (side == null) {
                return itemHandlerLazyOptional.cast();
            }

            int sideId = relativeSide.get3DDataValue();
            int sideMode = getDeviceSideMode(sideId);

            if (sideMode == 1) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 4, 5)).cast();
            }

            if (sideMode == 2) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 5, 6) {
                    @Override
                    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                        return stack;
                    }
                }).cast();
            }
            return LazyOptional.empty();

        }

        return super.getCapability(cap, side);
    }
}
