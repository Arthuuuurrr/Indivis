import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchPersistencePerf1 implements Opcodes {
    private static final int API = ASM8;

    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        Path p = root.resolve("net/tompsen/nexuscharacters/VaultManager.class");

        ClassNode cn = new ClassNode(API);
        new ClassReader(Files.readAllBytes(p)).accept(cn, 0);

        MethodNode m = null;
        for (MethodNode candidate : cn.methods) {
            if (candidate.name.equals("serializePlayerNbtTokenized")
                    && candidate.desc.equals("(Lnet/minecraft/class_3222;)[B")) {
                m = candidate;
                break;
            }
        }
        if (m == null) throw new IllegalStateException("serializePlayerNbtTokenized not found");

        m.instructions.clear();
        m.tryCatchBlocks.clear();
        if (m.localVariables != null) m.localVariables.clear();

        InsnList x = m.instructions;
        LabelNode loop = new LabelNode();
        LabelNode skipRemove = new LabelNode();
        LabelNode done = new LabelNode();

        x.add(new FieldInsnNode(GETSTATIC, "net/minecraft/class_8942", "field_60348", "Lnet/minecraft/class_8942;"));
        x.add(new VarInsnNode(ALOAD, 0));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_3222", "method_51469", "()Lnet/minecraft/class_3218;", false));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_3218", "method_30349", "()Lnet/minecraft/class_5455;", false));
        x.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/class_11362", "method_71459", "(Lnet/minecraft/class_8942;Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_11362;", false));
        x.add(new VarInsnNode(ASTORE, 1));
        x.add(new VarInsnNode(ALOAD, 0));
        x.add(new VarInsnNode(ALOAD, 1));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_3222", "method_5647", "(Lnet/minecraft/class_11372;)V", false));
        x.add(new VarInsnNode(ALOAD, 1));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_11362", "method_71475", "()Lnet/minecraft/class_2487;", false));
        x.add(new VarInsnNode(ASTORE, 2));

        x.add(new VarInsnNode(ALOAD, 2));
        x.add(new LdcInsnNode("Tags"));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_2487", "method_68569", "(Ljava/lang/String;)Lnet/minecraft/class_2499;", false));
        x.add(new VarInsnNode(ASTORE, 3));
        x.add(new VarInsnNode(ALOAD, 3));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_2499", "size", "()I", false));
        x.add(new InsnNode(ICONST_1));
        x.add(new InsnNode(ISUB));
        x.add(new VarInsnNode(ISTORE, 4));

        x.add(loop);
        x.add(new FrameNode(F_FULL, 5, new Object[]{
                "net/minecraft/class_3222",
                "net/minecraft/class_11362",
                "net/minecraft/class_2487",
                "net/minecraft/class_2499",
                INTEGER
        }, 0, new Object[]{}));
        x.add(new VarInsnNode(ILOAD, 4));
        x.add(new JumpInsnNode(IFLT, done));
        x.add(new VarInsnNode(ALOAD, 3));
        x.add(new VarInsnNode(ILOAD, 4));
        x.add(new LdcInsnNode(""));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_2499", "method_68577", "(ILjava/lang/String;)Ljava/lang/String;", false));
        x.add(new VarInsnNode(ASTORE, 5));
        x.add(new VarInsnNode(ALOAD, 5));
        x.add(new MethodInsnNode(INVOKESTATIC, "net/tompsen/nexuscharacters/PlayerNbtSanitizerV0622", "shouldStripTag", "(Ljava/lang/String;)Z", false));
        x.add(new JumpInsnNode(IFEQ, skipRemove));
        x.add(new VarInsnNode(ALOAD, 3));
        x.add(new VarInsnNode(ILOAD, 4));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/class_2499", "method_10536", "(I)Lnet/minecraft/class_2520;", false));
        x.add(new InsnNode(POP));

        x.add(skipRemove);
        x.add(new FrameNode(F_FULL, 6, new Object[]{
                "net/minecraft/class_3222",
                "net/minecraft/class_11362",
                "net/minecraft/class_2487",
                "net/minecraft/class_2499",
                INTEGER,
                "java/lang/String"
        }, 0, new Object[]{}));
        x.add(new IincInsnNode(4, -1));
        x.add(new JumpInsnNode(GOTO, loop));

        x.add(done);
        x.add(new FrameNode(F_FULL, 5, new Object[]{
                "net/minecraft/class_3222",
                "net/minecraft/class_11362",
                "net/minecraft/class_2487",
                "net/minecraft/class_2499",
                INTEGER
        }, 0, new Object[]{}));
        x.add(new TypeInsnNode(NEW, "java/io/ByteArrayOutputStream"));
        x.add(new InsnNode(DUP));
        x.add(new MethodInsnNode(INVOKESPECIAL, "java/io/ByteArrayOutputStream", "<init>", "()V", false));
        x.add(new VarInsnNode(ASTORE, 6));
        x.add(new VarInsnNode(ALOAD, 2));
        x.add(new VarInsnNode(ALOAD, 6));
        x.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/class_2507", "method_10634", "(Lnet/minecraft/class_2487;Ljava/io/OutputStream;)V", false));
        x.add(new VarInsnNode(ALOAD, 6));
        x.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/ByteArrayOutputStream", "toByteArray", "()[B", false));
        x.add(new InsnNode(ARETURN));

        m.maxLocals = 7;
        m.maxStack = 4;

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        Files.write(p, cw.toByteArray());
    }
}
