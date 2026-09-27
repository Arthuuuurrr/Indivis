package net.tompsen.nexuscharacters;

public final class ResponsiveNamePreviewSupport {
    private static final String[] COLORS = {
        "&0","&1","&2","&3","&4","&5","&6","&7",
        "&8","&9","&a","&b","&c","&d","&e","&f"
    };
    private static final String[] COLOR_LABELS = {
        "Noir","Bleu f.","Vert f.","Cyan f.","Rouge f.","Violet","Or","Gris",
        "Gris f.","Bleu","Vert","Aqua","Rouge","Rose","Jaune","Blanc"
    };
    private static final String[] STYLES = {"","&l","&o","&n","&m","&k","&r"};
    private static final String[] STYLE_LABELS = {"Normal","Gras","Ital.","Soul.","Barré","Obf.","Reset"};

    private ResponsiveNamePreviewSupport() {}

    public static String colorLabel(int index) {
        int i = Math.floorMod(index, COLORS.length);
        return "C: " + COLORS[i] + COLOR_LABELS[i];
    }

    public static String styleLabel(int index) {
        int i = Math.floorMod(index, STYLES.length);
        return "S: " + STYLES[i] + STYLE_LABELS[i];
    }
}
