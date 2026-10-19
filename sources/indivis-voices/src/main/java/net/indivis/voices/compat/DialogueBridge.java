package net.indivis.voices.compat;

import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.indivis.voices.IndivisVoices;

/** Cached reflection over signatures verified in the supplied b10/PRE34 binaries. */
public final class DialogueBridge {
    public record Session(int id, UUID npc, int entityId, UUID character, String name) {}
    private Method session, selected, characterId, touch;
    private Field id, npc, entityId, world, name;
    private boolean available;

    public DialogueBridge() {
        var loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("haute_capitale_dialogue")) return;
        // Fail closed for a different binary until its API has been checked.
        var version = loader.getModContainer("haute_capitale_dialogue").orElseThrow()
            .getMetadata().getVersion().getFriendlyString();
        if (!version.equals("0.1.0+1.21.11.b10")) {
            IndivisVoices.LOG.warn("Voices: dialogue {} non audité, chemin texte conservé", version);
            return;
        }
        try {
            var manager = Class.forName("net.hautecapitale.dialogue.session.DialogueManager");
            session = manager.getMethod("session", PlayerEntity.class);
            var type = Class.forName("net.hautecapitale.dialogue.session.DialogueSession");
            touch = type.getMethod("toucher", long.class);
            id = type.getField("id"); npc = type.getField("pnj"); entityId = type.getField("pnjEntityId");
            world = type.getField("monde");
            name = type.getField("pnjNom");
            if (loader.isModLoaded("nexuscharacters")) {
                selected = Class.forName("net.tompsen.nexuscharacters.NexusCharacters")
                    .getMethod("getSelectedCharacter", ServerPlayerEntity.class);
                characterId = Class.forName("net.tompsen.nexuscharacters.CharacterDto").getMethod("id");
            }
            available = true;
            IndivisVoices.LOG.info("Voices : pont b10 prêt ; contrôle d'identité Nexus {}", selected != null ? "actif" : "absent");
        } catch (ReflectiveOperationException | LinkageError e) {
            IndivisVoices.LOG.warn("Voices: pont indisponible, chemin texte conservé", e);
        }
    }
    public Session session(ServerPlayerEntity player) {
        if (!available) return null;
        try {
            Object s = ((Optional<?>) session.invoke(null, player)).orElse(null);
            if (s == null) return null;
            if (!world.get(s).equals(player.getEntityWorld().getRegistryKey())) return null;
            UUID character = character(player);
            if (character == null) return null;
            return new Session(id.getInt(s), (UUID) npc.get(s), entityId.getInt(s), character, (String) name.get(s));
        } catch (ReflectiveOperationException | RuntimeException e) {
            available = false; // Log once, never flood ticks or retain a broken lease.
            IndivisVoices.LOG.warn("Voices: pont désactivé après échec", e);
            return null;
        }
    }
    public UUID character(ServerPlayerEntity player) {
        if (!available) return null;
        if (selected == null) return player.getUuid();
        try {
            Object dto = selected.invoke(null, player);
            return dto == null ? null : (UUID) characterId.invoke(dto);
        } catch (ReflectiveOperationException e) { return null; }
    }
    public void keepAlive(ServerPlayerEntity player, long tick) {
        if (!available) return;
        try {
            Object s = ((Optional<?>) session.invoke(null, player)).orElse(null);
            if (s != null) touch.invoke(s, tick);
        } catch (ReflectiveOperationException e) { available = false; }
    }
}
