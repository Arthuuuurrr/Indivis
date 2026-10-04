import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchClasses {
    static byte[] patchArmor(byte[] in) {
        ClassNode cn = new ClassNode();
        new ClassReader(in).accept(cn, 0);
        int replacements = 0;
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
                    if (s.equals("minecraft:")) {
                        ldc.cst = "__never__:";
                        replacements++;
                    } else if (s.equals("[capitale_armor_balance] ARMOR40 x2 registered: modded armor ADD_VALUE only; vanilla/toughness/HP unchanged.")) {
                        ldc.cst = "[capitale_armor_balance] ARMOR40 x2 registered: all armor ADD_VALUE only; weapons/toughness/HP unchanged.";
                    }
                }
            }
        }
        if (replacements != 2) {
            throw new IllegalStateException("Expected 2 minecraft: LDC uses, got " + replacements);
        }
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static byte[] patchHud(byte[] in) {
        ClassNode cn = new ClassNode();
        new ClassReader(in).accept(cn, 0);
        boolean patched = false;

        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("lambda$render$0")) continue;

            for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn.getOpcode() == Opcodes.ILOAD && insn instanceof VarInsnNode vin && vin.var == 8) {
                    AbstractInsnNode next = insn.getNext();
                    if (next instanceof JumpInsnNode j && next.getOpcode() == Opcodes.IFEQ) {
                        boolean hasHelper = false;
                        AbstractInsnNode p = next;
                        for (int i = 0; i < 8 && p != null; i++, p = p.getNext()) {
                            if (p instanceof MethodInsnNode mi
                                    && mi.owner.equals("net/spell_engine/client/util/HcItemUseSpellIcon")
                                    && mi.name.equals("resolve")) {
                                hasHelper = true;
                                break;
                            }
                        }
                        if (hasHelper) {
                            InsnList add = new InsnList();
                            add.add(new VarInsnNode(Opcodes.ALOAD, 5));
                            add.add(new JumpInsnNode(Opcodes.IFNONNULL, j.label));
                            mn.instructions.insertBefore(insn, add);
                            patched = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!patched) {
            throw new IllegalStateException("HudRenderHelper target branch not found");
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        String mode = args[0];
        byte[] in = Files.readAllBytes(Path.of(args[1]));
        byte[] out = switch (mode) {
            case "armor" -> patchArmor(in);
            case "hud" -> patchHud(in);
            default -> throw new IllegalArgumentException(mode);
        };
        Files.write(Path.of(args[2]), out);
    }
}
