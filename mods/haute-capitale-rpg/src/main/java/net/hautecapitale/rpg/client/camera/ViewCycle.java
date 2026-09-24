package net.hautecapitale.rpg.client.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;

/**
 * Le cycle de la touche F5, vues vanilla et styles RPG réunis.
 *
 * <p>Le joueur n'a qu'une touche à connaître. F5 parcourt, dans cet ordre :
 * première personne vanilla, troisième personne vanilla, vue de face vanilla,
 * puis chaque style RPG, puis retour à la première personne. Les trois vues
 * vanilla restent <b>strictement</b> vanilla — la caméra RPG ne s'y applique
 * pas — et les styles RPG sont des positions à part entière du cycle.
 *
 * <p><b>Comment F5 est interceptée sans mixin.</b> Le jeu traite ses touches
 * dans {@code MinecraftClient#handleInputEvents}, appelé depuis {@code tick()}.
 * L'évènement {@code START_CLIENT_TICK} de Fabric API se déclenche en tête de ce
 * même {@code tick()} : en y consommant les appuis de F5 par {@code wasPressed()},
 * qui décrémente le compteur, le jeu n'en voit plus aucun et ne bascule rien
 * lui-même. Aucune classe du jeu n'est modifiée pour cela.
 *
 * <p><b>Ce que le jeu fait sur F5, et qui est reproduit à l'identique.</b>
 * Vérifié dans le bytecode 1.21.11 : {@code setPerspective}, puis
 * {@code GameRenderer#onCameraEntitySet} si l'on entre ou sort de la première
 * personne, puis {@code WorldRenderer#scheduleTerrainUpdate}. Omettre l'un des
 * deux laisserait la main du joueur ou le terrain dans l'état de la vue
 * précédente.
 *
 * <p>La position courante est <i>déduite</i> de la perspective réelle et non
 * mémorisée seule : si une cinématique ou un dialogue a mis la vue en première
 * personne entre-temps, le prochain F5 repart de la première personne, comme le
 * joueur s'y attend.
 */
public final class ViewCycle {

    /** Une position du cycle. */
    public enum Position {
        VANILLA_FIRST("1re personne"),
        VANILLA_BACK("3e personne"),
        VANILLA_FRONT("vue de face"),
        RPG("RPG");

        private final String libelle;

        Position(String libelle) {
            this.libelle = libelle;
        }

        public String libelle() {
            return this.libelle;
        }
    }

    /** Une étape du cycle : la position, et l'index du style si elle est RPG. */
    public record Etape(Position position, int indexStyle) {
    }

    private static boolean rpgSelectionne = false;
    private static int indexStyle = 0;

    private ViewCycle() {
    }

    // ------------------------------------------------------------------------
    // Logique pure, testable hors jeu
    // ------------------------------------------------------------------------

    /**
     * L'étape qui suit.
     *
     * @param courante      l'étape où l'on est
     * @param nbStyles      nombre de styles RPG configurés
     * @param rpgDisponible {@code false} si le module est coupé : le cycle se
     *                      referme alors sur les trois vues vanilla
     */
    public static Etape suivante(Etape courante, int nbStyles, boolean rpgDisponible) {
        boolean stylesPossibles = rpgDisponible && nbStyles > 0;
        return switch (courante.position()) {
            case VANILLA_FIRST -> new Etape(Position.VANILLA_BACK, courante.indexStyle());
            case VANILLA_BACK -> new Etape(Position.VANILLA_FRONT, courante.indexStyle());
            case VANILLA_FRONT -> stylesPossibles
                    ? new Etape(Position.RPG, 0)
                    : new Etape(Position.VANILLA_FIRST, courante.indexStyle());
            case RPG -> (stylesPossibles && courante.indexStyle() + 1 < nbStyles)
                    ? new Etape(Position.RPG, courante.indexStyle() + 1)
                    : new Etape(Position.VANILLA_FIRST, 0);
        };
    }

    /** La perspective vanilla que demande une position. */
    public static Perspective perspectiveDe(Position position) {
        return switch (position) {
            case VANILLA_FIRST -> Perspective.FIRST_PERSON;
            case VANILLA_FRONT -> Perspective.THIRD_PERSON_FRONT;
            case VANILLA_BACK, RPG -> Perspective.THIRD_PERSON_BACK;
        };
    }

    /**
     * Déduit la position courante de la perspective réelle et du drapeau RPG.
     *
     * <p>Le drapeau seul ne suffit pas : Bosses'Rise force la première personne
     * pendant ses cinématiques, un dialogue peut en faire autant, et le joueur
     * doit toujours pouvoir lire la position au cycle depuis ce qu'il voit.
     */
    public static Position courante(Perspective perspective, boolean rpgSelectionne) {
        if (perspective.isFirstPerson()) {
            return Position.VANILLA_FIRST;
        }
        if (rpgSelectionne) {
            return Position.RPG;
        }
        return perspective.isFrontView() ? Position.VANILLA_FRONT : Position.VANILLA_BACK;
    }

    // ------------------------------------------------------------------------
    // Branchement en jeu
    // ------------------------------------------------------------------------

    /**
     * À appeler en tête de tick client, avant que le jeu ne lise ses touches.
     *
     * <p>Pendant une cinématique ou un dialogue, les appuis sont consommés mais
     * ignorés : quelqu'un d'autre tient la caméra, et laisser le jeu basculer la
     * perspective sous lui provoquerait exactement la dispute qu'on veut éviter.
     */
    static void tickDebut(MinecraftClient client) {
        if (client.options == null) {
            return;
        }
        KeyBinding f5 = client.options.togglePerspectiveKey;
        traiter(client, f5::wasPressed);
    }

    /** Même traitement, avec des appuis synthétiques. Pour l'autotest en jeu. */
    public static void tickDebutPourTest(MinecraftClient client, int appuis) {
        int[] restant = {appuis};
        traiter(client, () -> restant[0]-- > 0);
    }

    private static void traiter(MinecraftClient client, java.util.function.BooleanSupplier appui) {
        CameraSettings s = CameraSettings.get();
        if (!s.f5_cycle || !s.actif) {
            // Cycle vanilla : on ne consomme rien, le jeu gere F5 lui-meme.
            return;
        }
        boolean bloque = CameraOverrideManager.isCinematic() || CameraFocus.estEngage()
                || client.player == null;
        while (appui.getAsBoolean()) {
            if (!bloque) {
                avancer(client);
            }
        }
    }

    /** Avance d'une position. Public pour l'autotest en jeu. */
    public static void avancer(MinecraftClient client) {
        CameraSettings s = CameraSettings.get();
        Perspective avant = client.options.getPerspective();
        Etape courante = new Etape(courante(avant, rpgSelectionne), indexStyle);
        Etape apres = suivante(courante, s.styles.size(), s.actif);
        appliquer(client, apres, avant);
    }

    /** Sélectionne directement un style RPG. Public pour l'autotest en jeu. */
    public static void selectionnerRpg(MinecraftClient client, int index) {
        CameraSettings s = CameraSettings.get();
        int borne = Math.max(0, Math.min(index, s.styles.size() - 1));
        appliquer(client, new Etape(Position.RPG, borne), client.options.getPerspective());
    }

    private static void appliquer(MinecraftClient client, Etape etape, Perspective avant) {
        rpgSelectionne = etape.position() == Position.RPG;
        indexStyle = etape.indexStyle();

        Perspective apres = perspectiveDe(etape.position());
        if (apres != avant) {
            client.options.setPerspective(apres);
            // Les deux effets de bord du F5 vanilla, dans le meme ordre.
            if (avant.isFirstPerson() != apres.isFirstPerson()) {
                client.gameRenderer.onCameraEntitySet(apres.isFirstPerson() ? client.getCameraEntity() : null);
            }
            client.worldRenderer.scheduleTerrainUpdate();
        }

        if (rpgSelectionne && client.inGameHud != null) {
            // Un mot dans la barre d'action : le joueur sait quel style il vient de
            // choisir sans ouvrir l'overlay de debug. Les vues vanilla n'affichent
            // rien, comme vanilla.
            client.inGameHud.setOverlayMessage(
                    Text.literal("Caméra : " + styleActif().nom), false);
        }
    }

    // ------------------------------------------------------------------------
    // Lecture
    // ------------------------------------------------------------------------

    /**
     * La caméra RPG a-t-elle sa place à l'écran en ce moment ?
     *
     * <p>Cycle F5 actif : seulement sur une position RPG. Cycle coupé : dès que la
     * vue est en troisième personne, comme avant l'existence du cycle.
     */
    public static boolean rpgSelectionne() {
        return !CameraSettings.get().f5_cycle || rpgSelectionne;
    }

    /** Le style RPG courant — celui que le zoom et l'overlay manipulent. */
    public static CameraSettings.Style styleActif() {
        java.util.List<CameraSettings.Style> styles = CameraSettings.get().styles;
        if (styles == null || styles.isEmpty()) {
            return new CameraSettings.Style();
        }
        return styles.get(Math.max(0, Math.min(indexStyle, styles.size() - 1)));
    }

    public static int indexStyle() {
        return indexStyle;
    }

    /** Libellé de la position courante, pour l'overlay de debug. */
    public static String affichage(Perspective perspective) {
        CameraSettings s = CameraSettings.get();
        if (!s.f5_cycle) {
            return "cycle vanilla (F5 non intercepte)";
        }
        Position position = courante(perspective, rpgSelectionne);
        if (position == Position.RPG) {
            return "RPG · " + styleActif().nom + " (" + (indexStyle + 1) + "/" + s.styles.size() + ")";
        }
        return position.libelle();
    }
}
