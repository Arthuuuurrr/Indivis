package net.indivis.voices.catalog;

/** One immutable take. A new recording changes hash/revision, never the line id. */
public record VoiceLine(String id, String speaker, String npcName, String text, String wireText,
                        String sound, String asset, String sha256, int durationMs,
                        String language, String takeVersion, String sourceFunction,
                        int nextSequence) {}
