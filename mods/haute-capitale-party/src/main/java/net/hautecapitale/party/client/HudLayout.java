package net.hautecapitale.party.client;

import net.hautecapitale.party.network.HudPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * La mise en page du HUD, sans un seul appel de dessin.
 *
 * <p>Cette classe transforme un instantane en une liste de rectangles et
 * d'etiquettes, dans un repere a l'echelle 1 dont l'origine est le coin haut-gauche
 * du bloc. Le rendu se contente ensuite de translater, mettre a l'echelle et
 * dessiner. Deux raisons a cette separation :
 * <ul>
 *   <li>tout ce qui peut se tromper — alignement, couleurs, cas mort et hors
 *       ligne — est ici, et se verifie hors du jeu, sans fenetre ;</li>
 *   <li>la seule dependance au moteur est la mesure de largeur de texte, injectee
 *       en fonction, donc remplacable par une regle fixe en test.</li>
 * </ul>
 *
 * <p><b>Couleurs.</b> Toutes sont ecrites avec leur alpha : en 1.21.11,
 * {@code drawText} ne dessine <em>rien</em> pour une couleur dont l'alpha vaut zero,
 * sans erreur ni avertissement. Un {@code 0xRRGGBB} oublie donne un HUD sans texte.
 * {@link #opaque(int)} rend ce piege impossible ici.
 */
public final class HudLayout {

    // --- geometrie a l'echelle 1 ---------------------------------------------

    public static final int WIDTH = 132;
    public static final int PADDING = 3;
    public static final int ROW_TEXT = 9;
    public static final int BAR_HEIGHT = 9;
    public static final int MEMBER_HEIGHT = ROW_TEXT + 1 + BAR_HEIGHT + 3;
    public static final int HEADER_HEIGHT = 11;
    private static final int INNER = WIDTH - 2 * PADDING;

    // --- couleurs, toutes opaques sauf le fond -------------------------------

    public static final int BACKGROUND = 0x8C000000;
    public static final int SELF_ROW = 0x30FFFFFF;
    public static final int BAR_BACK = opaque(0x262626);
    public static final int BAR_HIGH = opaque(0x3FB950);
    public static final int BAR_MID = opaque(0xD4A017);
    public static final int BAR_LOW = opaque(0xD23A3A);
    public static final int BAR_DEAD = opaque(0x4A4A4A);
    public static final int TEXT = opaque(0xFFFFFF);
    public static final int TEXT_SOFT = opaque(0xB8B8B8);
    public static final int TEXT_DIM = opaque(0x707070);
    public static final int TEXT_HEADER = opaque(0x7FD7DD);
    public static final int LEADER = opaque(0xFFD700);
    public static final int DEAD = opaque(0xE06060);
    public static final int ROLE_TANK = opaque(0xE8A317);
    public static final int ROLE_HEAL = opaque(0x5CD65C);
    public static final int ROLE_DPS = opaque(0xE05252);

    /** Force l'alpha a 255. Le seul chemin par lequel une couleur entre dans ce fichier. */
    public static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    // --- primitives ----------------------------------------------------------

    public record Rect(int x1, int y1, int x2, int y2, int argb) {
    }

    public record Label(String text, int x, int y, int argb, boolean shadow) {
    }

    /** Le resultat : ou poser le bloc, a quelle echelle, et quoi dessiner dedans. */
    public record Layout(float originX, float originY, float scale, int width, int height,
                         List<Rect> rects, List<Label> labels) {
        public boolean isEmpty() {
            return this.rects.isEmpty() && this.labels.isEmpty();
        }
    }

    private HudLayout() {
    }

    // --- construction --------------------------------------------------------

    /**
     * @param textWidth mesure de largeur d'une chaine, en pixels a l'echelle 1
     */
    public static Layout build(HudPayload payload, HudSettings settings,
                               int screenWidth, int screenHeight, ToIntFunction<String> textWidth) {
        List<HudPayload.Member> members = new ArrayList<>();
        for (HudPayload.Member member : payload.members()) {
            if (settings.hideSelf && member.self()) {
                continue;
            }
            members.add(member);
        }
        if (members.isEmpty()) {
            return new Layout(0, 0, settings.scale, 0, 0, List.of(), List.of());
        }

        int height = PADDING + HEADER_HEIGHT + members.size() * MEMBER_HEIGHT + PADDING - 3;
        List<Rect> rects = new ArrayList<>();
        List<Label> labels = new ArrayList<>();

        rects.add(new Rect(0, 0, WIDTH, height, BACKGROUND));

        labels.add(new Label(headerText(payload), PADDING, PADDING, TEXT_HEADER, true));

        int y = PADDING + HEADER_HEIGHT;
        for (HudPayload.Member member : members) {
            layoutMember(member, y, rects, labels, textWidth, settings);
            y += MEMBER_HEIGHT;
        }

        float scale = settings.scale;
        float scaledW = WIDTH * scale;
        float scaledH = height * scale;
        float originX;
        float originY;
        switch (settings.anchorValue()) {
            case TOP_RIGHT -> {
                originX = screenWidth - settings.offsetX - scaledW;
                originY = settings.offsetY;
            }
            case BOTTOM_LEFT -> {
                originX = settings.offsetX;
                originY = screenHeight - settings.offsetY - scaledH;
            }
            case BOTTOM_RIGHT -> {
                originX = screenWidth - settings.offsetX - scaledW;
                originY = screenHeight - settings.offsetY - scaledH;
            }
            default -> {
                originX = settings.offsetX;
                originY = settings.offsetY;
            }
        }
        return new Layout(originX, originY, scale, WIDTH, height, List.copyOf(rects), List.copyOf(labels));
    }

    private static String headerText(HudPayload payload) {
        int online = 0;
        for (HudPayload.Member member : payload.members()) {
            if (member.online()) {
                online++;
            }
        }
        return "Groupe  " + online + "/" + payload.members().size() + " en ligne";
    }

    private static void layoutMember(HudPayload.Member member, int y, List<Rect> rects, List<Label> labels,
                                     ToIntFunction<String> textWidth, HudSettings settings) {
        int left = PADDING;
        int right = WIDTH - PADDING;

        if (member.self()) {
            rects.add(new Rect(1, y - 1, WIDTH - 1, y + MEMBER_HEIGHT - 3, SELF_ROW));
        }

        // --- rangee 1 : chef, role, nom | classe et niveau ---
        int x = left;
        int nameColor = member.online() ? (member.alive() ? TEXT : DEAD) : TEXT_DIM;

        if (member.leader()) {
            labels.add(new Label("♛", x, y, LEADER, true));
            x += textWidth.applyAsInt("♛") + 2;
        }
        String roleSymbol = roleSymbol(member.role());
        if (!roleSymbol.isEmpty()) {
            labels.add(new Label(roleSymbol, x, y, roleColor(member.role()), true));
            x += textWidth.applyAsInt(roleSymbol) + 2;
        }

        String detail = "";
        if (settings.showClassAndLevel && member.online()) {
            detail = classAndLevel(member);
        }
        int detailWidth = detail.isEmpty() ? 0 : textWidth.applyAsInt(detail);
        int nameRoom = right - x - (detailWidth > 0 ? detailWidth + 4 : 0);
        String name = fit(member.name(), nameRoom, textWidth);
        labels.add(new Label(name, x, y, nameColor, true));
        if (!detail.isEmpty()) {
            labels.add(new Label(detail, right - detailWidth, y, TEXT_SOFT, true));
        }

        // --- rangee 2 : barre de vie | distance ---
        int barY = y + ROW_TEXT + 1;
        int barBottom = barY + BAR_HEIGHT;

        if (!member.online()) {
            labels.add(new Label("hors ligne", left, barY + 1, TEXT_DIM, false));
            return;
        }

        rects.add(new Rect(left, barY, right, barBottom, BAR_BACK));
        float ratio = member.maxHealth() > 0 ? Math.clamp(member.health() / member.maxHealth(), 0f, 1f) : 0f;
        if (member.alive() && ratio > 0) {
            int fill = Math.max(1, Math.round(INNER * ratio));
            rects.add(new Rect(left, barY, left + fill, barBottom, barColor(ratio)));
        } else if (!member.alive()) {
            rects.add(new Rect(left, barY, right, barBottom, BAR_DEAD));
        }

        String hp = member.alive()
                ? formatHealth(member.health()) + " / " + formatHealth(member.maxHealth())
                : "MORT";
        labels.add(new Label(hp, left + 2, barY + 1, member.alive() ? TEXT : DEAD, true));

        if (settings.showDistance && !member.self()) {
            String distance = distanceText(member.distance());
            if (!distance.isEmpty()) {
                labels.add(new Label(distance, right - 2 - textWidth.applyAsInt(distance), barY + 1, TEXT_SOFT, true));
            }
        }
    }

    // --- petits outils, publics pour etre testes ----------------------------

    public static int barColor(float ratio) {
        if (ratio > 0.5f) {
            return BAR_HIGH;
        }
        return ratio > 0.25f ? BAR_MID : BAR_LOW;
    }

    /** Sans decimale inutile : « 20 » plutot que « 20.0 », « 7.5 » quand ca compte. */
    public static String formatHealth(float value) {
        if (value == Math.floor(value)) {
            return Integer.toString((int) value);
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    public static String distanceText(int distance) {
        if (distance < 0) {
            return "ailleurs";
        }
        if (distance == 0) {
            return "";
        }
        return distance + " m";
    }

    public static String classAndLevel(HudPayload.Member member) {
        String className = member.className() == null ? "" : member.className();
        if (member.level() >= 0) {
            return className.isEmpty() ? "niv. " + member.level() : className + " " + member.level();
        }
        return className;
    }

    /** Symboles alignes sur ceux de {@code Role} : ordinal 0 = aucun, 1 tank, 2 heal, 3 dps. */
    public static String roleSymbol(byte role) {
        return switch (role) {
            case 1 -> "■";
            case 2 -> "✚";
            case 3 -> "⚔";
            default -> "";
        };
    }

    public static int roleColor(byte role) {
        return switch (role) {
            case 1 -> ROLE_TANK;
            case 2 -> ROLE_HEAL;
            case 3 -> ROLE_DPS;
            default -> TEXT_SOFT;
        };
    }

    /** Tronque avec une ellipse quand le nom deborde de la place qui lui reste. */
    public static String fit(String text, int room, ToIntFunction<String> textWidth) {
        if (room <= 0 || text == null) {
            return "";
        }
        if (textWidth.applyAsInt(text) <= room) {
            return text;
        }
        String ellipsis = "…";
        int ellipsisWidth = textWidth.applyAsInt(ellipsis);
        StringBuilder cut = new StringBuilder(text);
        while (cut.length() > 0 && textWidth.applyAsInt(cut.toString()) + ellipsisWidth > room) {
            cut.setLength(cut.length() - 1);
        }
        return cut.isEmpty() ? "" : cut + ellipsis;
    }
}
