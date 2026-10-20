package net.indivis.voices.client;

import java.util.UUID;
import net.indivis.voices.session.FadeEnvelope;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

final class NpcVoiceSound extends AbstractSoundInstance implements TickableSoundInstance, VoicePlaybackVolume.VoiceSound {
    final FadeEnvelope fade = new FadeEnvelope();
    private final UUID npc;
    private final int entityId;
    private boolean done;
    NpcVoiceSound(String event, UUID npc, int entityId) {
        super(Identifier.of("indivis_voices", event), SoundCategory.VOICE, Random.create());
        this.npc = npc; this.entityId = entityId;
        this.attenuationType = AttenuationType.LINEAR;
        this.relative = false;
        tick();
    }
    @Override public void tick() {
        var world = MinecraftClient.getInstance().world;
        var entity = world == null ? null : world.getEntityById(entityId);
        if (entity == null || !npc.equals(entity.getUuid()) || !entity.isAlive()) { done = true; return; }
        x = entity.getX(); y = entity.getEyeY(); z = entity.getZ();
        if (fade.finished(System.nanoTime())) done = true;
    }
    @Override public float getVolume() { return VoicesClient.options.volume * fade.gain(System.nanoTime()); }
    @Override public boolean isDone() { return done; }
    void stopNow() { done = true; MinecraftClient.getInstance().getSoundManager().stop(this); }
}
