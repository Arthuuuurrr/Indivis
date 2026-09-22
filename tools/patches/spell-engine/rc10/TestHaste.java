import net.spell_engine.internals.SpellParameters;

public class TestHaste {
    static void check(float base, float haste, float expected) {
        float got = SpellParameters.hasteAffectedValue(base, haste);
        if (Float.floatToIntBits(got) != Float.floatToIntBits(expected)) {
            throw new AssertionError("base=" + base + " haste=" + haste + " got=" + got + " expected=" + expected);
        }
        System.out.println("PASS base=" + base + " haste=" + haste + " -> " + got);
    }

    public static void main(String[] args) {
        check(5f, 1f, 5f);
        check(5f, 2f, 2.5f);
        check(5f, 0.5f, 10f);
        check(5f, 0.1f, 50f);
        check(5f, 0.099f, 50f);
        check(5f, 0f, 5f);
        check(5f, -1f, 5f);
        check(5f, Float.NaN, 5f);
        check(5f, Float.POSITIVE_INFINITY, 5f);
        check(5f, Float.NEGATIVE_INFINITY, 5f);
        check(5f, Float.MIN_VALUE, 50f);
    }
}
