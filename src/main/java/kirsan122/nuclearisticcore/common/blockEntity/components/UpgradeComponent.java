package kirsan122.nuclearisticcore.common.blockEntity.components;

import kirsan122.nuclearisticcore.common.util.NCTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Created by kirsan122,
 * Data: 30.10.2026,
 * Time: 9:51
 */
public class UpgradeComponent {
    private final BlockEntity parent;

    private final ItemStackHandler upgradeSlots = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            parent.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(NCTags.Items.ALL_UPGRADES);
        }
    };

    public UpgradeComponent(BlockEntity parent) {
        this.parent = parent;
    }

    public ItemStackHandler getUpgradeSlots() {
        return this.upgradeSlots;
    }

    public void save(CompoundTag tag) {
        tag.put("upgrade_slots", upgradeSlots.serializeNBT());
    }

    public void load(CompoundTag tag) {
        if (tag.contains("upgrade_slots")) {
            upgradeSlots.deserializeNBT(tag.getCompound("upgrade_slots"));
        }
    }
}