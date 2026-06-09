package org.inferis.manicminer.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record HighlightBlocksPayload(List<BlockPos> positions) implements CustomPayload {
    public static final Id<HighlightBlocksPayload> ID = new CustomPayload.Id<>(Identifier.of("manicminer:highlight_blocks"));

    public static final PacketCodec<RegistryByteBuf, HighlightBlocksPayload> CODEC = new PacketCodec<>() {
        @Override
        public HighlightBlocksPayload decode(RegistryByteBuf buf) {
            int size = buf.readVarInt();
            var positions = new ArrayList<BlockPos>(size);
            for (int i = 0; i < size; i++) {
                positions.add(buf.readBlockPos());
            }
            return new HighlightBlocksPayload(positions);
        }

        @Override
        public void encode(RegistryByteBuf buf, HighlightBlocksPayload value) {
            buf.writeVarInt(value.positions().size());
            for (var pos : value.positions()) {
                buf.writeBlockPos(pos);
            }
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
