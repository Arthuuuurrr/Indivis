import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchEggRightClick137 implements Opcodes {
    static final String BLOCK_STATE="net/minecraft/class_2680";
    static final String WORLD="net/minecraft/class_1937";
    static final String BLOCK_POS="net/minecraft/class_2338";
    static final String PLAYER="net/minecraft/class_1657";
    static final String HIT="net/minecraft/class_3965";
    static final String ACTION="net/minecraft/class_1269";
    static final String ITEM_STACK="net/minecraft/class_1799";
    static final String NBT="net/minecraft/class_2487";
    static final String PROP="net/minecraft/class_2769";
    static final String BLOCK="net/minecraft/class_2248";
    static final String BLOCKS="net/minecraft/class_2246";
    static final String DATA_TYPES="net/minecraft/class_9334";
    static final String NBT_COMPONENT="net/minecraft/class_9279";

    static void addUseMethod(ClassNode cn, boolean hasVariant) {
        for (MethodNode old : cn.methods) {
            if (old.name.equals("method_55766") && old.desc.equals("(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;")) {
                throw new IllegalStateException(cn.name + " already has interaction override");
            }
        }

        MethodNode m = new MethodNode(ACC_PROTECTED, "method_55766",
            "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;",
            null, null);

        LabelNode server = new LabelNode();
        LabelNode remove = new LabelNode();

        m.instructions.add(new VarInsnNode(ALOAD, 2));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, WORLD, "method_8608", "()Z", false));
        m.instructions.add(new JumpInsnNode(IFEQ, server));
        m.instructions.add(new FieldInsnNode(GETSTATIC, ACTION, "field_5812", "Lnet/minecraft/class_1269$class_9860;"));
        m.instructions.add(new InsnNode(ARETURN));

        m.instructions.add(server);
        m.instructions.add(new FrameNode(F_FULL, 6,
            new Object[]{cn.name, BLOCK_STATE, WORLD, BLOCK_POS, PLAYER, HIT}, 0, new Object[]{}));

        m.instructions.add(new TypeInsnNode(NEW, ITEM_STACK));
        m.instructions.add(new InsnNode(DUP));
        m.instructions.add(new VarInsnNode(ALOAD, 0));
        m.instructions.add(new MethodInsnNode(INVOKESPECIAL, ITEM_STACK, "<init>", "(Lnet/minecraft/class_1935;)V", false));
        m.instructions.add(new VarInsnNode(ASTORE, 6));

        if (hasVariant) {
            m.instructions.add(new TypeInsnNode(NEW, NBT));
            m.instructions.add(new InsnNode(DUP));
            m.instructions.add(new MethodInsnNode(INVOKESPECIAL, NBT, "<init>", "()V", false));
            m.instructions.add(new VarInsnNode(ASTORE, 7));
            m.instructions.add(new VarInsnNode(ALOAD, 7));
            m.instructions.add(new LdcInsnNode("Variant"));
            m.instructions.add(new VarInsnNode(ALOAD, 1));
            m.instructions.add(new FieldInsnNode(GETSTATIC, cn.name, "VARIANT", "Lnet/minecraft/class_2758;"));
            m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK_STATE, "method_11654", "(Lnet/minecraft/class_2769;)Ljava/lang/Comparable;", false));
            m.instructions.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
            m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
            m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, NBT, "method_10569", "(Ljava/lang/String;I)V", false));
            m.instructions.add(new VarInsnNode(ALOAD, 6));
            m.instructions.add(new FieldInsnNode(GETSTATIC, DATA_TYPES, "field_49628", "Lnet/minecraft/class_9331;"));
            m.instructions.add(new VarInsnNode(ALOAD, 7));
            m.instructions.add(new MethodInsnNode(INVOKESTATIC, NBT_COMPONENT, "method_57456", "(Lnet/minecraft/class_2487;)Lnet/minecraft/class_9279;", false));
            m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, ITEM_STACK, "method_57379", "(Lnet/minecraft/class_9331;Ljava/lang/Object;)Ljava/lang/Object;", false));
            m.instructions.add(new InsnNode(POP));
        }

        m.instructions.add(new VarInsnNode(ALOAD, 4));
        m.instructions.add(new VarInsnNode(ALOAD, 6));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, PLAYER, "method_7270", "(Lnet/minecraft/class_1799;)Z", false));
        m.instructions.add(new JumpInsnNode(IFNE, remove));
        m.instructions.add(new VarInsnNode(ALOAD, 4));
        m.instructions.add(new VarInsnNode(ALOAD, 6));
        m.instructions.add(new InsnNode(ICONST_0));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, PLAYER, "method_7328", "(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1542;", false));
        m.instructions.add(new InsnNode(POP));

        m.instructions.add(remove);
        m.instructions.add(new FrameNode(F_FULL, hasVariant ? 8 : 7,
            hasVariant
                ? new Object[]{cn.name, BLOCK_STATE, WORLD, BLOCK_POS, PLAYER, HIT, ITEM_STACK, NBT}
                : new Object[]{cn.name, BLOCK_STATE, WORLD, BLOCK_POS, PLAYER, HIT, ITEM_STACK},
            0, new Object[]{}));

        m.instructions.add(new VarInsnNode(ALOAD, 2));
        m.instructions.add(new VarInsnNode(ALOAD, 3));
        m.instructions.add(new FieldInsnNode(GETSTATIC, BLOCKS, "field_10124", "Lnet/minecraft/class_2248;"));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK, "method_9564", "()Lnet/minecraft/class_2680;", false));
        m.instructions.add(new InsnNode(ICONST_3));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, WORLD, "method_8652", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)Z", false));
        m.instructions.add(new InsnNode(POP));
        m.instructions.add(new FieldInsnNode(GETSTATIC, ACTION, "field_5812", "Lnet/minecraft/class_1269$class_9860;"));
        m.instructions.add(new InsnNode(ARETURN));

        cn.methods.add(m);
    }

    static byte[] patch(byte[] input, boolean hasVariant) {
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        addUseMethod(cn, hasVariant);

        // Fresh writer: rebuild the constant pool instead of retaining stale entries.
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("usage: <class-root> <output-root>");
        Path in = Path.of(args[0]), out = Path.of(args[1]);
        String[][] targets = {
            {"net/suprk/ufauna/block/custom/CrocodileEgg", "true"},
            {"net/suprk/ufauna/block/custom/KomodoDragonEgg", "false"}
        };
        for (String[] t : targets) {
            Path input = in.resolve(t[0] + ".class");
            Path output = out.resolve(t[0] + ".class");
            Files.createDirectories(output.getParent());
            Files.write(output, patch(Files.readAllBytes(input), Boolean.parseBoolean(t[1])));
        }
    }
}
