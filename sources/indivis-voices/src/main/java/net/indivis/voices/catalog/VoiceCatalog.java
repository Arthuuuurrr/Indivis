package net.indivis.voices.catalog;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class VoiceCatalog {
    private record Document(int schema, String revision, List<VoiceLine> lines) {}
    private final String revision;
    private final Map<String, VoiceLine> byId = new LinkedHashMap<>();
    private final Map<String, VoiceLine> byMessage = new HashMap<>();

    public VoiceCatalog(String revision, List<VoiceLine> lines) {
        this.revision = Objects.requireNonNull(revision);
        for (var line : lines) {
            if (!line.id().matches("[a-z0-9_.]{1,96}") ||
                line.npcName() == null || line.npcName().isBlank() ||
                !line.asset().matches("[a-z0-9_/]+\\.ogg") ||
                !line.sha256().matches("[a-f0-9]{64}") ||
                line.durationMs() < 100 || line.durationMs() > 120_000 ||
                line.nextSequence() <= 0 || !line.language().equals("fr_fr")) {
                throw new IllegalArgumentException("Invalid voice line " + line.id());
            }
            if (byId.putIfAbsent(line.id(), line) != null ||
                byMessage.putIfAbsent(line.wireText(), line) != null) {
                throw new IllegalArgumentException("Duplicate line/text " + line.id());
            }
        }
    }
    public static VoiceCatalog load() {
        var stream = VoiceCatalog.class.getResourceAsStream("/indivis-voices/catalog.json");
        if (stream == null) throw new IllegalStateException("Missing voice catalog");
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var doc = new Gson().fromJson(reader, Document.class);
            if (doc.schema != 1) throw new IllegalStateException("Unsupported voice catalog");
            return new VoiceCatalog(doc.revision, doc.lines);
        } catch (java.io.IOException e) { throw new IllegalStateException(e); }
    }
    public String revision() { return revision; }
    public Collection<VoiceLine> lines() { return Collections.unmodifiableCollection(byId.values()); }
    public VoiceLine line(String id) { return byId.get(id); }
    /** Exact text match, deliberately no fuzzy lore/text inference. */
    public VoiceLine message(String wireText) { return byMessage.get(wireText); }
}
