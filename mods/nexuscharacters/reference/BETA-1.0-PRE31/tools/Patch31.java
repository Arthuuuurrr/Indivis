import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Applies only the writer bridge and the first-person serialization guard. */
public final class Patch31 implements Opcodes {
    static final String P = "net/tompsen/nexuscharacters/", S = P + "PosedSurfaceSupport$State";
    static AbstractInsnNode real(AbstractInsnNode n) {
        while (n != null && n.getOpcode() < 0) n = n.getNext();
        return n;
    }
    public static void main(String[] args) throws Exception {
        try (ZipFile z = new ZipFile(args[0])) {
            for (String cls : List.of("SurfaceGeometry", "PosedSurfaceSupport")) {
                String name = P + cls;
                ClassNode c = new ClassNode();
                new ClassReader(z.getInputStream(z.getEntry(name + ".class"))).accept(c, 0);
                int patches = 0;
                for (MethodNode m : c.methods) {
                    if (cls.equals("SurfaceGeometry") && m.name.equals("write") && m.desc.equals("(Ljava/util/List;)[F")) {
                        m.instructions.clear(); m.tryCatchBlocks.clear();
                        if (m.localVariables != null) m.localVariables.clear();
                        m.instructions.add(new VarInsnNode(ALOAD, 0));
                        m.instructions.add(new MethodInsnNode(INVOKESTATIC, P+"PrimitiveSurfaceWriter", "write", m.desc, false));
                        m.instructions.add(new InsnNode(ARETURN));
                        patches++;
                    }
                    if (cls.equals("PosedSurfaceSupport") && m.name.equals("applyPRE30")) {
                        JumpInsnNode branch = null, finish = null;
                        for (AbstractInsnNode n : m.instructions) {
                            if (n instanceof VarInsnNode v && v.getOpcode() == ILOAD && v.var == 4
                                    && real(n.getNext()) instanceof JumpInsnNode j && j.getOpcode() == IFEQ) {
                                if (branch != null) throw new AssertionError("Ambiguous first-person branch");
                                branch = j;
                            }
                        }
                        if (branch == null) throw new AssertionError("Missing first-person branch");
                        for (AbstractInsnNode n = branch.getNext(); n != branch.label; n = n.getNext()) {
                            if (n instanceof JumpInsnNode j && j.getOpcode() == GOTO
                                    && real(n.getNext()) instanceof TypeInsnNode t && t.getOpcode() == NEW
                                    && t.desc.equals(P+"PosedSurfaceSupport$Grid")) finish = j;
                        }
                        if (finish == null) throw new AssertionError("Missing first-person exit");
                        // PRE30 slot 12 is the already computed localChanged boolean.
                        boolean localPredicate = false;
                        for (AbstractInsnNode n : m.instructions)
                            if (n instanceof VarInsnNode v && v.getOpcode() == ILOAD && v.var == 12
                                    && real(n.getNext()) instanceof JumpInsnNode j && j.getOpcode() == IFEQ
                                    && real(j.label) instanceof VarInsnNode w && w.getOpcode() == ILOAD && w.var == 4)
                                localPredicate = true;
                        if (!localPredicate) throw new AssertionError("PRE30 localChanged layout changed");
                        LabelNode write = new LabelNode();
                        InsnList guard = new InsnList();
                        guard.add(new VarInsnNode(ILOAD, 12));
                        guard.add(new JumpInsnNode(IFNE, write));
                        guard.add(new VarInsnNode(ALOAD, 5));
                        guard.add(new FieldInsnNode(GETFIELD, S, "firstPerson", "Z"));
                        guard.add(new JumpInsnNode(IFEQ, write));
                        guard.add(new JumpInsnNode(GOTO, finish.label));
                        guard.add(write);
                        m.instructions.insert(branch, guard);
                        patches++;
                    }
                }
                if (patches != 1) throw new AssertionError(cls+" patches="+patches);
                ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS) {
                    protected String getCommonSuperClass(String a, String b) {
                        try {
                            ClassLoader loader = Patch31.class.getClassLoader();
                            Class<?> x = Class.forName(a.replace('/', '.'), false, loader), y = Class.forName(b.replace('/', '.'), false, loader);
                            if (x.isAssignableFrom(y)) return a;
                            if (y.isAssignableFrom(x)) return b;
                            if (x.isInterface() || y.isInterface()) return "java/lang/Object";
                            do { x = x.getSuperclass(); } while (!x.isAssignableFrom(y));
                            return x.getName().replace('.', '/');
                        } catch (ClassNotFoundException e) { throw new RuntimeException(e); }
                    }
                };
                c.accept(w);
                Path file = Path.of(args[1], name+".class");
                Files.createDirectories(file.getParent()); Files.write(file, w.toByteArray());
                System.out.println("PATCH31 "+cls+" narrowly patched");
            }
        }
    }
}
