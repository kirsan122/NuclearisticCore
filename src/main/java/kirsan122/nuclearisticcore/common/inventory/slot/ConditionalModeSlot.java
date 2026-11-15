package kirsan122.nuclearisticcore.common.inventory.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Supplier;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:32
 */
public class ConditionalModeSlot extends SlotItemHandler {
    private final Supplier<Boolean> visibilityCondition;

    public ConditionalModeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Supplier<Boolean> visibilityCondition) {
        super(itemHandler, index, xPosition, yPosition);
        this.visibilityCondition = visibilityCondition;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return visibilityCondition.get() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player playerIn) {
        return visibilityCondition.get() && super.mayPickup(playerIn);
    }

    @Override
    public boolean isActive() {
        return visibilityCondition.get();
    }
}
