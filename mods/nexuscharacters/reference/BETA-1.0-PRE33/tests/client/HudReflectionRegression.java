import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

/** Exercise the patched private lookup on the real HUD class, retaining its invocation behavior. */
public final class HudReflectionRegression {
    interface Named { default int value() { return 41; } }
    static class Parent { private int secret() { return 7; } public int inherited() { return 13; } }
    static class Child extends Parent implements Named {
        int dynamic = 2; private int fixture = 12; static int shared = 6;
        public int score() { return dynamic; }
        public int score(int n) { return n; }
        private int hidden() { return 5; }
        public int fails() { throw new IllegalStateException("fixture"); }
    }
    static int checks;
    static void check(boolean v, String n) { checks++; if (!v) throw new AssertionError(n); }
    public static void run() throws Exception {
        Class<?> hud = Class.forName("fr.arthur.capitale.rphud.CapitaleRpHudClient");
        Method find = hud.getDeclaredMethod("findNoArg", Class.class, String.class); find.setAccessible(true);
        Method invoke = hud.getDeclaredMethod("invokeInt", Object.class, int.class, String[].class); invoke.setAccessible(true);
        Child child = new Child();
        check(find.invoke(null,null,"absent") == null,"Null class fallback changed");
        check(find.invoke(null,Child.class,null) == null,"Null name fallback changed");
        for (String n : List.of("score", "hidden", "secret", "inherited", "value")) {
            Method method = (Method) find.invoke(null, Child.class, n);
            check(method != null && method.getParameterCount() == 0, "HUD failed " + n);
            method.setAccessible(true); check(method.invoke(child) instanceof Number, "HUD invocation failed " + n);
        }
        for (int i = 0; i < 500; i++) {
            child.dynamic = i;
            check(((Number) invoke.invoke(null, child, -1, new String[]{"missing", "score"})).intValue() == i,
                    "HUD cached a score value");
            check(find.invoke(null, Child.class, "missing") == null, "Missing method resolved");
        }
        check(((Number) invoke.invoke(null, child, -1, new String[]{"fails", "score"})).intValue() == 499,
                "Exception fallback changed");
        Class<?> joint=Class.forName("net.tompsen.nexuscharacters.LowerJointSupport");
        Method field=joint.getDeclaredMethod("field",Object.class,String.class),set=joint.getDeclaredMethod("set",Object.class,String.class,Object.class);
        field.setAccessible(true);set.setAccessible(true);Child another=new Child();
        for(int i=0;i<500;i++){
            set.invoke(null,child,"fixture",i);
            check(((Number)field.invoke(null,child,"fixture")).intValue()==i,"Joint lookup cached instance values");
            check(((Number)field.invoke(null,another,"fixture")).intValue()==12,"Joint cache mixed two objects");
            try{field.invoke(null,child,"absent_optional");throw new AssertionError("Missing joint field resolved");}
            catch(InvocationTargetException e){check(e.getCause() instanceof NoSuchFieldException && e.getCause().getMessage().equals("absent_optional"),"Missing field exception changed");}
        }
        check(((Number)field.invoke(null,Child.class,"shared")).intValue()==6,"Static field lookup changed");
        if(!Boolean.getBoolean("pre30.baseline")){
            for(String helper:new String[]{"net.tompsen.nexuscharacters.LowerJointFieldAccess","fr.arthur.capitale.rphud.NoArgMethodCache"}){
                Class<?> type=Class.forName(helper);Field cache=type.getDeclaredField(helper.contains("LowerJoint")?"FIELDS":"METHODS");cache.setAccessible(true);
                Map<?,?> entries=(Map<?,?>)((ClassValue<?>)cache.get(null)).get(Child.class);
                check(entries.containsKey(helper.contains("LowerJoint")?"absent_optional":"missing"),"Absent lookup was not cached");
                check(entries.size()<16,"Repeated lookup grew cache");
            }
        }
        String line = "HUD33_PASS checks=" + checks + " changing_scores=500 inherited_private_interface_and_fallback=true\n";
        Path out = Path.of("pre33/qa/" + (Boolean.getBoolean("pre30.baseline") ? "baseline" : "candidate"));
        Files.createDirectories(out); Files.writeString(out.resolve("hud-reflection.txt"), line); System.out.print(line);
    }
}
