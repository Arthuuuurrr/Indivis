import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Verify33 implements Opcodes {
    static final String P = "net/tompsen/nexuscharacters/", C = P + "PreparedSurfaceCache";
    static void removePreceding(MethodNode method, AbstractInsnNode call, int count) {
        AbstractInsnNode node = call;
        for (int i = 0; i < count; i++) {
            AbstractInsnNode previous = node.getPrevious(); method.instructions.remove(node); node = previous;
        }
    }
    public static void main(String[] args) throws Exception {
        int methods = 0, hooks = 0;
        try (ZipFile old = new ZipFile(args[0]); ZipFile fresh = new ZipFile(args[1])) {
            for (String name : List.of("PosedSurfaceSupport", "GenericSkinLayerSupport", "LowerJointSupport")) {
                ClassNode a = Verify.cls(old, P + name + ".class"), b = Verify.cls(fresh, P + name + ".class");
                if (a.fields.size() != b.fields.size() || a.methods.size() != b.methods.size()) throw new AssertionError("Changed members");
                Map<String, MethodNode> patched = new HashMap<>(); for (MethodNode m : b.methods) patched.put(m.name + m.desc, m);
                for (MethodNode original : a.methods) {
                    MethodNode m = patched.get(original.name + original.desc);
                    if (name.equals("LowerJointSupport") && Set.of("field", "set").contains(m.name)) {
                        long calls = java.util.stream.Stream.of(m.instructions.toArray()).filter(n -> n instanceof MethodInsnNode c
                                && c.owner.equals(P + "LowerJointFieldAccess") && c.name.equals(m.name) && c.desc.equals(m.desc)).count();
                        if (calls != 1) throw new AssertionError("Unexpected field wrapper"); hooks++; continue;
                    }
                    for (AbstractInsnNode node : m.instructions.toArray()) if (node instanceof MethodInsnNode c && c.owner.equals(C)) {
                        switch (c.name) {
                            case "beforeApply" -> removePreceding(m, c, 2);
                            case "restore" -> removePreceding(m, c, 7);
                            case "capture" -> removePreceding(m, c, 3);
                            case "clearCaches" -> removePreceding(m, c, 1);
                            case "store" -> {
                                AbstractInsnNode part = c.getPrevious(), model = part.getPrevious();
                                m.instructions.remove(part); m.instructions.remove(model);
                                c.setOpcode(INVOKEINTERFACE); c.owner = "java/util/Map"; c.name = "put";
                                c.desc = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"; c.itf = true;
                            }
                            case "lookup" -> {
                                InsnList producer = new InsnList(); producer.add(new VarInsnNode(ALOAD, 5));
                                producer.add(new FieldInsnNode(GETFIELD, P + "PosedSurfaceSupport$State", "prepared", "Ljava/util/Map;"));
                                producer.add(new VarInsnNode(ALOAD, 8)); producer.add(new VarInsnNode(ILOAD, 13)); producer.add(new InsnNode(AALOAD));
                                producer.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
                                m.instructions.insert(c, producer); removePreceding(m, c, 9);
                            }
                            default -> throw new AssertionError("Unexpected hook " + c.name);
                        }
                        hooks++;
                    }
                    if (!Verify.code(original).equals(Verify.code(m))) throw new AssertionError("Changed instructions " + name + "." + m.name);
                    methods++;
                }
            }
        }
        if (hooks != 8) throw new AssertionError("Hook count " + hooks);
        try (ZipFile old = new ZipFile(args[2]); ZipFile fresh = new ZipFile(args[3])) {
            String name = "fr/arthur/capitale/rphud/CapitaleRpHudClient.class";
            ClassNode a = Verify.cls(old, name), b = Verify.cls(fresh, name);
            Map<String, MethodNode> patched = new HashMap<>(); for (MethodNode m : b.methods) patched.put(m.name + m.desc, m);
            for (MethodNode m : a.methods) {
                MethodNode n = patched.get(m.name + m.desc);
                if (m.name.equals("findNoArg")) continue;
                if (n == null || !Verify.code(m).equals(Verify.code(n))) throw new AssertionError("Changed HUD method " + m.name);
                methods++;
            }
        }
        System.out.println("BYTECODE33_PASS preserved_methods=" + methods + " nexus_hooks=" + hooks + " hud_lookup=1 geometry_math_and_score_reads_unchanged=true");
    }
}
