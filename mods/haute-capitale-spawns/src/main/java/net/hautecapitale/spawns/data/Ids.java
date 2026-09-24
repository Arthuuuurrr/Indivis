package net.hautecapitale.spawns.data;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Identifiants des zones, des points et des profils.
 *
 * <p>Un identifiant complet de point s'ecrit {@code zone.point}, par exemple
 * {@code orc_camp_01.archer_01}. Le point (.) est le seul separateur, ce qui
 * interdit le point dans les identifiants simples.
 */
public final class Ids {

    private static final Pattern SIMPLE = Pattern.compile("[a-z0-9_-]{1,64}");

    private Ids() {
    }

    public static boolean isSimple(String id) {
        return id != null && SIMPLE.matcher(id).matches();
    }

    public static String full(String zone, String point) {
        return zone + "." + point;
    }

    /** Decoupe {@code zone.point} ; vide si la forme est mauvaise. */
    public static Optional<String[]> split(String fullId) {
        if (fullId == null) {
            return Optional.empty();
        }
        int dot = fullId.indexOf('.');
        if (dot <= 0 || dot == fullId.length() - 1 || fullId.indexOf('.', dot + 1) >= 0) {
            return Optional.empty();
        }
        String zone = fullId.substring(0, dot);
        String point = fullId.substring(dot + 1);
        if (!isSimple(zone) || !isSimple(point)) {
            return Optional.empty();
        }
        return Optional.of(new String[] {zone, point});
    }
}
