import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

public class PatchCastSafety {
    static final int ASM = Opcodes.ASM8;

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException("usage: PatchCastSafety <SpellParameters.class> <out SpellParameters.class> <ClientCastController.class> <out ClientCastController.class>");
        }
        patchSpellParameters(Path.of(args[0]), Path.of(args[1]));
        patchClientController(Path.of(args[2]), Path.of(args[3]));
    }

    private static void patchSpellParameters(Path in, Path out) throws Exception {
        ClassReader cr = new ClassReader(Files.readAllBytes(in));
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(ASM, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (name.equals("hasteAffectedValue") && desc.equals("(FF)F")) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
                    mv.visitCode();
                    Label validHaste = new Label();
                    Label neutralFallback = new Label();

                    mv.visitVarInsn(Opcodes.FLOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "isFinite", "(F)Z", false);
                    mv.visitJumpInsn(Opcodes.IFEQ, neutralFallback);

                    mv.visitVarInsn(Opcodes.FLOAD, 1);
                    mv.visitInsn(Opcodes.FCONST_0);
                    mv.visitInsn(Opcodes.FCMPG);
                    mv.visitJumpInsn(Opcodes.IFLE, neutralFallback);

                    mv.visitVarInsn(Opcodes.FLOAD, 1);
                    mv.visitLdcInsn(0.1f);
                    mv.visitInsn(Opcodes.FCMPG);
                    Label useProvidedHaste = new Label();
                    Label divide = new Label();
                    mv.visitJumpInsn(Opcodes.IFGE, useProvidedHaste);
                    mv.visitLdcInsn(0.1f);
                    mv.visitJumpInsn(Opcodes.GOTO, divide);

                    mv.visitLabel(useProvidedHaste);
                    mv.visitVarInsn(Opcodes.FLOAD, 1);
                    mv.visitLabel(divide);
                    mv.visitVarInsn(Opcodes.FSTORE, 3);
                    mv.visitJumpInsn(Opcodes.GOTO, validHaste);

                    mv.visitLabel(neutralFallback);
                    mv.visitVarInsn(Opcodes.FLOAD, 0);
                    mv.visitInsn(Opcodes.FRETURN);

                    mv.visitLabel(validHaste);
                    mv.visitVarInsn(Opcodes.FLOAD, 0);
                    mv.visitVarInsn(Opcodes.FLOAD, 3);
                    mv.visitInsn(Opcodes.FDIV);
                    mv.visitVarInsn(Opcodes.FSTORE, 2);

                    Label finiteResult = new Label();
                    mv.visitVarInsn(Opcodes.FLOAD, 2);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "isFinite", "(F)Z", false);
                    mv.visitJumpInsn(Opcodes.IFNE, finiteResult);
                    mv.visitVarInsn(Opcodes.FLOAD, 0);
                    mv.visitInsn(Opcodes.FRETURN);

                    mv.visitLabel(finiteResult);
                    mv.visitVarInsn(Opcodes.FLOAD, 2);
                    mv.visitInsn(Opcodes.FRETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                    return null;
                }
                return super.visitMethod(access, name, desc, sig, ex);
            }
        };
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        Files.write(out, cw.toByteArray());
    }

    private static void patchClientController(Path in, Path out) throws Exception {
        ClassReader cr = new ClassReader(Files.readAllBytes(in));
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(ASM, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor base = super.visitMethod(access, name, desc, sig, ex);
                if (!name.equals("keyHeld") || !desc.equals("(Lnet/spell_engine/internals/casting/SpellCast$Option;ZZ)Lnet/spell_engine/client/casting/ClientCastController$Reaction;")) {
                    return base;
                }
                return new MethodVisitor(ASM, base) {
                    int iload2Seen = 0;

                    @Override
                    public void visitVarInsn(int opcode, int var) {
                        if (opcode == Opcodes.ILOAD && var == 2) {
                            iload2Seen++;
                            if (iload2Seen == 1) {
                                Label useStopState = new Label();
                                Label done = new Label();

                                super.visitVarInsn(Opcodes.ALOAD, 1);
                                super.visitMethodInsn(
                                        Opcodes.INVOKEVIRTUAL,
                                        "net/spell_engine/internals/casting/SpellCast$Option",
                                        "mode",
                                        "()Lnet/spell_engine/internals/casting/SpellCast$Mode;",
                                        false);
                                super.visitFieldInsn(
                                        Opcodes.GETSTATIC,
                                        "net/spell_engine/internals/casting/SpellCast$Mode",
                                        "CHANNEL",
                                        "Lnet/spell_engine/internals/casting/SpellCast$Mode;");
                                super.visitJumpInsn(Opcodes.IF_ACMPNE, useStopState);

                                // START is debounced after a successful start until key-up.
                                super.visitVarInsn(Opcodes.ILOAD, 3);
                                super.visitJumpInsn(Opcodes.GOTO, done);

                                super.visitLabel(useStopState);
                                // Preserve upstream behavior for non-CHANNEL CASTING.
                                super.visitVarInsn(Opcodes.ILOAD, 2);

                                super.visitLabel(done);
                                return;
                            }
                        }
                        super.visitVarInsn(opcode, var);
                    }
                };
            }
        };
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        Files.write(out, cw.toByteArray());
    }
}
