package kirsan122.nuclearisticcore.common.item.upgrades;

import net.minecraft.world.item.Item;

/* Crusher will support this upgrade, that makes crusher's second drop multiplier a bit larger.
 * I will do it, but later.
 */
@SuppressWarnings("unused")
public class SieveUpgradeItem extends Item {
    private final float secondaryDropMultiplier;

    public SieveUpgradeItem(float secondaryDropMultiplier, Properties properties) {
        super(properties);
        this.secondaryDropMultiplier = secondaryDropMultiplier;
    }

    public float getSecondaryDropMultiplier() {
        return this.secondaryDropMultiplier;
    }
}
