import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Haute Capitale containment hotfix for accessories_compat_layer 0.1.13.b1.
 *
 * It inserts a null-capability guard at the beginning of:
 * - WrappedTrinketComponent.readData(ReadView)
 * - WrappedTrinketComponent.writeData(WriteView)
 *
 * No slot registration, Trinkets mapping or Accessories core code is changed.
 */
public final class PatchAccessoriesCompatNullCapability {
    private static final String CLASS =
            "io/wispforest/accessories_compat/trinkets/wrapper/WrappedTrinketComponent";
    private static final String CAP_DESC =
            "()Lio/wispforest/accessories/api/AccessoriesCapability;";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("input class output class");
        }

        byte[] input = Files.readAllBytes(Path.of(args[0]));
        ClassNode cn = new ClassNode(Opcodes.ASM9);
        new ClassReader(input).accept(cn, 0);

        int patched = 0;

        for (MethodNode m : cn.methods) {
            boolean target =
                    (m.name.equals("readData") &&
                     m.desc.equals("(Lnet/minecraft/class_11368;)V"))
                    ||
                    (m.name.equals("writeData") &&
                     m.desc.equals("(Lnet/minecraft/class_11372;)V"));

            if (!target) continue;

            InsnList guard = new InsnList();
            LabelNode capabilityPresent = new LabelNode();

            guard.add(new VarInsnNode(Opcodes.ALOAD, 0));
            guard.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    CLASS,
                    "capability",
                    CAP_DESC,
                    false
            ));
            guard.add(new JumpInsnNode(Opcodes.IFNONNULL, capabilityPresent));
            guard.add(new InsnNode(Opcodes.RETURN));
            guard.add(capabilityPresent);

            m.instructions.insert(guard);
            patched++;
        }

        if (patched != 2) {
            throw new IllegalStateException(
                    "Expected readData + writeData, patched=" + patched
            );
        }

        ClassWriter cw = new ClassWriter(
                ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
        ) {
            @Override
            protected String getCommonSuperClass(String a, String b) {
                return "java/lang/Object";
            }
        };

        cn.accept(cw);

        Path output = Path.of(args[1]);
        Files.createDirectories(output.getParent());
        Files.write(output, cw.toByteArray());
    }
}
