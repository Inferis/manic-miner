package org.inferis.manicminer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.inferis.manicminer.networking.HotKeyPressedPayload;

import java.util.ArrayList;

public class HotkeyPressedTracker {
    private static final ArrayList<String> playersPressed = new ArrayList<>();

    public HotkeyPressedTracker() {
        PayloadTypeRegistry.serverboundPlay().register(HotKeyPressedPayload.ID, HotKeyPressedPayload.CODEC);

        // Register payload receiver. The client will send the payload to the server, which
        // will then in turn set the right mode on the magnet items.
        ServerPlayNetworking.registerGlobalReceiver(HotKeyPressedPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var id = context.player().getStringUUID();
                if (payload.isPressed()) {
                    if (!playersPressed.contains(id)) {
                        playersPressed.add(id);
                    }
                }
                else {
                    playersPressed.remove(id);
                }
            });
        });
    }

    public Boolean isPlayerPressingHotKey(ServerPlayer player) {
        return playersPressed.contains(player.getStringUUID());
    }
}
