import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Narrow hooks into the exact PRE32 classes; original geometry instructions stay intact. */
public final class Patch33 implements Opcodes {
    static final String P = "net/tompsen/nexuscharacters/", C = P + "PreparedSurfaceCache";
    static MethodInsnNode call(String owner, String name, String desc) {
        return new MethodInsnNode(INVOKESTATIC, owner, name, desc, false);
    }
    static void write(ClassNode cls, Path dir) throws Exception {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cls.accept(writer);
        Path path = dir.resolve(cls.name + ".class"); Files.createDirectories(path.getParent());
        Files.write(path, writer.toByteArray());
    }
    static void replaceBody(MethodNode m, InsnList code) {
        m.instructions = code; m.tryCatchBlocks.clear();
        if (m.localVariables != null) m.localVariables.clear();
        m.visibleLocalVariableAnnotations = null; m.invisibleLocalVariableAnnotations = null;
    }
    public static void main(String[] args) throws Exception {
        try (ZipFile zip = new ZipFile(args[0])) {
            for (String name : List.of("PosedSurfaceSupport", "GenericSkinLayerSupport", "LowerJointSupport")) {
                ClassNode cls = new ClassNode();
                new ClassReader(zip.getInputStream(zip.getEntry(P + name + ".class"))).accept(cls, 0);
                int hooks = 0;
                for (MethodNode m : cls.methods) {
                    if (name.equals("PosedSurfaceSupport") && m.name.equals("applyPRE30")) {
                        InsnList entry = new InsnList(); entry.add(new VarInsnNode(ALOAD, 0));
                        entry.add(call(C, "beforeApply", "(Ljava/lang/Object;)V")); m.instructions.insert(entry); hooks++;
                        boolean workspace = false;
                        for (AbstractInsnNode insn : m.instructions.toArray()) {
                            if (insn instanceof MethodInsnNode c && c.owner.equals(P + "PoseScratch") && c.name.equals("prepare")) workspace = true;
                            if (workspace && insn instanceof VarInsnNode v && v.getOpcode() == ASTORE && v.var == 8) {
                                InsnList code = new InsnList();
                                for (int local : new int[]{5, 0, 6, 7, 8}) code.add(new VarInsnNode(ALOAD, local));
                                code.add(new VarInsnNode(ILOAD, 4));
                                code.add(call(C, "restore", "(L" + P + "PosedSurfaceSupport$State;Ljava/lang/Object;[Lorg/joml/Matrix4f;[Lorg/joml/Matrix4f;[Ljava/lang/Object;Z)V"));
                                m.instructions.insert(insn, code); workspace = false; hooks++;
                            }
                            if (insn instanceof FieldInsnNode f && f.owner.equals(P + "PosedSurfaceSupport$State") && f.name.equals("prepared")) {
                                AbstractInsnNode next = insn;
                                while (!(next instanceof MethodInsnNode methodCall && methodCall.owner.equals("java/util/Map") && (methodCall.name.equals("get") || methodCall.name.equals("put")))) next = next.getNext();
                                MethodInsnNode c = (MethodInsnNode) next;
                                if (c.name.equals("get")) {
                                    AbstractInsnNode start = insn.getPrevious();
                                    if (!(start instanceof VarInsnNode v && v.getOpcode() == ALOAD && v.var == 5)) throw new AssertionError("prepared.get producer");
                                    InsnList code = new InsnList();
                                    code.add(new VarInsnNode(ALOAD, 0)); code.add(new VarInsnNode(ALOAD, 8)); code.add(new VarInsnNode(ILOAD, 13)); code.add(new InsnNode(AALOAD));
                                    code.add(new VarInsnNode(ALOAD, 7)); code.add(new VarInsnNode(ILOAD, 13)); code.add(new InsnNode(AALOAD)); code.add(new VarInsnNode(ILOAD, 13));
                                    code.add(call(C, "lookup", "(Ljava/lang/Object;Ljava/lang/Object;Lorg/joml/Matrix4f;I)L" + P + "PosedSurfaceSupport$Prepared;"));
                                    m.instructions.insertBefore(start, code);
                                    AbstractInsnNode end = c.getNext();
                                    for (AbstractInsnNode n = start; n != end;) { AbstractInsnNode after = n.getNext(); m.instructions.remove(n); n = after; }
                                    hooks++;
                                } else {
                                    InsnList code = new InsnList(); code.add(new VarInsnNode(ALOAD, 0)); code.add(new VarInsnNode(ILOAD, 13));
                                    m.instructions.insertBefore(c, code);
                                    c.setOpcode(INVOKESTATIC); c.owner = C; c.name = "store";
                                    c.desc = "(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/Object;"; c.itf = false; hooks++;
                                }
                            }
                            if (insn instanceof FieldInsnNode f && f.getOpcode() == PUTSTATIC && f.owner.equals(P + name) && f.name.equals("recomputeMaxNanos")) {
                                InsnList code = new InsnList(); code.add(new VarInsnNode(ALOAD, 5)); code.add(new VarInsnNode(ALOAD, 0));
                                code.add(call(C, "capture", "(L" + P + "PosedSurfaceSupport$State;Ljava/lang/Object;)V"));
                                m.instructions.insert(insn, code); hooks++;
                            }
                        }
                    }
                    if (name.equals("GenericSkinLayerSupport") && m.name.equals("clearCaches")) {
                        m.instructions.insert(call(C, "clearCaches", "()V")); hooks++;
                    }
                    if (name.equals("LowerJointSupport") && (m.name.equals("field") || m.name.equals("set"))) {
                        InsnList code = new InsnList();
                        code.add(new VarInsnNode(ALOAD, 0)); code.add(new VarInsnNode(ALOAD, 1));
                        if (m.name.equals("set")) code.add(new VarInsnNode(ALOAD, 2));
                        code.add(call(P + "LowerJointFieldAccess", m.name, m.desc));
                        code.add(new InsnNode(m.name.equals("field") ? ARETURN : RETURN));
                        replaceBody(m, code); hooks++;
                    }
                }
                int expected = name.equals("PosedSurfaceSupport") ? 5 : name.equals("GenericSkinLayerSupport") ? 1 : 2;
                if (hooks != expected) throw new AssertionError(name + " hooks=" + hooks);
                write(cls, Path.of(args[1])); System.out.println("PATCH33 " + name + " hooks=" + hooks);
            }
        }
        try (ZipFile zip = new ZipFile(args[2])) {
            ClassNode cls = new ClassNode();
            new ClassReader(zip.getInputStream(zip.getEntry("fr/arthur/capitale/rphud/CapitaleRpHudClient.class"))).accept(cls, 0);
            int hooks = 0;
            for (MethodNode m : cls.methods) if (m.name.equals("findNoArg") && m.desc.equals("(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Method;")) {
                InsnList code = new InsnList(); code.add(new VarInsnNode(ALOAD, 0)); code.add(new VarInsnNode(ALOAD, 1));
                code.add(call("fr/arthur/capitale/rphud/NoArgMethodCache", "find", m.desc)); code.add(new InsnNode(ARETURN));
                replaceBody(m, code); hooks++;
            }
            if (hooks != 1) throw new AssertionError("HUD lookup hook=" + hooks);
            write(cls, Path.of(args[3])); System.out.println("PATCH33 HUD lookup=1");
        }
    }
}
