package net.hautecapitale.spawns.api;

/** Le resultat d'une operation d'administration : reussite et message lisible. */
public record OpResult(boolean ok, String message) {

    public static OpResult ok(String message) {
        return new OpResult(true, message);
    }

    public static OpResult fail(String message) {
        return new OpResult(false, message);
    }
}
