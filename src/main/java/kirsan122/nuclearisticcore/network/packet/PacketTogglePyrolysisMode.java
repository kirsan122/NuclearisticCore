package kirsan122.nuclearisticcore.network.packet;

import kirsan122.nuclearisticcore.common.blockEntity.machines.PyrolysisOvenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 10:27
 */
public class PacketTogglePyrolysisMode {
    private final BlockPos pos;

    public PacketTogglePyrolysisMode(BlockPos pos) {
        this.pos = pos;
    }

    public PacketTogglePyrolysisMode(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(this.pos);
            if (be instanceof PyrolysisOvenBlockEntity pyrolysis) {
                pyrolysis.toggleMode();
                pyrolysis.setChanged();

                player.level().sendBlockUpdated(this.pos, be.getBlockState(), be.getBlockState(), 3);
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}

