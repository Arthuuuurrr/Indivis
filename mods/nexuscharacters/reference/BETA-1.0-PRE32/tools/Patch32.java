import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Change the hand call and cache cleanup hooks; keep all geometry/world methods intact. */
public final class Patch32 implements Opcodes {
    static final String P = "net/tompsen/nexuscharacters/";
    public static void main(String[] args) throws Exception {
        try (ZipFile z = new ZipFile(args[0])) {
            for (String cls : List.of("GenericSkinLayerSupport", "PosedSurfaceSupport")) {
                ClassNode c = new ClassNode();
                new ClassReader(z.getInputStream(z.getEntry(P + cls + ".class"))).accept(c, 0);
                int patches = 0;
                for (MethodNode m : c.methods) {
                    if (cls.equals("GenericSkinLayerSupport") && m.name.equals("firstPerson")) {
                        for (AbstractInsnNode n : m.instructions.toArray()) {
                            if (n instanceof MethodInsnNode call && call.owner.equals(P + "PosedSurfaceSupport")
                                    && call.name.equals("firstPerson")) {
                                if (!call.desc.equals("(Ljava/lang/Object;[Ljava/lang/Object;Z)V")) throw new AssertionError(call.desc);
                                m.instructions.insertBefore(call, new VarInsnNode(ALOAD, 2));
                                call.owner = P + "FirstPersonSurfaceSupport";
                                call.desc = "(Ljava/lang/Object;[Ljava/lang/Object;ZLjava/lang/Object;)V";
                                patches++;
                            }
                        }
                    }
                    if (cls.equals("GenericSkinLayerSupport") && m.name.equals("clearCaches")) {
                        m.instructions.insert(new MethodInsnNode(INVOKESTATIC, P + "FirstPersonSurfaceSupport", "clearCaches", "()V", false));
                        patches++;
                    }
                    if (cls.equals("PosedSurfaceSupport") && m.name.equals("clear")) {
                        InsnList hook = new InsnList();
                        hook.add(new VarInsnNode(ALOAD, 0));
                        hook.add(new MethodInsnNode(INVOKESTATIC, P + "FirstPersonSurfaceSupport", "detach", "(Ljava/lang/Object;)V", false));
                        m.instructions.insert(hook);
                        patches++;
                    }
                }
                if (patches != (cls.equals("GenericSkinLayerSupport") ? 2 : 1)) throw new AssertionError(cls + " patches=" + patches);
                ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                c.accept(w);
                Path file = Path.of(args[1], P + cls + ".class");
                Files.createDirectories(file.getParent()); Files.write(file, w.toByteArray());
                System.out.println("PATCH32 " + cls + " hooks=" + patches);
            }
        }
    }
}
