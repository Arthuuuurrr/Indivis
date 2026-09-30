import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * NexusCharacters BETA 1.0 PRE8 console-noise patch.
 *
 * Base: PRE7.
 *
 * Changes only:
 * - removes the periodic Puffish save success INFO call;
 * - removes the periodic ProfileState save success stdout call;
 * - runs profile hooks with ServerCommandSource.withSilent().
 *
 * It does not change checkpoint cadence, save calls, payloads, files,
 * failure diagnostics, load logic, disconnect handling, or shutdown handling.
 */
public final class PatchConsoleSpam {
    private static final int API = Opcodes.ASM9;

    private static final String PUFFISH = "net/tompsen/nexuscharacters/PuffishSkillsBridge";
    private static final String PROFILE = "net/tompsen/nexuscharacters/ProfileStateBridge";
    private static final String DTO = "Lnet/tompsen/nexuscharacters/CharacterDto;";
    private static final String PLAYER = "Lnet/minecraft/class_3222;";
    private static final String SAVE_DESC = "(" + PLAYER + DTO + ")V";

    private static byte[] patchPuffish(byte[] input) {
        ClassNode cn = read(input);
        int patched = 0;

        for (MethodNode m : cn.methods) {
            if (!m.name.equals("save") || !m.desc.equals(SAVE_DESC)) {
                continue;
            }

            for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                if (!(n instanceof LdcInsnNode ldc)
                        || !Objects.equals(ldc.cst, "[Nexus] Puffish Skills saved for character {} ({})")) {
                    continue;
                }

                AbstractInsnNode start = n;
                while (start != null) {
                    if (start instanceof FieldInsnNode f
                            && f.getOpcode() == Opcodes.GETSTATIC
                            && f.owner.equals("net/tompsen/nexuscharacters/NexusCharacters")
                            && f.name.equals("LOGGER")) {
                        break;
                    }
                    start = start.getPrevious();
                }
                if (start == null) {
                    throw new IllegalStateException("Puffish success log LOGGER start not found");
                }

                AbstractInsnNode end = n;
                while (end != null) {
                    if (end instanceof MethodInsnNode mi
                            && mi.getOpcode() == Opcodes.INVOKEINTERFACE
                            && mi.owner.equals("org/slf4j/Logger")
                            && mi.name.equals("info")) {
                        break;
                    }
                    end = end.getNext();
                }
                if (end == null) {
                    throw new IllegalStateException("Puffish success log info() end not found");
                }

                removeRange(m.instructions, start, end);
                patched++;
                break;
            }
        }

        if (patched != 1) {
            throw new IllegalStateException("Puffish save-success log patch count=" + patched);
        }
        return write(cn);
    }

    private static byte[] patchProfile(byte[] input) {
        ClassNode cn = read(input);
        int saveLogPatched = 0;
        int silentHookPatched = 0;

        for (MethodNode m : cn.methods) {
            if (m.name.equals("save") && m.desc.equals(SAVE_DESC)) {
                for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                    if (!(n instanceof FieldInsnNode f)
                            || f.getOpcode() != Opcodes.GETSTATIC
                            || !f.owner.equals("java/lang/System")
                            || !f.name.equals("out")
                            || !f.desc.equals("Ljava/io/PrintStream;")) {
                        continue;
                    }

                    AbstractInsnNode end = n;
                    boolean hasRecipe = false;
                    int scanned = 0;
                    while (end != null && scanned++ < 80) {
                        if (containsText(end, "Profile scores saved for character")) {
                            hasRecipe = true;
                        }
                        if (end instanceof MethodInsnNode mi
                                && mi.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && mi.owner.equals("java/io/PrintStream")
                                && mi.name.equals("println")
                                && mi.desc.equals("(Ljava/lang/String;)V")) {
                            if (hasRecipe) {
                                removeRange(m.instructions, n, end);
                                saveLogPatched++;
                            }
                            break;
                        }
                        end = end.getNext();
                    }
                    if (saveLogPatched == 1) {
                        break;
                    }
                }
            }

            if (m.name.equals("runHookIfCorePresent")
                    && m.desc.equals("(" + PLAYER + "Lnet/minecraft/class_269;Ljava/lang/String;)V")) {
                for (AbstractInsnNode n = m.instructions.getFirst(); n != null; n = n.getNext()) {
                    if (n instanceof MethodInsnNode mi
                            && mi.getOpcode() == Opcodes.INVOKEVIRTUAL
                            && mi.owner.equals("net/minecraft/class_3222")
                            && mi.name.equals("method_64396")
                            && mi.desc.equals("()Lnet/minecraft/class_2168;")) {
                        MethodInsnNode quiet = new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                "net/minecraft/class_2168",
                                "method_9217",
                                "()Lnet/minecraft/class_2168;",
                                false
                        );
                        m.instructions.insert(n, quiet);
                        silentHookPatched++;
                        break;
                    }
                }
            }
        }

        if (saveLogPatched != 1) {
            throw new IllegalStateException("Profile save-success stdout patch count=" + saveLogPatched);
        }
        if (silentHookPatched != 1) {
            throw new IllegalStateException("Profile hook withSilent patch count=" + silentHookPatched);
        }
        return write(cn);
    }

    private static boolean containsText(AbstractInsnNode n, String needle) {
        if (n instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
            return s.contains(needle);
        }
        if (n instanceof InvokeDynamicInsnNode indy) {
            for (Object arg : indy.bsmArgs) {
                if (arg instanceof String s && s.contains(needle)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeRange(InsnList list, AbstractInsnNode start, AbstractInsnNode end) {
        AbstractInsnNode n = start;
        while (n != null) {
            AbstractInsnNode next = n.getNext();
            list.remove(n);
            if (n == end) {
                return;
            }
            n = next;
        }
        throw new IllegalStateException("Instruction range end not reached");
    }

    private static ClassNode read(byte[] input) {
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode(API);
        cr.accept(cn, 0);
        return cn;
    }

    private static byte[] write(ClassNode cn) {
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
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "usage: ProfileStateBridge.in ProfileStateBridge.out PuffishSkillsBridge.in PuffishSkillsBridge.out");
        }

        Path profileIn = Path.of(args[0]);
        Path profileOut = Path.of(args[1]);
        Path puffishIn = Path.of(args[2]);
        Path puffishOut = Path.of(args[3]);

        Files.createDirectories(profileOut.getParent());
        Files.createDirectories(puffishOut.getParent());

        Files.write(profileOut, patchProfile(Files.readAllBytes(profileIn)));
        Files.write(puffishOut, patchPuffish(Files.readAllBytes(puffishIn)));

        System.out.println("Patched ProfileStateBridge: silent hook + no save-success stdout.");
        System.out.println("Patched PuffishSkillsBridge: no save-success INFO.");
    }
}
