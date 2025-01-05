package org.inferis.manicminer.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HotKeyPressedPayload(boolean isPressed) implements CustomPayload {
    public static final CustomPayload.Id<HotKeyPressedPayload> ID = new CustomPayload.Id<>(Identifier.of("manicminer:hotkey_pressed"));
    public static final PacketCodec<RegistryByteBuf, HotKeyPressedPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, HotKeyPressedPayload::isPressed, HotKeyPressedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}