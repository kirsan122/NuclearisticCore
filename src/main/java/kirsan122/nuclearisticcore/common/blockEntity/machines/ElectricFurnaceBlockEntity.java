package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.upgrades.IEnergyUpgrade;
import kirsan122.nuclearisticcore.api.upgrades.ISpeedUpgrade;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.block.machines.ElectricFurnaceBlock;
import kirsan122.nuclearisticcore.common.energy.DynamicEnergyStorage;
import kirsan122.nuclearisticcore.common.init.NCBlockEntities;
import kirsan122.nuclearisticcore.common.init.NCMachineBlocks;
import kirsan122.nuclearisticcore.common.inventory.ElectricFurnaceMenu;
import kirsan122.nuclearisticcore.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.nuclearisticcore.common.item.upgrades.SpeedUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends UniversalBlockEntity implements MenuProvider, ISpeedUpgrade, IEnergyUpgrade {
    private final int maxEnergyStorage = 8500;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;
    private final SimpleContainer inventoryWrapper = new SimpleContainer(3);
    private SmeltingRecipe cachedRecipe = null;
    public boolean inventoryChanged = true;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot != 1) {
                inventoryChanged = true;
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 2;
        }
    };

    private final DynamicEnergyStorage energyStorage = new DynamicEnergyStorage(maxEnergyStorage, 1000, 0) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {

            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> createEnergyStorageWrapper(this.energyStorage));

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(NCBlockEntities.ELECTRIC_FURNACE_BE.get(), pos, state, true, true, true);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    case 2 -> ElectricFurnaceBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> ElectricFurnaceBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergyStorage;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nuclearisticcore.electric_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {

        return new ElectricFurnaceMenu(id, inventory, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity entity) {
        if (level.isClientSide()) return;
        if (!state.is(NCMachineBlocks.ELECTRIC_FURNACE.get())) return;

        chargeFromEnergySlot(entity.energyStorage, entity.itemHandler, 1, entity);

        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = true;
        if (entity.isRedstoneConfigurable()) {
            assert entity.redstoneComp != null;
            canWork = entity.redstoneComp.getRedstoneMode().canWork(level.hasNeighborSignal(pos));
        }
        boolean isCrafting = false;

        if (canWork) {

            Optional<SmeltingRecipe> recipe = entity.getCurrentRecipe();
            boolean canCraft = recipe.isPresent() && entity.canInsertIntoOutputSlot(recipe.get());

            if (canCraft) {
                SmeltingRecipe currentRecipe = recipe.get();

                int currentMaxProgress = SpeedUpgradeItem.getCookTime(entity);
                int currentEnergyConsumption = SpeedUpgradeItem.getEnergyUsage(20, entity.getUpgradeSlots());

                boolean hasEnoughEnergy = entity.energyStorage.getEnergyStored() >= currentEnergyConsumption;

                if (hasEnoughEnergy) {
                    isCrafting = true;
                    entity.energyStorage.consumeEnergyDirect(currentEnergyConsumption);

                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= currentMaxProgress) {
                        entity.craftItem(currentRecipe);
                        entity.progress = 0;


                        Optional<SmeltingRecipe> nextRecipe = entity.getCurrentRecipe();
                        boolean canContinue = nextRecipe.isPresent() && entity.canInsertIntoOutputSlot(nextRecipe.get());

                        isCrafting = canContinue && entity.energyStorage.getEnergyStored() >= SpeedUpgradeItem.getEnergyUsage(20, entity.getUpgradeSlots());
                    }
                }
            } else {
                entity.progress = 0;
            }
        }


        if (state.getValue(ElectricFurnaceBlock.ACTIVE) != isCrafting) {
            level.setBlock(pos, state.setValue(ElectricFurnaceBlock.ACTIVE, isCrafting), 3);
        }
    }

    private Optional<SmeltingRecipe> getCurrentRecipe() {
        assert this.level != null;


        if (!this.inventoryChanged && this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }


        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            this.inventoryWrapper.setItem(i, this.itemHandler.getStackInSlot(i));
        }


        Optional<SmeltingRecipe> recipeMatch = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this.inventoryWrapper, this.level);


        this.cachedRecipe = recipeMatch.orElse(null);
        this.inventoryChanged = false;

        return recipeMatch;
    }

    private boolean canInsertIntoOutputSlot(SmeltingRecipe recipe) {
        assert this.level != null;
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess());
        ItemStack outputSlot = this.itemHandler.getStackInSlot(2);


        if (outputSlot.isEmpty()) return true;


        if (!ItemStack.isSameItemSameTags(outputSlot, resultItem)) return false;


        return (outputSlot.getCount() + resultItem.getCount()) <= outputSlot.getMaxStackSize();
    }

    private void craftItem(SmeltingRecipe recipe) {
        assert this.level != null;
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess()).copy();
        if (resultItem.isEmpty()) return;


        this.itemHandler.extractItem(0, 1, false);

        ItemStack currentOutput = this.itemHandler.getStackInSlot(2);
        if (currentOutput.isEmpty()) {
            this.itemHandler.setStackInSlot(2, resultItem);
        } else {
            currentOutput.grow(resultItem.getCount());
            this.itemHandler.setStackInSlot(2, currentOutput);
        }


        this.inventoryChanged = true;
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");

        energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));

        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(tag.get("energy"));
        }
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    @Override
    public int getBaseProcessTime() {
        return maxProgress;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiData.getRelativeSide(blockFacing, side);

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
            }
            
            int sideId = relativeSide.get3DDataValue();
            int sideMode = getDeviceSideMode(sideId);

            if (sideMode == 1) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1)).cast();
            }

            if (sideMode == 2) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 2, 3) {
                    @Override
                    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                        return stack;
                    }
                }).cast();
            }
            return LazyOptional.empty();
        }

        if (cap == ForgeCapabilities.ENERGY) {
            if (side == null) {
                return energyStorageLazyOptional.cast();
            }
            int sideId = relativeSide.get3DDataValue();
            int sideMode = getDeviceSideMode(sideId);

            if (sideMode != 1) return LazyOptional.empty();

            return energyStorageLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void refreshCaps() {
        this.lazyItemHandler.invalidate();
        this.energyStorageLazyOptional.invalidate();

        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
        this.energyStorageLazyOptional = LazyOptional.of(() -> createEnergyStorageWrapper(this.energyStorage));
    }

}