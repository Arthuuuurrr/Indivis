package net.indivis.voices.session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** One bounded handover from a consumed voice to the next native text. */
public final class DialogueContinuation<T> {
    private final T spoken;
    private final int session;
    private final UUID npc;
    private final long deadlineMs;
    private boolean finished;

    public DialogueContinuation(T spoken, int session, UUID npc, long nowMs) {
        this.spoken = spoken; this.session = session; this.npc = npc;
        deadlineMs = nowMs + 5_000;
    }

    /** Identity matters: a repeated phrase captured after a choice is new text. */
    public Optional<List<T>> poll(int session, UUID npc, List<T> captured, long nowMs, boolean historyVisible) {
        if (finished) return Optional.empty();
        if (nowMs > deadlineMs || this.session != session || !this.npc.equals(npc) ||
                captured.isEmpty() || captured.getFirst() != spoken) {
            finished = true;
            return Optional.empty();
        }
        if (historyVisible || captured.size() == 1) return Optional.empty();
        finished = true;
        return Optional.of(List.copyOf(captured.subList(1, captured.size())));
    }

    public boolean finished() { return finished; }
}
