package net.indivis.voices.catalog;

/** One immutable take. A new recording changes hash/revision, never the line id. */
public record VoiceLine(String id, String speaker, String npcName, String text, String wireText,
                        String sound, String asset, String sha256, int durationMs,
                        String language, String takeVersion, String sourceFunction,
                        int nextSequence, String productionId) {
    public VoiceLine(String id, String speaker, String npcName, String text, String wireText,
                     String sound, String asset, String sha256, int durationMs,
                     String language, String takeVersion, String sourceFunction, int nextSequence) {
        this(id, speaker, npcName, text, wireText, sound, asset, sha256, durationMs,
            language, takeVersion, sourceFunction, nextSequence, null);
    }
    /** Zero is the native idle state, never a request to advance a quest. */
    public boolean sequenced() { return nextSequence > 0; }
}
