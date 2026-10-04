import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

public final class PatchFirearmAuthority {
    private static final String AUTH = "net/hautecapitale/fusils/skills/CapSkillsFusilAuthority";

    public static void main(String[] args) throws Exception {
        if (args.length != 4) throw new IllegalArgumentException("gun-in gun-out hunter-in hunter-out");
        patchGun(Path.of(args[0]), Path.of(args[1]));
        patchHunter(Path.of(args[2]), Path.of(args[3]));
    }

    private static void patchGun(Path in, Path out) throws Exception {
        ClassReader cr = new ClassReader(Files.readAllBytes(in));
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM8, cw) {
            @Override public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
                if (name.equals("tryFire") && desc.equals("(Lnet/minecraft/class_1309;Lnet/minecraft/class_243;Lnet/hautecapitale/fusils/gun/GunplayManager$FireOptions;)Z")) {
                    return new MethodVisitor(Opcodes.ASM8, mv) {
                        @Override public void visitCode() {
                            super.visitCode();
                            Label ok = new Label();
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, AUTH, "canFire", "(Lnet/minecraft/class_1309;)Z", false);
                            super.visitJumpInsn(Opcodes.IFNE, ok);
                            super.visitInsn(Opcodes.ICONST_0);
                            super.visitInsn(Opcodes.IRETURN);
                            super.visitLabel(ok);
                            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        }
                    };
                }
                return mv;
            }
        };
        cr.accept(cv, 0);
        Files.write(out, cw.toByteArray());
    }

    private static void patchHunter(Path in, Path out) throws Exception {
        ClassReader cr = new ClassReader(Files.readAllBytes(in));
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM8, cw) {
            @Override public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
                if (name.equals("use") && desc.equals("(Lnet/minecraft/class_1309;Ljava/lang/String;I)Ljava/util/Optional;")) {
                    return new MethodVisitor(Opcodes.ASM8, mv) {
                        @Override public void visitCode() {
                            super.visitCode();
                            Label ok = new Label();
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, AUTH, "canUseSkill", "(Lnet/minecraft/class_1309;Ljava/lang/String;)Z", false);
                            super.visitJumpInsn(Opcodes.IFNE, ok);
                            super.visitLdcInsn("compétence non débloquée");
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Optional", "of", "(Ljava/lang/Object;)Ljava/util/Optional;", false);
                            super.visitInsn(Opcodes.ARETURN);
                            super.visitLabel(ok);
                            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        }
                    };
                }
                return mv;
            }
        };
        cr.accept(cv, 0);
        Files.write(out, cw.toByteArray());
    }
}
