package kirsan122.nuclearisticcore.common.blockEntity.machines;

import kirsan122.nuclearisticcore.api.IConfigurableBE;
import kirsan122.nuclearisticcore.api.IUpgradableBE;
import kirsan122.nuclearisticcore.network.packet.PacketUpdateRedstone;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by kirsan122,
 * Data: 22.10.2026,
 * Time: 19:32
 */
public class UniversalBlockEntity extends BlockEntity implements PacketUpdateRedstone.IRedstoneConfigurable, IUpgradableBE, IConfigurableBE {
}
