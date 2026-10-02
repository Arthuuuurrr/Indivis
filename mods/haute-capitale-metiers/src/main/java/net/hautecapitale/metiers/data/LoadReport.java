package net.hautecapitale.metiers.data;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ce qui s'est passé pendant un chargement de données.
 *
 * <p>Une erreur de saisie dans un fichier de données ne doit jamais empêcher le
 * serveur de démarrer, ni disparaître silencieusement. Chaque problème est
 * collecté ici avec le fichier fautif, et reste consultable en jeu tant qu'un
 * nouveau chargement n'a pas eu lieu.
 */
public final class LoadReport {

    /** Un problème rencontré sur un fichier précis. */
    public record Entry(Identifier file, String message, Severity severity) {
        @Override
        public String toString() {
            return severity.prefix + " " + file + " — " + message;
        }
    }

    public enum Severity {
        /** Le fichier est inutilisable : il a été écarté. */
        ERREUR("ERREUR"),
        /** Le fichier est chargé, mais quelque chose mérite attention. */
        AVERTISSEMENT("ATTENTION");

        private final String prefix;

        Severity(String prefix) {
            this.prefix = prefix;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private int loaded;

    void error(Identifier file, String message) {
        entries.add(new Entry(file, message, Severity.ERREUR));
    }

    void warn(Identifier file, String message) {
        entries.add(new Entry(file, message, Severity.AVERTISSEMENT));
    }

    void countLoaded() {
        loaded++;
    }

    public int loaded() {
        return loaded;
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public long errorCount() {
        return entries.stream().filter(e -> e.severity() == Severity.ERREUR).count();
    }

    public long warningCount() {
        return entries.stream().filter(e -> e.severity() == Severity.AVERTISSEMENT).count();
    }

    public boolean isClean() {
        return entries.isEmpty();
    }
}
