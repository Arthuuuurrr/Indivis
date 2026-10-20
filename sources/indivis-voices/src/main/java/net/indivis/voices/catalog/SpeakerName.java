package net.indivis.voices.catalog;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Same name-query principle as b10 nearest_named; no text or lore fuzzy matching. */
public final class SpeakerName {
    private static final Pattern FORMAT = Pattern.compile("§[0-9a-fk-or]", Pattern.CASE_INSENSITIVE);
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern SPACE = Pattern.compile("\\s+");
    private static String key(String name) {
        return SPACE.matcher(MARKS.matcher(Normalizer.normalize(FORMAT.matcher(name).replaceAll(""), Normalizer.Form.NFD))
            .replaceAll("").toLowerCase(Locale.ROOT)).replaceAll(" ").strip();
    }
    public static boolean matches(String actual, String query) {
        return actual != null && query != null && !query.isBlank() && key(actual).contains(key(query));
    }
}
