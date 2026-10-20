package net.indivis.voices.client;

import java.lang.reflect.*;
import java.util.UUID;
import java.util.List;
import net.indivis.voices.catalog.VoiceLine;
import net.indivis.voices.session.DialogueContinuation;
import net.minecraft.text.Text;
import net.fabricmc.loader.api.FabricLoader;
import net.indivis.voices.IndivisVoices;
import net.minecraft.client.gui.screen.Screen;

/** b10 API adapter; no camera, movement, native choice or quest implementation. */
final class ClientDialogueBridge {
    private Method session, capture, npc, typewriterEnabled, typewriterSpeed, typewriter, choiceAt, advance, page, pages, history;
    private Field paragraphs, pageReset, captureVersion, captureUntil, panel;
    private List<Text> beforeFocus;
    private Text focusedParagraph;
    private DialogueContinuation<Text> continuation;
    private int focusedVersion;
    private boolean available;
    ClientDialogueBridge() {
        var loader = FabricLoader.getInstance();
        var container = loader.getModContainer("haute_capitale_dialogue");
        if (container.isEmpty() || !container.get().getMetadata().getVersion().getFriendlyString().equals("0.1.0+1.21.11.b10")) {
            IndivisVoices.LOG.warn("Voices client : dialogue b10 absent ou version non auditée, chemin texte conservé");
            return;
        }
        try {
            var state = Class.forName("net.hautecapitale.dialogue.client.DialogueClientState");
            session = state.getMethod("sessionId"); capture = state.getMethod("captureActive"); npc = state.getMethod("pnjUuid");
            typewriterEnabled = state.getMethod("typewriterActifCapture"); typewriterSpeed = state.getMethod("typewriterVitesse");
            paragraphs = field(state, "REPLIQUES"); pageReset = field(state, "pageRemise");
            captureVersion = field(state, "captureVersion"); captureUntil = field(state, "fenetreJusquaMs");
            var screen = Class.forName("net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen");
            panel = screen.getDeclaredField("panneau"); panel.setAccessible(true);
            var p = panel.getType();
            typewriter = p.getMethod("typewriter", boolean.class, int.class);
            choiceAt = p.getMethod("choixSous", double.class, double.class);
            advance = p.getMethod("avancer");
            page = p.getMethod("page"); pages = p.getMethod("pages"); history = p.getMethod("historiqueVisible");
            available = true;
            IndivisVoices.LOG.info("Voices client : pont dialogue b10 prêt");
        } catch (ReflectiveOperationException | LinkageError e) { fail(e); }
    }
    boolean available() { return available; }
    boolean screen(Screen screen) { return available && screen != null && panel.getDeclaringClass().isInstance(screen); }
    boolean matches(int id, UUID uuid) {
        if (!available) return false;
        try { return (boolean) capture.invoke(null) && (int) session.invoke(null) == id && uuid.equals(npc.invoke(null)); }
        catch (ReflectiveOperationException e) { fail(e); return false; }
    }
    boolean capturing() {
        if (!available) return false;
        try { return (boolean) capture.invoke(null); }
        catch (ReflectiveOperationException e) { fail(e); return false; }
    }
    /** Focus only an already captured, server-authorized line; retain native history/choices. */
    boolean focus(VoiceLine line, Screen screen) {
        if (!screen(screen)) return false;
        try {
            @SuppressWarnings("unchecked") var lines = (List<Text>) paragraphs.get(null);
            if (lines.isEmpty() || panel.get(screen) == null) return false;
            var last = lines.getLast(); String text = last.getString().strip();
            if (!text.equals(line.wireText()) && !text.equals(": " + line.text()) && !text.equals(line.text())) return false;
            beforeFocus = List.copyOf(lines);
            continuation = null;
            focusedParagraph = last;
            lines.clear(); lines.add(last);
            pageReset.setBoolean(null, true);
            captureVersion.setInt(null, captureVersion.getInt(null) + 1);
            focusedVersion = captureVersion.getInt(null);
            typewriter.invoke(panel.get(screen), false, typewriterSpeed.invoke(null));
            keepVisible();
            return true;
        } catch (ReflectiveOperationException e) { fail(e); return false; }
    }
    void finishFocus(boolean fallback, boolean consumed, Screen screen) {
        if (beforeFocus == null) return;
        try {
            if (fallback && captureVersion.getInt(null) == focusedVersion) {
                @SuppressWarnings("unchecked") var lines = (List<Text>) paragraphs.get(null);
                lines.clear(); lines.addAll(beforeFocus);
                pageReset.setBoolean(null, true);
                captureVersion.setInt(null, focusedVersion + 1);
            }
            if (consumed && available) {
                continuation = new DialogueContinuation<>(focusedParagraph, (int) session.invoke(null),
                    (UUID) npc.invoke(null), System.nanoTime() / 1_000_000L);
            }
        } catch (ReflectiveOperationException e) { fail(e); }
        beforeFocus = null;
        focusedParagraph = null;
        restoreTypewriter(screen);
    }
    /** b10 appends unvoiced replies without resetting the page. Remove only our
     * already spoken paragraph; native history, new text and choices stay intact. */
    void continueText(Screen screen) {
        if (continuation == null) return;
        if (!screen(screen) || !capturing()) { continuation = null; return; }
        try {
            @SuppressWarnings("unchecked") var lines = (List<Text>) paragraphs.get(null);
            var p = panel.get(screen);
            if (p == null) return;
            var next = continuation.poll((int) session.invoke(null), (UUID) npc.invoke(null), lines,
                System.nanoTime() / 1_000_000L, (boolean) history.invoke(p));
            if (next.isPresent()) {
                lines.clear(); lines.addAll(next.get());
                pageReset.setBoolean(null, true);
                captureVersion.setInt(null, captureVersion.getInt(null) + 1);
                IndivisVoices.LOG.info("Voices client : suite textuelle native affichée (session #{}, {} paragraphes)",
                    session.invoke(null), lines.size());
            }
            if (continuation.finished()) continuation = null;
        } catch (ReflectiveOperationException e) { fail(e); continuation = null; }
    }
    void cancelContinuation() { continuation = null; }
    void keepVisible() {
        if (!available) return;
        try {
            // Native silence closure must not mistake a long spoken take for inactivity.
            captureUntil.setLong(null, Math.max(captureUntil.getLong(null), System.currentTimeMillis() + 1500));
        } catch (ReflectiveOperationException e) { fail(e); }
    }
    void paginate(Screen screen, long elapsedMs, int durationMs) {
        if (!screen(screen)) return;
        try {
            var p = panel.get(screen);
            if (p == null || (boolean) history.invoke(p)) return;
            int count = (int) pages.invoke(p);
            int target = (int) Math.min(count - 1, elapsedMs * count / Math.max(1, durationMs));
            // b10 owns wrapping/layout; progression uses real take duration, not char count.
            while ((int) page.invoke(p) < target && (boolean) advance.invoke(p)) { }
        } catch (ReflectiveOperationException e) { fail(e); }
    }
    void restoreTypewriter(Screen screen) {
        if (!screen(screen)) return;
        try { if (panel.get(screen) != null) typewriter.invoke(panel.get(screen), typewriterEnabled.invoke(null), typewriterSpeed.invoke(null)); }
        catch (ReflectiveOperationException e) { fail(e); }
    }
    int choiceAt(Screen screen, double x, double y) {
        if (!screen(screen)) return -1;
        try { return panel.get(screen) == null ? -1 : (int) choiceAt.invoke(panel.get(screen), x, y); }
        catch (ReflectiveOperationException e) { fail(e); return -1; }
    }
    private void fail(Throwable e) {
        available = false;
        IndivisVoices.LOG.warn("Voices: interface b10 indisponible, chemin texte conservé", e);
    }
    private static Field field(Class<?> type, String name) throws ReflectiveOperationException {
        var f = type.getDeclaredField(name); f.setAccessible(true); return f;
    }
}
