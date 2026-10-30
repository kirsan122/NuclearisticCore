package kirsan122.nuclearisticcore.api;

import kirsan122.nuclearisticcore.common.blockEntity.machines.UniversalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Universal interface for menus that support automation side configuration, redstone configuration and upgrades.
 */
public interface IUniversalMenu<T extends UniversalBlockEntity> {
    int getSideMode(int sideId);
    void setSideMode(int sideId, int mode);
    RedstoneModes getRedstoneMode();
    BlockPos getBlockEntityPos();
    T getBlockEntity();
    void setUpgradeTabFullyOpen(boolean open);
}
