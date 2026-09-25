import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchRc14ItemUseIcon {
    private static final String TARGET = "lambda$render$0";
    private static final String SPELL_RENDER = "net/spell_engine/client/util/SpellRender";
    private static final String HELPER = "net/spell_engine/client/util/HcItemUseSpellIcon";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("input.class output.class");
        ClassNode cn = new ClassNode(Opcodes.ASM8);
        new ClassReader(Files.readAllBytes(Path.of(args[0]))).accept(cn, 0);
        int patched = 0;
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals(TARGET)) continue;
            for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (!(insn instanceof MethodInsnNode mi)) continue;
                if (mi.getOpcode() != Opcodes.INVOKESTATIC || !mi.owner.equals(SPELL_RENDER) || !mi.name.equals("iconTexture")) continue;
                AbstractInsnNode cursor = insn.getPrevious();
                int steps = 0;
                while (cursor != null && steps++ < 12) {
                    if (cursor.getOpcode() == Opcodes.ACONST_NULL) {
                        InsnList replacement = new InsnList();
                        replacement.add(new VarInsnNode(Opcodes.ALOAD, 7));
                        replacement.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC, HELPER, "resolve",
                                "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2960;", false));
                        mn.instructions.insertBefore(cursor, replacement);
                        mn.instructions.remove(cursor);
                        patched++;
                        break;
                    }
                    cursor = cursor.getPrevious();
                }
            }
        }
        if (patched != 1) throw new IllegalStateException("Expected one item-use icon branch, patched=" + patched);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        Files.write(Path.of(args[1]), cw.toByteArray());
    }
}
