package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/** BETA 1.0 PRE4: unified 2D/3D pilosity selector with cultural beard catalogue. */
public final class UnifiedPilositySupport {
    private static final String[] COLOR_IDS = {
            "brown", "black", "brown", "chestnut", "blond", "light_blond", "dark_blond",
            "red", "bright_red", "auburn", "gray", "white", "silver"
    };
    private static final String[] COLOR_LABELS = {
            "Liée aux cheveux", "Noir", "Brun", "Châtain", "Blond", "Blond clair", "Blond foncé",
            "Roux", "Roux vif", "Auburn", "Gris", "Blanc", "Argenté"
    };

    private static final String[] HUMAN_3D = {"trimmed", "noble_goatee", "long"};
    private static final String[] NORDIC_3D = {"short", "full", "long", "wild", "split"};
    private static final String[] DWARF_3D = {"short", "full", "long", "forked", "braided", "double_braid", "triple_braid", "runic", "segmented"};

    private UnifiedPilositySupport() {}

    public static void fix(Object screen) {
        if (screen == null) return;
        try {
            Object race = get(screen, "race");
            boolean human = named(race, "HUMAN");
            boolean nordic = named(race, "NORDIC");
            boolean dwarf = named(race, "DWARF");
            boolean bearded = human || nordic || dwarf;
            Object legacyBeardColor = beardColorButton(screen);
            setVisibleActive(legacyBeardColor, false, false);

            if (!bearded) return;

            int facial = intValue(screen, "nexuscharacters$facialHair", 0);
            String raw = stringValue(screen, "nexuscharacters$beardStyle", "none");
            String beard = CulturalBeardSupport.baseStyle(raw);

            if (dwarf) {
                facial = 0;
            } else {
                facial = clamp(facial, 0, 6);
            }
            if (facial > 0) beard = "none";
            if (!CulturalBeardSupport.styleAllowed(race, beard)) beard = "none";

            setInt(screen, "nexuscharacters$facialHair", facial);
            if ("none".equals(beard)) setString(screen, "nexuscharacters$beardStyle", "none");

            Object unified = get(screen, "nexuscharacters$facialHairButton");
            setVisibleActive(unified, true, true);
            setText(unified, "Pilosité : " + currentLabel(facial, beard));

            boolean selected = facial > 0 || !"none".equals(beard);
            Object color = get(screen, "nexuscharacters$facialColorButton");
            setVisibleActive(color, true, selected);
            int colorIndex = clamp(intValue(screen, "nexuscharacters$facialColor", 0), 0, 12);
            setText(color, selected ? "Couleur : " + COLOR_LABELS[colorIndex] : "Couleur : —");

            syncColor(screen);
            CulturalBeardUiSupport.refreshCosmeticButton(screen);
        } catch (Throwable ignored) {}
    }

    public static void cycle(Object screen) {
        if (screen == null) return;
        try {
            Object race = get(screen, "race");
            boolean human = named(race, "HUMAN");
            boolean nordic = named(race, "NORDIC");
            boolean dwarf = named(race, "DWARF");
            if (!(human || nordic || dwarf)) return;

            int facial = clamp(intValue(screen, "nexuscharacters$facialHair", 0), 0, 6);
            String raw = stringValue(screen, "nexuscharacters$beardStyle", "none");
            String beard = CulturalBeardSupport.baseStyle(raw);
            String ornament = CulturalBeardSupport.ornamentId(raw);

            if ((human || nordic) && facial > 0) {
                if (facial < 6) {
                    facial++;
                    beard = "none";
                } else {
                    facial = 0;
                    beard = human ? HUMAN_3D[0] : NORDIC_3D[0];
                }
            } else if (!"none".equals(beard)) {
                String[] seq = human ? HUMAN_3D : nordic ? NORDIC_3D : DWARF_3D;
                int idx = indexOf(seq, beard);
                if (idx >= 0 && idx + 1 < seq.length) beard = seq[idx + 1];
                else beard = "none";
                facial = 0;
            } else {
                if (human || nordic) facial = 1;
                else beard = DWARF_3D[0];
            }

            if (!CulturalBeardSupport.ornamentAllowed(race, beard, ornament)) ornament = "none";
            setInt(screen, "nexuscharacters$facialHair", facial);
            if ("none".equals(beard)) setString(screen, "nexuscharacters$beardStyle", "none");
            else setString(screen, "nexuscharacters$beardStyle", CulturalBeardSupport.compose(beard, selectedColorId(screen), ornament));
            syncColor(screen);
            fix(screen);
        } catch (Throwable ignored) {}
    }

    public static void syncColor(Object screen) {
        if (screen == null) return;
        try {
            String color = selectedColorId(screen);
            Field f = CosmeticColorSupport.class.getDeclaredField("SCREEN_COLORS");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Object,String> map = (Map<Object,String>) f.get(null);
            map.put(screen, color);

            int facial = clamp(intValue(screen, "nexuscharacters$facialHair", 0), 0, 6);
            if (facial == 0) {
                String raw = stringValue(screen, "nexuscharacters$beardStyle", "none");
                String style = CulturalBeardSupport.baseStyle(raw);
                if (!"none".equals(style)) {
                    Object race = get(screen, "race");
                    String ornament = CulturalBeardSupport.ornamentId(raw);
                    if (!CulturalBeardSupport.ornamentAllowed(race, style, ornament)) ornament = "none";
                    setString(screen, "nexuscharacters$beardStyle", CulturalBeardSupport.compose(style, color, ornament));
                }
            }
        } catch (Throwable ignored) {}
    }

    public static String selectedColorId(Object screen) {
        int facialColor = clamp(intValue(screen, "nexuscharacters$facialColor", 0), 0, 12);
        int hairColor = clamp(intValue(screen, "nexuscharacters$hairColor", 2), 0, 12);
        int index = facialColor == 0 ? hairColor : facialColor;
        if (index <= 0 || index >= COLOR_IDS.length) index = 2;
        return COLOR_IDS[index];
    }

    private static String currentLabel(int facial, String beard) {
        if (facial > 0) {
            return switch (facial) {
                case 1 -> "Barbe de trois jours";
                case 2 -> "Moustache courte";
                case 3 -> "Moustache épaisse";
                case 4 -> "Moustache mince";
                case 5 -> "Barbe mince 1";
                case 6 -> "Barbe mince 2";
                default -> "Aucune";
            };
        }
        return CulturalBeardSupport.styleLabel(beard);
    }

    private static boolean named(Object race, String name) { return race instanceof Enum<?> e && name.equals(e.name()); }
    private static int indexOf(String[] seq, String value) { for (int i=0;i<seq.length;i++) if (seq[i].equals(value)) return i; return -1; }

    private static Object beardColorButton(Object screen) {
        try {
            Field f = CosmeticColorSupport.class.getDeclaredField("SCREEN_BUTTONS");
            f.setAccessible(true);
            Object v = f.get(null);
            if (v instanceof Map<?,?> map) return map.get(screen);
        } catch (Throwable ignored) {}
        return null;
    }

    static Object get(Object o, String name) {
        try { Field f=findField(o.getClass(),name); if(f==null)return null; f.setAccessible(true); return f.get(o); }
        catch(Throwable ignored){ return null; }
    }
    static void setInt(Object o,String name,int value){ try{Field f=findField(o.getClass(),name); if(f!=null){f.setAccessible(true);f.setInt(o,value);}}catch(Throwable ignored){} }
    static void setString(Object o,String name,String value){ try{Field f=findField(o.getClass(),name); if(f!=null){f.setAccessible(true);f.set(o,value);}}catch(Throwable ignored){} }
    static int intValue(Object o,String name,int def){ Object v=get(o,name); return v instanceof Number n?n.intValue():def; }
    static String stringValue(Object o,String name,String def){ Object v=get(o,name); return v==null?def:String.valueOf(v); }
    static Field findField(Class<?> c,String name){ for(Class<?> k=c;k!=null;k=k.getSuperclass())try{return k.getDeclaredField(name);}catch(NoSuchFieldException ignored){} return null; }

    static void setVisibleActive(Object button, boolean visible, boolean active) {
        if (button == null) return;
        try { Field f=findField(button.getClass(),"field_22763"); if(f!=null){f.setAccessible(true);f.setBoolean(button,visible);} } catch(Throwable ignored){}
        try { Field f=findField(button.getClass(),"field_22764"); if(f!=null){f.setAccessible(true);f.setBoolean(button,active);} } catch(Throwable ignored){}
    }

    static void setText(Object button, String text) {
        if (button == null) return;
        try {
            Class<?> textClass = Class.forName("net.minecraft.class_2561");
            Method literal = textClass.getMethod("method_43470", String.class);
            Object component = literal.invoke(null, text);
            Method setter = null;
            for (Method m : button.getClass().getMethods()) {
                if (m.getName().equals("method_25355") && m.getParameterCount()==1) { setter=m; break; }
            }
            if (setter != null) setter.invoke(button, component);
        } catch (Throwable ignored) {}
    }

    private static int clamp(int v,int lo,int hi){ return Math.max(lo,Math.min(hi,v)); }
}
