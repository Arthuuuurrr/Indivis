package net.tompsen.nexuscharacters;

/** Reuses the old cosmetic button as a beard-ornament selector on bearded races. */
public final class CulturalBeardUiSupport {
    private CulturalBeardUiSupport() {}

    public static void cycleCosmetic(Object screen) {
        if (screen == null) return;
        try {
            Object race = UnifiedPilositySupport.get(screen, "race");
            if (race instanceof CharacterRace cr && CharacterCosmetics.hasElfEars(cr)) {
                String raw = UnifiedPilositySupport.stringValue(screen, "nexuscharacters$earStyle", "long");
                String next = CharacterCosmetics.EarStyle.fromId(raw).next().id();
                UnifiedPilositySupport.setString(screen, "nexuscharacters$earStyle", next);
                refreshCosmeticButton(screen);
                return;
            }
            if (!isBearded(race)) return;
            int facial = UnifiedPilositySupport.intValue(screen, "nexuscharacters$facialHair", 0);
            if (facial > 0) return;
            String raw = UnifiedPilositySupport.stringValue(screen, "nexuscharacters$beardStyle", "none");
            String style = CulturalBeardSupport.baseStyle(raw);
            if ("none".equals(style)) return;
            String next = CulturalBeardSupport.nextOrnament(race, style, CulturalBeardSupport.ornamentId(raw));
            String color = UnifiedPilositySupport.selectedColorId(screen);
            UnifiedPilositySupport.setString(screen, "nexuscharacters$beardStyle", CulturalBeardSupport.compose(style, color, next));
            UnifiedPilositySupport.syncColor(screen);
            refreshCosmeticButton(screen);
        } catch (Throwable ignored) {}
    }

    public static void refreshCosmeticButton(Object screen) {
        if (screen == null) return;
        try {
            Object button = UnifiedPilositySupport.get(screen, "nexuscharacters$cosmeticButton");
            Object race = UnifiedPilositySupport.get(screen, "race");
            if (button == null || race == null) return;

            if (race instanceof CharacterRace cr && CharacterCosmetics.hasElfEars(cr)) {
                String raw = UnifiedPilositySupport.stringValue(screen, "nexuscharacters$earStyle", "long");
                CharacterCosmetics.EarStyle style = CharacterCosmetics.EarStyle.fromId(raw);
                UnifiedPilositySupport.setVisibleActive(button, true, true);
                UnifiedPilositySupport.setText(button, "Oreilles : " + style.label());
                return;
            }

            if (isBearded(race)) {
                int facial = UnifiedPilositySupport.intValue(screen, "nexuscharacters$facialHair", 0);
                String raw = UnifiedPilositySupport.stringValue(screen, "nexuscharacters$beardStyle", "none");
                String style = CulturalBeardSupport.baseStyle(raw);
                boolean show = facial == 0 && !"none".equals(style);
                UnifiedPilositySupport.setVisibleActive(button, show, show);
                if (show) {
                    UnifiedPilositySupport.setText(button, "Ornement : " + CulturalBeardSupport.ornamentLabel(CulturalBeardSupport.ornamentId(raw)));
                }
                return;
            }

            UnifiedPilositySupport.setVisibleActive(button, false, false);
        } catch (Throwable ignored) {}
    }

    private static boolean isBearded(Object race) {
        return race instanceof Enum<?> e && (e.name().equals("HUMAN") || e.name().equals("NORDIC") || e.name().equals("DWARF"));
    }
}
