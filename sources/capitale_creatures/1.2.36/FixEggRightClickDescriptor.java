import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class FixEggRightClickDescriptor implements Opcodes {
    static byte[] fix(byte[] input) {
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        int fixed = 0;

        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                if (n instanceof MethodInsnNode mi
                        && mi.owner.equals("net/minecraft/class_1657")
                        && mi.name.equals("method_7270")
                        && mi.desc.equals("(Lnet/minecraft/class_1799)Z")) {
                    mi.desc = "(Lnet/minecraft/class_1799;)Z";
                    fixed++;
                }
            }
        }

        if (fixed != 1) {
            throw new IllegalStateException(cn.name + ": expected exactly one descriptor fix, got " + fixed);
        }

        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("usage: <class-root> <output-root>");
        Path in = Path.of(args[0]);
        Path out = Path.of(args[1]);

        String[] targets = {
            "net/suprk/ufauna/block/custom/CrocodileEgg",
            "net/suprk/ufauna/block/custom/KomodoDragonEgg"
        };

        for (String t : targets) {
            Path src = in.resolve(t + ".class");
            Path dst = out.resolve(t + ".class");
            Files.createDirectories(dst.getParent());
            Files.write(dst, fix(Files.readAllBytes(src)));
        }
    }
}
