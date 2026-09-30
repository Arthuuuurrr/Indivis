import java.nio.file.Files;
import java.nio.file.Path;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

/**
 * PRE9 compatibility hotfix.
 *
 * PRE7 rebuilt UnifiedPilositySupport from a reference source that accidentally
 * omitted the public allowsPilosity(Object) ABI used by RacialAppearance69Support.
 * PRE8 inherited that binary unchanged.
 *
 * This patch restores only that missing method to the PRE8 class. All existing
 * PRE8 bytecode, including the dwarf-beard fix inherited from PRE7, is preserved.
 */
public final class PatchUnifiedPilosityAbi {
    private static final String TARGET = "net/tompsen/nexuscharacters/UnifiedPilositySupport";
    private static final String DESC = "(Ljava/lang/Object;)Z";
    private static final String NAMED_DESC = "(Ljava/lang/Object;Ljava/lang/String;)Z";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("usage: PatchUnifiedPilosityAbi <input.class> <output.class>");
        }

        Path in = Path.of(args[0]);
        Path out = Path.of(args[1]);

        ClassReader reader = new ClassReader(Files.readAllBytes(in));
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        if (!TARGET.equals(node.name)) {
            throw new IllegalStateException("Unexpected class: " + node.name);
        }

        boolean namedPresent = node.methods.stream().anyMatch(
                m -> m.name.equals("named") && m.desc.equals(NAMED_DESC));
        if (!namedPresent) {
            throw new IllegalStateException("Required private helper named(Object,String) is missing");
        }

        boolean alreadyPresent = node.methods.stream().anyMatch(
                m -> m.name.equals("allowsPilosity") && m.desc.equals(DESC));
        if (alreadyPresent) {
            throw new IllegalStateException("allowsPilosity(Object) already exists; wrong PRE8 base?");
        }

        MethodNode method = new MethodNode(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "allowsPilosity",
                DESC,
                null,
                null
        );

        // Exact PRE5 semantics:
        // HUMAN || NORDIC || DWARF.
        // IOR is deliberately used instead of branches so the injected straight-
        // line method needs no new StackMapTable frames.
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new LdcInsnNode("HUMAN"));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC, TARGET, "named", NAMED_DESC, false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new LdcInsnNode("NORDIC"));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC, TARGET, "named", NAMED_DESC, false));
        method.instructions.add(new InsnNode(Opcodes.IOR));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new LdcInsnNode("DWARF"));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC, TARGET, "named", NAMED_DESC, false));
        method.instructions.add(new InsnNode(Opcodes.IOR));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));

        method.maxStack = 3;
        method.maxLocals = 1;
        node.methods.add(method);

        ClassWriter writer = new ClassWriter(reader, 0);
        node.accept(writer);

        Files.createDirectories(out.getParent());
        Files.write(out, writer.toByteArray());
        System.out.println("Restored UnifiedPilositySupport.allowsPilosity(Object) ABI.");
    }
}
