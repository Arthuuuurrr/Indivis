package net.hautecapitale.spawns.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Le point selectionne par chaque administrateur : ce sur quoi {@code /mmospawn move},
 * {@code set}, {@code info}... agissent quand aucun identifiant n'est donne.
 */
public final class Selection {

    private static final Map<UUID, String> SELECTED = new HashMap<>();

    private Selection() {
    }

    public static void select(UUID admin, String fullId) {
        SELECTED.put(admin, fullId);
    }

    public static Optional<String> selected(UUID admin) {
        return Optional.ofNullable(SELECTED.get(admin));
    }

    public static void clear(UUID admin) {
        SELECTED.remove(admin);
    }

    /** Quand un point est renomme ou supprime, les selections qui le visaient sont ajustees. */
    public static void replace(String oldFullId, String newFullId) {
        for (Map.Entry<UUID, String> entry : SELECTED.entrySet()) {
            if (entry.getValue().equals(oldFullId)) {
                entry.setValue(newFullId);
            }
        }
    }

    public static void forget(String fullId) {
        SELECTED.values().removeIf(fullId::equals);
    }

    public static void forgetZone(String zoneId) {
        SELECTED.values().removeIf(id -> id.startsWith(zoneId + "."));
    }

    public static void clearAll() {
        SELECTED.clear();
    }
}
