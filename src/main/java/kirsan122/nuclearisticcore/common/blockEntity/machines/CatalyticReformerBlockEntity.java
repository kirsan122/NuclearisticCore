package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.RedstoneModes;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.energy.DynamicEnergyStorage;
import kirsan122.nuclearisticcore.common.init.NCBlockEntities;
import kirsan122.nuclearisticcore.common.init.NCRecipes;
import kirsan122.nuclearisticcore.common.inventory.CatalyticReformerMenu;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.nuclearisticcore.common.recipe.CatalyticReformerRecipe;
import kirsan122.nuclearisticcore.common.util.NCTags;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kirsan122,
 * Data: 23.10.2026,
 * Time: 18:16
 */
public class CatalyticReformerBlockEntity extends UniversalBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 22000;
    private final int maxCapacity = 18000;
    protected final ContainerData data;
    public boolean inventoryChanged = true;
    private CatalyticReformerRecipe cachedRecipe = null;
    private Fluid lastCheckedFluid = Fluids.EMPTY;
    private int lastCheckedAmount = 0;
    private int lastCheckedCatalystTier = 0;

    private final FluidTank[] tanks = new FluidTank[3];
    private final LazyOptional<IFluidHandler>[] handlers = new LazyOptional[3];

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == 0) {
                inventoryChanged = true;
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 4) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            if (slot == 0) return stack.is(NCTags.Items.CATALYTIC_REFORMER_CATALYSTS);
            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
        }
    };


    public CatalyticReformerBlockEntity(BlockPos pos, BlockState state) {
        super(NCBlockEntities.CATALYTIC_REFORMER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 2 -> CatalyticReformerBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> CatalyticReformerBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> CatalyticReformerBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> CatalyticReformerBlockEntity.this.sideConfigModes[index - 5];
                    default -> 2;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 4 ->
                            CatalyticReformerBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        CatalyticReformerBlockEntity.this.sideConfigModes[index - 5] = value;
                        CatalyticReformerBlockEntity.this.setChanged();

                        if (CatalyticReformerBlockEntity.this.level != null && !CatalyticReformerBlockEntity.this.level.isClientSide) {
                            BlockState state = CatalyticReformerBlockEntity.this.getBlockState();
                            CatalyticReformerBlockEntity.this.level.sendBlockUpdated(CatalyticReformerBlockEntity.this.worldPosition, state, state, 3);
                            CatalyticReformerBlockEntity.this.level.updateNeighborsAt(CatalyticReformerBlockEntity.this.worldPosition, state.getBlock());
                        }
                    }
                }
            }

            @Override
            public int getCount() {
                return 11;
            }

        };
        for (int i = 0; i < 3; i++) {
            this.tanks[i] = new FluidTank(maxCapacity) {
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

    @Override
    public int getBaseProcessTime() {
        return 1;
    }

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


    public LazyOptional<IFluidHandler> getTankCapability(int tankIndex) {
        return handlers[tankIndex];
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nuclearisticcore.catalytic_reformer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new CatalyticReformerMenu(containerId, playerInv, this, this.data);
    }

    private int getCatalystTierFromSlot() {
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return 0;

        if (stack.getItem() instanceof kirsan122.nuclearisticcore.common.item.CatalystItem item) {
            return item.getTier();
        }
        return 0;
    }

    private CatalyticReformerRecipe getCurrentRecipe() {
        assert level != null;

        FluidStack currentFluid = tanks[0].getFluid();
        int currentCatalystTier = getCatalystTierFromSlot();

        if (this.inventoryChanged || currentFluid.getFluid() != this.lastCheckedFluid || currentFluid.getAmount() < this.lastCheckedAmount || currentCatalystTier != this.lastCheckedCatalystTier) {

            this.lastCheckedFluid = currentFluid.getFluid();
            this.lastCheckedAmount = currentFluid.getAmount();
            this.lastCheckedCatalystTier = currentCatalystTier;
            this.inventoryChanged = false;


            this.cachedRecipe = level.getRecipeManager()
                    .getAllRecipesFor(NCRecipes.CATALYTIC_REFORMER_TYPE.get())
                    .stream()
                    .filter(r -> r.inputFluid().isFluidEqual(currentFluid) && currentCatalystTier >= r.catalystTier())
                    .findFirst()
                    .orElse(null);
        }

        return this.cachedRecipe;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CatalyticReformerBlockEntity entity) {
        if (level.isClientSide) return;

        drainItemToTank(entity.tanks[0], entity.itemHandler, 1, 1, entity);
        fillItemFromTank(entity.tanks[1], entity.itemHandler, 2, 2, entity);
        fillItemFromTank(entity.tanks[2], entity.itemHandler, 3, 3, entity);
        chargeFromEnergySlot(entity.energyStorage, entity.itemHandler, 4, entity);

        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        var inputTank = entity.tanks[0];

        if (inputTank.isEmpty()) {
            entity.cachedRecipe = null;
            entity.lastCheckedFluid = Fluids.EMPTY;
            entity.lastCheckedAmount = 0;
            return;
        }

        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));
        if (canWork) {
            CatalyticReformerRecipe recipe = entity.getCurrentRecipe();

            if (recipe != null
                    && inputTank.getFluidAmount() >= recipe.inputFluid().getAmount()
                    && entity.canOutput(recipe.outputFluids())
                    && entity.energyStorage.getEnergyStored() >= 45) {

                entity.energyStorage.extractEnergy(45, false);

                inputTank.drain(recipe.inputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

                FluidStack[] outputs = recipe.outputFluids();
                if (outputs.length > 0 && outputs[0] != null && !outputs[0].isEmpty()) {
                    entity.tanks[1].fill(outputs[0].copy(), IFluidHandler.FluidAction.EXECUTE);
                }
                if (outputs.length > 1 && outputs[1] != null && !outputs[1].isEmpty()) {
                    entity.tanks[2].fill(outputs[1].copy(), IFluidHandler.FluidAction.EXECUTE);
                }

                setChanged(level, pos, state);
            }
        }
    }


    private boolean canOutput(FluidStack[] outputs) {
        if (outputs == null || outputs.length == 0) return true;

        if (outputs[0] != null && !outputs[0].isEmpty()) {
            FluidTank targetTank1 = this.tanks[1];
            if (!targetTank1.getFluid().isEmpty() && !targetTank1.getFluid().isFluidEqual(outputs[0])) return false;
            if (targetTank1.getFluidAmount() + outputs[0].getAmount() > targetTank1.getCapacity()) return false;
        }

        if (outputs.length > 1 && outputs[1] != null && !outputs[1].isEmpty()) {
            FluidTank targetTank2 = this.tanks[2];
            if (!targetTank2.getFluid().isEmpty() && !targetTank2.getFluid().isFluidEqual(outputs[1])) return false;
            return targetTank2.getFluidAmount() + outputs[1].getAmount() <= targetTank2.getCapacity();
        }

        return true;
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergyStorage;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < 2; i++) handlers[i].invalidate();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inputTank", tanks[0].writeToNBT(new CompoundTag()));
        tag.put("outputTank1", tanks[1].writeToNBT(new CompoundTag()));
        tag.put("outputTank2", tanks[2].writeToNBT(new CompoundTag()));
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tanks[0].readFromNBT(tag.getCompound("inputTank"));
        tanks[1].readFromNBT(tag.getCompound("outputTank1"));
        tanks[2].readFromNBT(tag.getCompound("outputTank2"));

        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
        this.energyStorage.setEnergyDirect(tag.getInt("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.inventoryChanged = true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiData.getRelativeSide(blockFacing, side);

        if (cap == ForgeCapabilities.ENERGY) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];
                if (sideMode == 0 || sideMode == 2) return LazyOptional.empty();
            }
            return energyStorageLazyOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];

                if (sideMode == 0) return LazyOptional.empty();
                switch (sideMode) {
                    case 1 -> {
                        return handlers[0].cast();
                    }
                    case 2 -> {
                        return handlers[1].cast();
                    }
                }
            }
            return handlers[1].cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];
                if (sideMode == 0) return LazyOptional.empty();
                switch (sideMode) {
                    case 1, 2 -> {
                        return itemHandlerLazyOptional.cast();
                    }
                }
            }
            return itemHandlerLazyOptional.cast();
        }

        return super.getCapability(cap, side);
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

    @Override
    public void refreshCaps() {
        this.itemHandlerLazyOptional.invalidate();
        this.energyStorageLazyOptional.invalidate();
        this.handlers[0].invalidate();
        this.handlers[1].invalidate();
        this.handlers[2].invalidate();

        this.itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
        this.energyStorageLazyOptional = LazyOptional.of(() -> createEnergyStorageWrapper(this.energyStorage));
        this.handlers[0] = LazyOptional.of(() -> tanks[0]);
        this.handlers[1] = LazyOptional.of(() -> tanks[1]);
        this.handlers[2] = LazyOptional.of(() -> tanks[2]);
    }
}
