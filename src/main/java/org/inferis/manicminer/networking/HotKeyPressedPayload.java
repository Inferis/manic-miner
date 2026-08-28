package org.inferis.manicminer.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HotKeyPressedPayload(boolean isPressed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HotKeyPressedPayload> ID = new CustomPacketPayload.Type<>(Identifier.parse("manicminer:hotkey_pressed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotKeyPressedPayload> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, HotKeyPressedPayload::isPressed, HotKeyPressedPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}