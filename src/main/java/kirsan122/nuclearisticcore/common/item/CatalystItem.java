package kirsan122.nuclearisticcore.common.item;

import net.minecraft.world.item.Item;

/**
 * Created by kirsan122,
 * Data: 25.10.2026,
 * Time: 19:23
 */
public class CatalystItem extends Item {
    private final int tier;

    public CatalystItem(int tier, Properties properties) {
        super(properties);

        if (tier == 0) {
            throw new IllegalArgumentException("Catalytic tier cannot be '0'.");
        }
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }
}

