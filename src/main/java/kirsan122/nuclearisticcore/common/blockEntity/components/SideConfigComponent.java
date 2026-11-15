package kirsan122.nuclearisticcore.common.blockEntity.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by kirsan122,
 * Data: 30.10.2026,
 * Time: 9:50
 */
public class SideConfigComponent {
    private final BlockEntity parent;
    private final int[] sideConfigModes = new int[6];

    public SideConfigComponent(BlockEntity parent) {
        this.parent = parent;
    }

    public int getSideMode(int sideId) {
        if (sideId < 0 || sideId >= 6) return 0;
        return this.sideConfigModes[sideId];
    }

    public void setSideMode(int sideId, int mode) {
        if (sideId >= 0 && sideId < 6) {
            this.sideConfigModes[sideId] = mode;
            this.parent.setChanged();
        }
    }

    public void save(CompoundTag tag) {
        tag.putIntArray("SideConfigs", this.sideConfigModes);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("SideConfigs", Tag.TAG_INT_ARRAY)) {
            int[] savedModes = tag.getIntArray("SideConfigs");
            if (savedModes.length == 6) {
                System.arraycopy(savedModes, 0, this.sideConfigModes, 0, 6);
            }
        }
    }
}

