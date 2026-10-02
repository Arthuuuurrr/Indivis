package net.hautecapitale.spawns.store;

import java.util.ArrayList;
import java.util.List;

/** Le bilan d'une lecture des fichiers de zones et de profils. */
public final class LoadReport {

    public int zones;
    public int points;
    public int profiles;
    public int invalidPoints;
    public final List<String> errors = new ArrayList<>();
    public final List<String> warnings = new ArrayList<>();

    public boolean ok() {
        return this.errors.isEmpty();
    }

    public String summary() {
        return this.zones + " zone(s), " + this.points + " point(s) dont " + this.invalidPoints + " invalide(s), "
                + this.profiles + " profil(s), " + this.errors.size() + " erreur(s), " + this.warnings.size() + " avertissement(s)";
    }
}
