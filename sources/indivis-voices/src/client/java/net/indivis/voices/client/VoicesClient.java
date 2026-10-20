package net.indivis.voices.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.security.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.*;
import net.indivis.voices.IndivisVoices;
import net.indivis.voices.catalog.VoiceLine;
import net.indivis.voices.network.VoicePackets;
import net.indivis.voices.session.PlaybackLease;
import net.indivis.voices.client.mixin.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.resource.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public final class VoicesClient implements ClientModInitializer {
    public static VoiceOptions options;
    private final ClientDialogueBridge bridge = new ClientDialogueBridge();
    private Set<String> available = Set.of();
    private long packGeneration, highestGeneration, lastHelloNanos;
    private boolean helloDirty;
    private UUID epoch;
    private VoicePackets.Start active, pending;
    private NpcVoiceSound sound, fading;
    private long beganNanos;
    private boolean heard;
    private long pendingDeadlineNanos;
    private SoundInstance preview;
    private VoiceLine previewLine;
    private long previewBegan;
    private boolean previewHeard;
    private String lastDiagnostic = "aucune lecture demandée";
    private long lastFadeUpdate;
    private int captionWidth = -1;
    private List<net.minecraft.text.OrderedText> caption = List.of();

    @Override public void onInitializeClient() {
        options = VoiceOptions.load();
        ClientPlayNetworking.registerGlobalReceiver(VoicePackets.Start.ID, (packet, context) -> start(packet));
        ClientPlayNetworking.registerGlobalReceiver(VoicePackets.Stop.ID, (packet, context) -> {
            if (active != null && active.token().equals(packet.token())) clear(false, PlaybackLease.End.CANCEL);
            if (pending != null && pending.token().equals(packet.token())) pending = null;
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            reset(); helloDirty = true;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudRenderCallback.EVENT.register((context, tickCounter) -> pumpFade());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override public Identifier getFabricId() { return Identifier.of("indivis_voices", "verify_pack"); }
            @Override public void reload(ResourceManager manager) {
                long generation = ++packGeneration;
                available = Set.of();
                clear(true, PlaybackLease.End.MISSING);
                helloDirty = true;
                // Disk reads / SHA-256 only on the worker, never in render or tick.
                CompletableFuture.supplyAsync(() -> verify(manager)).exceptionally(failure -> {
                    IndivisVoices.LOG.warn("Voices client : vérification audio impossible, chemin texte conservé", failure);
                    return Set.of();
                }).thenAccept(lines ->
                    MinecraftClient.getInstance().execute(() -> {
                        if (generation == packGeneration) {
                            available = lines; helloDirty = true;
                            IndivisVoices.LOG.info("Voices client : banque {} : {}/{} prises vérifiées", IndivisVoices.CATALOG.revision(), available.size(), IndivisVoices.CATALOG.lines().size());
                            if (available.isEmpty()) IndivisVoices.LOG.warn("Voices client : aucun audio valide chargé ; vérifier le JAR Voices ou une surcharge de ressources");
                        }
                    }));
            }
        });
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!bridge.screen(screen)) return;
            ScreenMouseEvents.allowMouseClick(screen).register((s, click) -> {
                if (!speaking() || (click.button() != 0 && click.button() != 1)) return true;
                // Native choices remain native. A click never both skips and selects.
                if (bridge.choiceAt(s, click.x(), click.y()) >= 0) { clear(true, PlaybackLease.End.CANCEL); return true; }
                skip(); return false;
            });
            ScreenKeyboardEvents.allowKeyPress(screen).register((s, key) -> {
                if (speaking() && (key.key() == 32 || key.key() == 257 || key.key() == 335)) { skip(); return false; }
                return true; // ESC, history and numbered choices keep b10 behavior.
            });
            ScreenEvents.afterRender(screen).register((s, context, x, y, delta) -> {
                pumpFade();
                if (!options.subtitles || active == null) return;
                if (captionWidth != s.width) {
                    captionWidth = s.width;
                    var line = IndivisVoices.CATALOG.line(active.token().line());
                    caption = client.textRenderer.wrapLines(Text.literal(line.speaker() + " : " + line.text()), Math.max(80, s.width - 60));
                }
                int top = 12;
                for (var text : caption) {
                    context.drawTextWithShadow(client.textRenderer, text, 30, top, 0xFFFFFFFF);
                    top += client.textRenderer.fontHeight + 2;
                }
            });
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            dispatcher.register(literal("ivvoices")
                .executes(c -> status(c.getSource()))
                .then(literal("test").executes(c -> preview(c.getSource(), "prologue.geraud.reveil.01"))
                    .then(argument("line", StringArgumentType.word()).suggests((c, builder) -> {
                        for (var line : IndivisVoices.CATALOG.lines()) {
                            builder.suggest(line.productionId() != null ? line.productionId() : line.id());
                        }
                        return builder.buildFuture();
                    }).executes(c -> preview(c.getSource(), StringArgumentType.getString(c, "line")))))
                .then(literal("on").executes(c -> { options.enabled = true; changed(); return status(c.getSource()); }))
                .then(literal("off").executes(c -> { options.enabled = false; clear(true, PlaybackLease.End.CANCEL); changed(); return status(c.getSource()); }))
                .then(literal("volume").then(argument("percent", IntegerArgumentType.integer(0, 100)).executes(c -> {
                    options.volume = IntegerArgumentType.getInteger(c, "percent") / 100f;
                    if (options.volume == 0) clear(true, PlaybackLease.End.CANCEL);
                    changed(); return status(c.getSource());
                })))
                .then(literal("subtitles").then(literal("on").executes(c -> { options.subtitles = true; changed(); return 1; }))
                    .then(literal("off").executes(c -> { options.subtitles = false; changed(); return 1; })))
            );
        });
    }
    private int status(FabricClientCommandSource source) {
        var client = MinecraftClient.getInstance();
        source.sendFeedback(Text.literal("Indivis Voices : " + (options.enabled ? "actif" : "désactivé") +
            ", volume " + Math.round(options.volume * 100) + " %, prises vérifiées " + available.size() +
            "/" + IndivisVoices.CATALOG.lines().size() + ", pont b10 " + (bridge.available() ? "prêt" : "indisponible") +
            ", canal serveur " + (ClientPlayNetworking.canSend(VoicePackets.Hello.ID) ? "présent" : "absent") +
            ", Minecraft général " + Math.round(client.options.getSoundVolume(SoundCategory.MASTER) * 100) +
            " %, voix " + Math.round(client.options.getSoundVolume(SoundCategory.VOICE) * 100) + " %. " + VoicePlaybackVolume.diagnostic() + ". " + lastDiagnostic +
            ". /ivvoices test|on|off|volume <0..100>|subtitles on|off"));
        return 1;
    }
    /** Local audio check: no server packet, NPC, camera, scoreboard or quest action. */
    private int preview(FabricClientCommandSource source, String id) {
        if (speaking() || bridge.capturing()) {
            source.sendError(Text.literal("Fermez le dialogue avant de tester la voix.")); return 0;
        }
        var line = IndivisVoices.CATALOG.lookup(id);
        if (line == null) {
            source.sendError(Text.literal("Code de réplique inconnu : " + id)); return 0;
        }
        if (!available.contains(line.id()) || !options.enabled || options.volume <= 0) {
            source.sendError(Text.literal("Test indisponible : activer Voices et vérifier que cette prise est chargée. /ivvoices")); return 0;
        }
        var client = MinecraftClient.getInstance();
        if (client.options.getSoundVolume(SoundCategory.MASTER) <= 0 || client.options.getSoundVolume(SoundCategory.VOICE) <= 0) {
            source.sendError(Text.literal("Le volume général ou Narrator/Voice de Minecraft est à zéro.")); return 0;
        }
        stopPreview();
        previewLine = line;
        preview = new VoicePlaybackVolume.Preview(line.sound(), options.volume);
        var result = client.getSoundManager().play(preview);
        previewBegan = System.nanoTime(); previewHeard = false;
        lastDiagnostic = "test audio " + id + " : " + result;
        IndivisVoices.LOG.info("Voices client : {}", lastDiagnostic);
        if (result != SoundSystem.PlayResult.STARTED) {
            stopPreview(); source.sendError(Text.literal(lastDiagnostic)); return 0;
        }
        source.sendFeedback(Text.literal("Test local " + id + " : " + line.text()));
        return 1;
    }
    private void stopPreview() {
        if (preview != null) MinecraftClient.getInstance().getSoundManager().stop(preview);
        preview = null;
        previewLine = null;
    }
    private void changed() { options.save(); helloDirty = true; }
    private Set<String> verify(ResourceManager manager) {
        var verified = new HashSet<String>();
        for (var line : IndivisVoices.CATALOG.lines()) {
            var resource = manager.getResource(Identifier.of("indivis_voices", "sounds/" + line.asset()));
            if (resource.isEmpty()) {
                IndivisVoices.LOG.warn("Voices client : audio absent : {}", line.id()); continue;
            }
            try (var stream = resource.get().getInputStream()) {
                var hash = MessageDigest.getInstance("SHA-256");
                byte[] buffer = new byte[16_384]; int read; long size = 0;
                while ((read = stream.read(buffer)) >= 0) {
                    size += read; if (size > 2_000_000) throw new IOException("Voice asset too large");
                    hash.update(buffer, 0, read);
                }
                if (HexFormat.of().formatHex(hash.digest()).equals(line.sha256())) verified.add(line.id());
                else IndivisVoices.LOG.warn("Voices client : empreinte audio différente du catalogue : {}", line.id());
            } catch (IOException | NoSuchAlgorithmException e) {
                IndivisVoices.LOG.warn("Voices client : audio illisible : {}", line.id(), e);
            }
        }
        return Set.copyOf(verified);
    }
    private void start(VoicePackets.Start packet) {
        var token = packet.token();
        if (epoch != null && !epoch.equals(token.epoch()) || token.generation() <= highestGeneration) return;
        epoch = token.epoch(); highestGeneration = token.generation();
        clear(false, null);
        if (!enabled()) { reject(packet, "voix désactivées ou pont b10 indisponible"); return; }
        if (!available.contains(token.line())) { reject(packet, "audio absent ou empreinte différente"); return; }
        pending = packet;
        pendingDeadlineNanos = System.nanoTime() + 1_000_000_000L;
        beginPending();
    }
    private boolean enabled() { return options.enabled && options.volume > 0 && bridge.available(); }
    private void reject(VoicePackets.Start packet, String reason) {
        lastDiagnostic = packet.token().line() + " : " + reason;
        IndivisVoices.LOG.warn("Voices client : retour texte : {}", lastDiagnostic);
        send(packet, PlaybackLease.End.MISSING);
    }
    private boolean speaking() { return active != null || pending != null; }
    private void beginPending() {
        if (pending == null || fading != null) return;
        if (!enabled()) { clear(true, PlaybackLease.End.CANCEL); return; }
        if (System.nanoTime() > pendingDeadlineNanos) {
            var failed = pending; pending = null;
            reject(failed, "session, écran ou réplique b10 indisponible après 1 s"); return;
        }
        // b10 owns session opening/capture. Allow the matching native line and
        // screen to become ready instead of rejecting a transient absence.
        if (!bridge.matches(pending.token().session(), pending.npc()) ||
            !bridge.focus(IndivisVoices.CATALOG.line(pending.token().line()), MinecraftClient.getInstance().currentScreen)) return;
        active = pending; pending = null;
        captionWidth = -1;
        var line = IndivisVoices.CATALOG.line(active.token().line());
        sound = new NpcVoiceSound(line.sound(), active.npc(), active.entityId());
        var result = MinecraftClient.getInstance().getSoundManager().play(sound);
        beganNanos = System.nanoTime(); heard = false;
        lastDiagnostic = active.token().line() + " : " + result;
        IndivisVoices.LOG.info("Voices client : lecture {}", lastDiagnostic);
        if (result != SoundSystem.PlayResult.STARTED) clear(true, PlaybackLease.End.MISSING);
    }
    private void tick(MinecraftClient client) {
        long now = System.nanoTime();
        if (preview != null) {
            boolean playing = client.getSoundManager().isPlaying(preview);
            previewHeard |= playing;
            long elapsed = (now - previewBegan) / 1_000_000;
            if (!playing && elapsed > 500 || elapsed > previewLine.durationMs() + 3000L) {
                lastDiagnostic = previewHeard ? "test audio : source terminée" : "test audio : aucune source active observée";
                IndivisVoices.LOG.info("Voices client : {}", lastDiagnostic);
                stopPreview();
            }
        }
        pumpFade();
        beginPending();
        if (helloDirty && now - lastHelloNanos > 600_000_000L && ClientPlayNetworking.canSend(VoicePackets.Hello.ID)) {
            ClientPlayNetworking.send(new VoicePackets.Hello(VoicePackets.PROTOCOL, IndivisVoices.CATALOG.revision(), enabled(), List.copyOf(available)));
            lastHelloNanos = now; helloDirty = false;
        }
        if (active == null) return;
        if (!enabled() || !bridge.matches(active.token().session(), active.npc()) ||
            !bridge.screen(client.currentScreen) || client.world == null) {
            clear(true, PlaybackLease.End.CANCEL); return;
        }
        bridge.keepVisible();
        boolean playing = client.getSoundManager().isPlaying(sound);
        heard |= playing;
        if (sound.isDone()) { clear(true, PlaybackLease.End.CANCEL); return; }
        long elapsed = (now - beganNanos) / 1_000_000;
        bridge.paginate(client.currentScreen, elapsed, IndivisVoices.CATALOG.line(active.token().line()).durationMs());
        if (!playing && elapsed > 500) {
            clear(true, heard ? PlaybackLease.End.NATURAL : PlaybackLease.End.MISSING);
        } else if (elapsed > IndivisVoices.CATALOG.line(active.token().line()).durationMs() + 3_000L) {
            clear(true, PlaybackLease.End.MISSING);
        }
    }
    private void skip() {
        if (pending != null) { send(pending, PlaybackLease.End.SKIP); pending = null; }
        clear(true, PlaybackLease.End.SKIP);
    }
    private void clear(boolean notify, PlaybackLease.End reason) {
        stopPreview();
        if (pending != null && notify && reason != null) send(pending, reason);
        pending = null;
        if (active != null && notify && reason != null) send(active, reason);
        active = null;
        bridge.finishFocus(reason == PlaybackLease.End.MISSING || reason == PlaybackLease.End.CANCEL,
            MinecraftClient.getInstance().currentScreen);
        if (sound != null) {
            if (fading != null) fading.stopNow();
            fading = sound; sound = null; fading.fade.begin(System.nanoTime());
        }
    }
    private static void send(VoicePackets.Start start, PlaybackLease.End end) {
        if (ClientPlayNetworking.canSend(VoicePackets.End.ID)) ClientPlayNetworking.send(new VoicePackets.End(start.token(), end));
    }
    /** Only our fading source is touched. Native category/master levels remain authoritative. */
    private void pumpFade() {
        if (fading == null) return;
        long now = System.nanoTime();
        if (fading.fade.finished(now)) { fading.stopNow(); fading = null; return; }
        if (now - lastFadeUpdate < 4_000_000L) return;
        lastFadeUpdate = now;
        var manager = MinecraftClient.getInstance().getSoundManager();
        var system = ((SoundManagerAccessor) manager).indivisVoices$system();
        var access = (SoundSystemAccessor) system;
        var source = access.indivisVoices$sources().get(fading);
        if (source != null) {
            float volume = access.indivisVoices$adjustedVolume(fading);
            source.run(s -> s.setVolume(volume));
        }
    }
    private void reset() {
        clear(false, null);
        if (fading != null) { fading.stopNow(); fading = null; }
        epoch = null; highestGeneration = 0; helloDirty = false; lastHelloNanos = 0;
    }
}
