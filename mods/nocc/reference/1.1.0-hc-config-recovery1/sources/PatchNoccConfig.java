import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import java.nio.file.*;

public final class PatchNoccConfig {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("input class output class");
        byte[] input = Files.readAllBytes(Path.of(args[0]));
        ClassNode cn = new ClassNode(Opcodes.ASM9);
        new ClassReader(input).accept(cn, 0);
        int changed = 0;
        for (MethodNode m : cn.methods) {
            if (!m.name.equals("load") || !m.desc.equals("()Lcom/cyborggrizzly/nocc/client/NoccConfig;")) continue;
            for (TryCatchBlockNode t : m.tryCatchBlocks) {
                if ("java/io/IOException".equals(t.type)) {
                    t.type = "java/lang/Exception";
                    changed++;
                }
            }
        }
        if (changed != 2) throw new IllegalStateException("expected 2 IOException handlers in NoccConfig.load, changed=" + changed);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override protected String getCommonSuperClass(String a, String b) { return "java/lang/Object"; }
        };
        cn.accept(cw);
        Path out = Path.of(args[1]);
        Files.createDirectories(out.getParent());
        Files.write(out, cw.toByteArray());
        System.out.println("Patched NoccConfig.load: malformed JSON now falls back to defaults. handlers=" + changed);
    }
}
