import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Haute Capitale NexusCharacters 0.8.0-alpha1.2.1 persistence PERF1.
 *
 * Replaces only VaultManager.serializePlayerNbtTokenized so the transient
 * command-tag sanitizer runs on the already-built NBT compound before its
 * single GZIP serialization. The previous implementation serialized first,
 * decompressed in PlayerNbtSanitizerV0622.sanitize(byte[]), then potentially
 * serialized a second time.
 *
 * No checkpoint cadence, vault format, tag policy or lifecycle hook is changed.
 */
public final class PatchPersistenceSingleCompress {
    private static final int API = Opcodes.ASM9;

    private static byte[] patchVaultManager(byte[] input) {
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode(API);
        cr.accept(cn, 0);

        int patched = 0;
        for (MethodNode m : cn.methods) {
            if (!m.name.equals("serializePlayerNbtTokenized")
                    || !m.desc.equals("(Lnet/minecraft/class_3222;)[B")) {
                continue;
            }
            patched++;

            InsnList i = new InsnList();

            // Same NBT construction sequence as VaultManager.serializePlayerNbt.
            i.add(new FieldInsnNode(Opcodes.GETSTATIC,
                    "net/minecraft/class_8942", "field_60348", "Lnet/minecraft/class_8942;"));
            i.add(new VarInsnNode(Opcodes.ALOAD, 0));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_3222", "method_51469", "()Lnet/minecraft/class_3218;", false));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_3218", "method_30349", "()Lnet/minecraft/class_5455;", false));
            i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "net/minecraft/class_11362", "method_71459",
                    "(Lnet/minecraft/class_8942;Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_11362;",
                    false));
            i.add(new VarInsnNode(Opcodes.ASTORE, 1));

            i.add(new VarInsnNode(Opcodes.ALOAD, 0));
            i.add(new VarInsnNode(Opcodes.ALOAD, 1));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_3222", "method_5647", "(Lnet/minecraft/class_11372;)V", false));

            i.add(new VarInsnNode(Opcodes.ALOAD, 1));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_11362", "method_71475", "()Lnet/minecraft/class_2487;", false));
            i.add(new VarInsnNode(Opcodes.ASTORE, 2));

            // Same top-level Tags accessor as PlayerNbtSanitizerV0622.
            i.add(new VarInsnNode(Opcodes.ALOAD, 2));
            i.add(new LdcInsnNode("Tags"));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_2487", "method_68569",
                    "(Ljava/lang/String;)Lnet/minecraft/class_2499;", false));
            i.add(new VarInsnNode(Opcodes.ASTORE, 3));

            i.add(new InsnNode(Opcodes.ICONST_0));
            i.add(new VarInsnNode(Opcodes.ISTORE, 4));

            i.add(new VarInsnNode(Opcodes.ALOAD, 3));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_2499", "size", "()I", false));
            i.add(new InsnNode(Opcodes.ICONST_1));
            i.add(new InsnNode(Opcodes.ISUB));
            i.add(new VarInsnNode(Opcodes.ISTORE, 5));

            LabelNode loop = new LabelNode();
            LabelNode afterLoop = new LabelNode();
            LabelNode keep = new LabelNode();

            i.add(loop);
            i.add(new VarInsnNode(Opcodes.ILOAD, 5));
            i.add(new JumpInsnNode(Opcodes.IFLT, afterLoop));
            i.add(new VarInsnNode(Opcodes.ALOAD, 3));
            i.add(new VarInsnNode(Opcodes.ILOAD, 5));
            i.add(new LdcInsnNode(""));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_2499", "method_68577",
                    "(ILjava/lang/String;)Ljava/lang/String;", false));
            i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "net/tompsen/nexuscharacters/PlayerNbtSanitizerV0622",
                    "shouldStripTag", "(Ljava/lang/String;)Z", false));
            i.add(new JumpInsnNode(Opcodes.IFEQ, keep));
            i.add(new VarInsnNode(Opcodes.ALOAD, 3));
            i.add(new VarInsnNode(Opcodes.ILOAD, 5));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/class_2499", "method_10536",
                    "(I)Lnet/minecraft/class_2520;", false));
            i.add(new InsnNode(Opcodes.POP));
            i.add(new IincInsnNode(4, 1));
            i.add(keep);
            i.add(new IincInsnNode(5, -1));
            i.add(new JumpInsnNode(Opcodes.GOTO, loop));
            i.add(afterLoop);

            // Single compression, after transient tags have been removed.
            i.add(new TypeInsnNode(Opcodes.NEW, "java/io/ByteArrayOutputStream"));
            i.add(new InsnNode(Opcodes.DUP));
            i.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                    "java/io/ByteArrayOutputStream", "<init>", "()V", false));
            i.add(new VarInsnNode(Opcodes.ASTORE, 6));

            i.add(new VarInsnNode(Opcodes.ALOAD, 2));
            i.add(new VarInsnNode(Opcodes.ALOAD, 6));
            i.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "net/minecraft/class_2507", "method_10634",
                    "(Lnet/minecraft/class_2487;Ljava/io/OutputStream;)V", false));

            // Preserve the existing sanitizer diagnostic when tags were stripped.
            LabelNode noLog = new LabelNode();
            i.add(new VarInsnNode(Opcodes.ILOAD, 4));
            i.add(new JumpInsnNode(Opcodes.IFEQ, noLog));
            i.add(new FieldInsnNode(Opcodes.GETSTATIC,
                    "java/lang/System", "out", "Ljava/io/PrintStream;"));
            i.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
            i.add(new InsnNode(Opcodes.DUP));
            i.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                    "java/lang/StringBuilder", "<init>", "()V", false));
            i.add(new LdcInsnNode("[Nexus][Persistence 0.6.22] Sanitized "));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            i.add(new VarInsnNode(Opcodes.ILOAD, 4));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false));
            i.add(new LdcInsnNode(" transient/derived player tag(s) from profile NBT."));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            i.add(noLog);

            i.add(new VarInsnNode(Opcodes.ALOAD, 6));
            i.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/io/ByteArrayOutputStream", "toByteArray", "()[B", false));
            i.add(new InsnNode(Opcodes.ARETURN));

            m.instructions = i;
            m.tryCatchBlocks.clear();
            m.localVariables = null;
            m.visibleLocalVariableAnnotations = null;
            m.invisibleLocalVariableAnnotations = null;
            m.maxLocals = 7;
            m.maxStack = 8;
        }

        if (patched != 1) {
            throw new IllegalStateException("serializePlayerNbtTokenized patch count=" + patched);
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String a, String b) {
                return "java/lang/Object";
            }
        };
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("input VaultManager.class, output VaultManager.class");
        }
        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);
        Files.createDirectories(output.getParent());
        Files.write(output, patchVaultManager(Files.readAllBytes(input)));
        System.out.println("Patched VaultManager.serializePlayerNbtTokenized: single-compress persistence.");
    }
}
