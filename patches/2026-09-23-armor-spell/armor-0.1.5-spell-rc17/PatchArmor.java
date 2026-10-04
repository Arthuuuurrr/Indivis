import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

public class PatchArmor {
    public static void main(String[] args) throws Exception {
        Path in = Path.of(args[0]);
        Path out = Path.of(args[1]);
        byte[] bytes = Files.readAllBytes(in);
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, 0);
        final int[] hits = {0};

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM8, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, exceptions);
                if (!name.equals("attributesFrom")) return mv;
                return new MethodVisitor(Opcodes.ASM8, mv) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        super.visitFieldInsn(opcode, owner, name, descriptor);
                        if (opcode == Opcodes.GETFIELD
                                && owner.equals("net/spell_engine/rpg_series/config/ArmorSetConfig$Piece")
                                && name.equals("armor")
                                && descriptor.equals("I")) {
                            hits[0]++;
                            if (hits[0] == 2) {
                                super.visitInsn(Opcodes.ICONST_2);
                                super.visitInsn(Opcodes.IMUL);
                            }
                        }
                    }
                };
            }
        };

        cr.accept(cv, 0);
        if (hits[0] != 2) {
            throw new IllegalStateException("Expected exactly 2 Piece.armor reads in Armor.attributesFrom, found " + hits[0]);
        }
        Files.createDirectories(out.getParent());
        Files.write(out, cw.toByteArray());
        System.out.println("PATCH_OK Piece.armor reads=" + hits[0] + " multiplied-read=2");
    }
}
