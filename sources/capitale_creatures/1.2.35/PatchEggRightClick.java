import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchEggRightClick implements Opcodes {
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
    static final String DATA_TYPE="net/minecraft/class_9331";
    static final String NBT_COMPONENT="net/minecraft/class_9279";

    static void addUseMethod(ClassNode cn) {
        for (MethodNode m : cn.methods) {
            if (m.name.equals("method_55766") && m.desc.equals("(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;"))
                throw new IllegalStateException(cn.name + " already has method_55766");
        }

        MethodNode m = new MethodNode(ACC_PROTECTED, "method_55766",
            "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;",
            null, null);

        LabelNode success = new LabelNode();
        LabelNode remove = new LabelNode();

        m.instructions.add(new VarInsnNode(ALOAD, 2));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, WORLD, "method_8608", "()Z", false));
        m.instructions.add(new JumpInsnNode(IFNE, success));

        m.instructions.add(new TypeInsnNode(NEW, ITEM_STACK));
        m.instructions.add(new InsnNode(DUP));
        m.instructions.add(new VarInsnNode(ALOAD, 0));
        m.instructions.add(new MethodInsnNode(INVOKESPECIAL, ITEM_STACK, "<init>", "(Lnet/minecraft/class_1935;)V", false));
        m.instructions.add(new VarInsnNode(ASTORE, 6));

        m.instructions.add(new TypeInsnNode(NEW, NBT));
        m.instructions.add(new InsnNode(DUP));
        m.instructions.add(new MethodInsnNode(INVOKESPECIAL, NBT, "<init>", "()V", false));
        m.instructions.add(new VarInsnNode(ASTORE, 7));
        m.instructions.add(new VarInsnNode(ALOAD, 7));
        m.instructions.add(new LdcInsnNode("Variant"));
        m.instructions.add(new VarInsnNode(ALOAD, 1));
        m.instructions.add(new FieldInsnNode(GETSTATIC, cn.name, "VARIANT", "Lnet/minecraft/class_2758;"));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK_STATE, "method_11654", "(L"+PROP+";)Ljava/lang/Comparable;", false));
        m.instructions.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, NBT, "method_10569", "(Ljava/lang/String;I)V", false));

        m.instructions.add(new VarInsnNode(ALOAD, 6));
        m.instructions.add(new FieldInsnNode(GETSTATIC, DATA_TYPES, "field_49628", "L"+DATA_TYPE+";"));
        m.instructions.add(new VarInsnNode(ALOAD, 7));
        m.instructions.add(new MethodInsnNode(INVOKESTATIC, NBT_COMPONENT, "method_57456", "(L"+NBT+";)L"+NBT_COMPONENT+";", false));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, ITEM_STACK, "method_57379", "(L"+DATA_TYPE+";Ljava/lang/Object;)Ljava/lang/Object;", false));
        m.instructions.add(new InsnNode(POP));

        m.instructions.add(new VarInsnNode(ALOAD, 4));
        m.instructions.add(new VarInsnNode(ALOAD, 6));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, PLAYER, "method_7270", "(L"+ITEM_STACK+")Z", false));
        m.instructions.add(new JumpInsnNode(IFNE, remove));
        m.instructions.add(new VarInsnNode(ALOAD, 4));
        m.instructions.add(new VarInsnNode(ALOAD, 6));
        m.instructions.add(new InsnNode(ICONST_0));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, PLAYER, "method_7328", "(L"+ITEM_STACK+";Z)Lnet/minecraft/class_1542;", false));
        m.instructions.add(new InsnNode(POP));

        m.instructions.add(remove);
        m.instructions.add(new FrameNode(F_FULL, 8,
            new Object[]{cn.name, BLOCK_STATE, WORLD, BLOCK_POS, PLAYER, HIT, ITEM_STACK, NBT},
            0, new Object[]{}));

        m.instructions.add(new VarInsnNode(ALOAD, 2));
        m.instructions.add(new VarInsnNode(ALOAD, 3));
        m.instructions.add(new FieldInsnNode(GETSTATIC, BLOCKS, "field_10124", "L"+BLOCK+";"));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK, "method_9564", "()L"+BLOCK_STATE+";", false));
        m.instructions.add(new InsnNode(ICONST_3));
        m.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, WORLD, "method_8652", "(L"+BLOCK_POS+";L"+BLOCK_STATE+";I)Z", false));
        m.instructions.add(new InsnNode(POP));

        m.instructions.add(success);
        m.instructions.add(new FrameNode(F_FULL, 6,
            new Object[]{cn.name, BLOCK_STATE, WORLD, BLOCK_POS, PLAYER, HIT},
            0, new Object[]{}));
        m.instructions.add(new FieldInsnNode(GETSTATIC, ACTION, "field_5812", "Lnet/minecraft/class_1269$class_9860;"));
        m.instructions.add(new InsnNode(ARETURN));

        m.maxLocals = 8;
        m.maxStack = 5;
        cn.methods.add(m);
    }

    static byte[] patch(byte[] input) {
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        addUseMethod(cn);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("usage: <class-root> <output-root>");
        Path in = Path.of(args[0]), out = Path.of(args[1]);
        String[] targets = {
            "net/suprk/ufauna/block/custom/CrocodileEgg",
            "net/suprk/ufauna/block/custom/KomodoDragonEgg"
        };
        for (String t : targets) {
            Path src = in.resolve(t + ".class");
            Path dst = out.resolve(t + ".class");
            Files.createDirectories(dst.getParent());
            Files.write(dst, patch(Files.readAllBytes(src)));
        }
    }
}
