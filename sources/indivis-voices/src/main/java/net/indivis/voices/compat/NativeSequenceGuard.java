package net.indivis.voices.compat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Pattern;

/** Repairs only the audited core 1.5.7 dispatcher, once when functions are compiled. */
public final class NativeSequenceGuard {
    public static final String FUNCTION = "capitale:quest/dialogue/sequence/dispatch_self";
    public static final String ORIGINAL_SHA256 = "7c2fa10c295379b9bab0ac4e5157ebe916a42a30d070408d7f57a3859acc6b4a";
    public static final String GUARDED_SHA256 = "721693a18c5552af0a372d857b3dcb2ca4389abe0fdd2e4156c8c80171721073";
    private static final Pattern BRANCH = Pattern.compile(
        "^(execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches \\d+ run )function (capitale:\\S+)$");

    private NativeSequenceGuard() {}

    public static String fingerprint(List<String> lines) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(String.join("\n", lines).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }

    /** Same list for every other ID/revision, including our already guarded input. */
    public static List<String> guard(String function, List<String> lines) {
        if (!FUNCTION.equals(function) || !ORIGINAL_SHA256.equals(fingerprint(lines))) return lines;
        var guarded = new ArrayList<String>(lines.size());
        for (var line : lines) {
            var branch = BRANCH.matcher(line);
            guarded.add(branch.matches() ? branch.group(1) + "return run function " + branch.group(2) : line);
        }
        if (!GUARDED_SHA256.equals(fingerprint(guarded))) {
            throw new IllegalStateException("Audited dispatcher rewrite does not match its expected fingerprint");
        }
        return List.copyOf(guarded);
    }
}
