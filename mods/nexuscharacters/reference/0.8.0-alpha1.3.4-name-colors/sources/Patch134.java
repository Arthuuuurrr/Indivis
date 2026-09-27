import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Patch134 implements Opcodes {
    static final int API = ASM8;

    static byte[] patchSelection(byte[] input) {
        ClassNode cn = new ClassNode(API);
        new ClassReader(input).accept(cn, 0);
        int changed = 0;
        for (MethodNode m : cn.methods) {
            if (!m.name.equals("method_25426")) continue;
            for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                if (n instanceof MethodInsnNode mi
                        && mi.getOpcode() == INVOKESTATIC
                        && mi.owner.equals("net/minecraft/class_2561")
                        && mi.name.equals("method_43470")
                        && mi.desc.equals("(Ljava/lang/String;)Lnet/minecraft/class_5250;")) {
                    AbstractInsnNode p = n.getPrevious();
                    while (p != null && p.getOpcode() < 0) p = p.getPrevious();
                    if (p instanceof VarInsnNode vi && vi.getOpcode() == ALOAD && vi.var == 8) {
                        mi.owner = "net/tompsen/nexuscharacters/LegacyCharacterNameTextSupport";
                        mi.name = "parseSelectionLabel";
                        mi.itf = false;
                        changed++;
                    }
                }
            }
        }
        if (changed != 1) throw new IllegalStateException("selection label patch count=" + changed);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static byte[] patchLabels(byte[] input) {
        ClassNode cn = new ClassNode(API);
        new ClassReader(input).accept(cn, 0);
        int moustache = 0, beard = 0;
        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                if (n instanceof LdcInsnNode l && l.cst instanceof String s) {
                    if (s.equals("Moustache 1")) { l.cst = "Moustache mince"; moustache++; }
                    else if (s.equals("Barbe légère")) { l.cst = "Barbe mince"; beard++; }
                }
            }
        }
        if (moustache != 1 || beard != 1) {
            throw new IllegalStateException("label patch mismatch moustache="+moustache+" beard="+beard);
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        Path s = root.resolve("net/tompsen/nexuscharacters/CharacterSelectionScreen.class");
        Files.write(s, patchSelection(Files.readAllBytes(s)));
        Path c = root.resolve("net/tompsen/nexuscharacters/CosmeticsPack1Support.class");
        Files.write(c, patchLabels(Files.readAllBytes(c)));
    }
}
