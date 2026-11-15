package kirsan122.nuclearisticcore.common.blockEntity.components;

import kirsan122.nuclearisticcore.api.RedstoneModes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by kirsan122,
 * Data: 30.10.2026,
 * Time: 9:50
 */
public class RedstoneComponent {
    private final BlockEntity parent;
    private RedstoneModes redstoneMode = RedstoneModes.IGNORED;

    public RedstoneComponent(BlockEntity parent) {
        this.parent = parent;
    }

    public RedstoneModes getRedstoneMode() {
        return this.redstoneMode;
    }

    public void setRedstoneMode(RedstoneModes mode) {
        this.redstoneMode = mode;
        this.parent.setChanged();
    }

    public void save(CompoundTag tag) {
        tag.putString("RedstoneMode", this.redstoneMode.getSerializedName());
    }

    public void load(CompoundTag tag) {
        if (tag.contains("RedstoneMode")) {
            this.redstoneMode = RedstoneModes.valueOf(tag.getString("RedstoneMode").toUpperCase());
        }
    }
}

