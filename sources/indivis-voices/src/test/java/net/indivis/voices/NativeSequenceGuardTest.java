package net.indivis.voices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.indivis.voices.compat.NativeSequenceGuard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NativeSequenceGuardTest {
    private static final Pattern CONDITION = Pattern.compile(
        "execute if score @s CAP_FLAG matches ([01]) if score @s CAP_QSEQ matches (\\d+) run (.*)");
    // CAP_QSEQ assignments in the supplied Géraud and Althéon native functions.
    private static final Map<Integer, Integer> NEXT = Map.of(101, 102, 102, 103, 103, 0,
        201, 202, 202, 203, 203, 0);

    private static List<String> original() throws IOException {
        try (var stream = NativeSequenceGuardTest.class.getResourceAsStream("/core-1.5.7-dispatch.mcfunction")) {
            assertNotNull(stream);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        }
    }
    private static List<String> guarded() throws IOException {
        return NativeSequenceGuard.guard(NativeSequenceGuard.FUNCTION, original());
    }
    @Test void locksTheAuditedInputAndGuardedOutput() throws IOException {
        assertEquals(NativeSequenceGuard.ORIGINAL_SHA256, NativeSequenceGuard.fingerprint(original()));
        assertEquals(NativeSequenceGuard.GUARDED_SHA256, NativeSequenceGuard.fingerprint(guarded()));
    }
    @Test void changesOnlyTheThirtyEightBranchReturns() throws IOException {
        var before = original(); var after = guarded();
        assertEquals(before.size(), after.size());
        int changed = 0;
        for (int i = 0; i < before.size(); i++) {
            if (before.get(i).equals(after.get(i))) continue;
            changed++;
            assertEquals(before.get(i).replace(" run function ", " run return run function "), after.get(i));
        }
        assertEquals(38, changed);
    }
    @Test void leavesOtherFunctionsAndModifiedDatapacksUntouched() throws IOException {
        var input = original();
        assertSame(input, NativeSequenceGuard.guard("another:dispatch_self", input));
        var modified = new ArrayList<>(input); modified.add("# Unreviewed revision");
        assertSame(modified, NativeSequenceGuard.guard(NativeSequenceGuard.FUNCTION, modified));
        var changedReward = new ArrayList<>(input); changedReward.add("give @s minecraft:diamond");
        assertSame(changedReward, NativeSequenceGuard.guard(NativeSequenceGuard.FUNCTION, changedReward));
        assertEquals(NativeSequenceGuard.ORIGINAL_SHA256, NativeSequenceGuard.fingerprint(input));
    }
    @Test void doesNotRewriteItsOwnOutputAgain() throws IOException {
        var after = guarded();
        assertSame(after, NativeSequenceGuard.guard(NativeSequenceGuard.FUNCTION, after));
    }
    @Test void reproducesTheOriginalGeraudCascadeBeforeTheFix() throws IOException {
        var result = dispatch(original(), 101);
        assertEquals(List.of(101, 102, 103), result.branches());
        assertEquals(0, result.sequence());
    }
    @Test void keepsEachGeraudLineUntilTheNextNativeDispatch() throws IOException {
        var script = guarded();
        var second = dispatch(script, 101);
        assertEquals(List.of(101), second.branches()); assertEquals(102, second.sequence());
        var third = dispatch(script, second.sequence());
        assertEquals(List.of(102), third.branches()); assertEquals(103, third.sequence());
        var objective = dispatch(script, third.sequence());
        assertEquals(List.of(103), objective.branches()); assertEquals(0, objective.sequence());
        assertTrue(dispatch(script, 0).branches().isEmpty());
    }
    @Test void altheonAndDifferentPlayersAlsoAdvanceOnlyOnce() throws IOException {
        var script = guarded();
        var a = dispatch(script, 101); var b = dispatch(script, 201);
        assertEquals(102, a.sequence()); assertEquals(202, b.sequence());
        assertEquals(List.of(201), b.branches());
        assertEquals(List.of(202), dispatch(script, b.sequence()).branches());
        assertEquals(List.of(102), dispatch(script, a.sequence()).branches());
        for (var line : script) {
            var condition = CONDITION.matcher(line);
            if (!condition.matches() || !condition.group(3).startsWith("return run function ")) continue;
            int sequence = Integer.parseInt(condition.group(2));
            assertEquals(List.of(sequence), dispatch(script, sequence).branches(), "sequence " + sequence);
        }
    }

    private record Result(int sequence, List<Integer> branches) {}
    /** Small control-flow model of the real fixture; the Fabric smoke test executes it in Minecraft. */
    private static Result dispatch(List<String> lines, int sequence) {
        int flag = -1; var executed = new ArrayList<Integer>();
        for (var line : lines) {
            if (line.equals("scoreboard players set @s CAP_FLAG 0")) { flag = 0; continue; }
            var c = CONDITION.matcher(line);
            if (!c.matches() || flag != Integer.parseInt(c.group(1)) || sequence != Integer.parseInt(c.group(2))) continue;
            var command = c.group(3);
            if (command.equals("scoreboard players set @s CAP_FLAG 1")) { flag = 1; continue; }
            assertTrue(command.startsWith("function ") || command.startsWith("return run function "));
            executed.add(sequence); sequence = NEXT.getOrDefault(sequence, sequence + 1);
            if (command.startsWith("return run ")) break;
        }
        return new Result(sequence, List.copyOf(executed));
    }
}
