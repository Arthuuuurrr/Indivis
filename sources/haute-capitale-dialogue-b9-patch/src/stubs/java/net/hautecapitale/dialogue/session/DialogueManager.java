package net.hautecapitale.dialogue.session;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class DialogueManager {
    private DialogueManager() {}
    public static boolean enSession(net.minecraft.entity.player.PlayerEntity player) { return false; }
    public static DialogueSession ouvrir(ServerPlayerEntity player, Entity entity, DialogueMode mode) { return null; }
}
