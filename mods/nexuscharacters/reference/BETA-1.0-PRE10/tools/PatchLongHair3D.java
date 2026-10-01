import java.nio.file.Files;
import java.nio.file.Path;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

/**
 * PRE10 targeted bytecode patch:
 * 1) DynamicAppearanceSupport.hairName(int) delegates to the optional 3D bridge.
 * 2) CharacterCosmeticFeatureRenderer.render(...) calls the bridge once before
 *    the existing ear/beard renderer.
 */
public final class PatchLongHair3D {
    private static final String SUPPORT = "net/tompsen/nexuscharacters/LongHair3DRenderSupport";

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "usage: PatchLongHair3D <dynamic-in> <dynamic-out> <renderer-in> <renderer-out>");
        }

        patchDynamic(Path.of(args[0]), Path.of(args[1]));
        patchRenderer(Path.of(args[2]), Path.of(args[3]));
    }

    private static void patchDynamic(Path in, Path out) throws Exception {
        ClassReader reader = new ClassReader(Files.readAllBytes(in));
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        int patched = 0;
        for (MethodNode m : node.methods) {
            if (m.name.equals("hairName") && m.desc.equals("(I)Ljava/lang/String;")) {
                m.instructions.clear();
                m.tryCatchBlocks.clear();
                if (m.localVariables != null) m.localVariables.clear();

                InsnList code = new InsnList();
                code.add(new VarInsnNode(Opcodes.ILOAD, 0));
                code.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        SUPPORT,
                        "hairAssetName",
                        "(I)Ljava/lang/String;",
                        false));
                code.add(new InsnNode(Opcodes.ARETURN));
                m.instructions.add(code);
                m.maxStack = 1;
                m.maxLocals = 1;
                patched++;
            }
        }
        if (patched != 1) {
            throw new IllegalStateException("Expected one DynamicAppearanceSupport.hairName, patched " + patched);
        }

        write(reader, node, out);
    }

    private static void patchRenderer(Path in, Path out) throws Exception {
        ClassReader reader = new ClassReader(Files.readAllBytes(in));
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        int patched = 0;
        for (MethodNode m : node.methods) {
            if (!m.name.equals("render")) continue;
            if (!m.desc.startsWith(
                    "(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;")) {
                continue;
            }

            InsnList call = new InsnList();
            call.add(new VarInsnNode(Opcodes.ALOAD, 0));
            call.add(new VarInsnNode(Opcodes.ALOAD, 1));
            call.add(new VarInsnNode(Opcodes.ALOAD, 2));
            call.add(new VarInsnNode(Opcodes.ILOAD, 3));
            call.add(new VarInsnNode(Opcodes.ALOAD, 4));
            call.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    SUPPORT,
                    "render",
                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;)V",
                    false));
            m.instructions.insert(call);
            patched++;
        }
        if (patched != 1) {
            throw new IllegalStateException("Expected one CharacterCosmeticFeatureRenderer.render, patched " + patched);
        }

        write(reader, node, out);
    }

    private static void write(ClassReader reader, ClassNode node, Path out) throws Exception {
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        Files.createDirectories(out.getParent());
        Files.write(out, writer.toByteArray());
    }
}
