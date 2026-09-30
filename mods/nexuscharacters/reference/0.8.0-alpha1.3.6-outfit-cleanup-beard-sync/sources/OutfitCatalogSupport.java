package net.tompsen.nexuscharacters;

public final class OutfitCatalogSupport {
    private static final int[] ALLOWED = {
        1,3,4,5,6,7,10,13,14,15,16,17,18,19,20,
        21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38
    };
    private OutfitCatalogSupport() {}
    public static int count() { return ALLOWED.length; }
    public static boolean isAllowed(int actual) {
        for (int v : ALLOWED) if (v == actual) return true;
        return false;
    }
    public static int normalize(int actual) { return isAllowed(actual) ? actual : ALLOWED[0]; }
    public static int next(int actual) {
        int v = normalize(actual);
        for (int i=0;i<ALLOWED.length;i++) if (ALLOWED[i]==v) return ALLOWED[(i+1)%ALLOWED.length];
        return ALLOWED[0];
    }
    public static int position(int actual) {
        int v = normalize(actual);
        for (int i=0;i<ALLOWED.length;i++) if (ALLOWED[i]==v) return i+1;
        return 1;
    }
    public static String label(int actual) { return "Tenue " + position(actual) + "/" + ALLOWED.length; }
}
