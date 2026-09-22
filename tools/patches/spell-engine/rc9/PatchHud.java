import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

public class PatchHud {
  static final int ASM = Opcodes.ASM8;

  public static void main(String[] args) throws Exception {
    Path in = Path.of(args[0]);
    Path outHud = Path.of(args[1]);
    Path outMig = Path.of(args[2]);
    byte[] bytes = Files.readAllBytes(in);
    ClassReader cr = new ClassReader(bytes);
    ClassWriter cw = new ClassWriter(0);
    ClassVisitor cv = new ClassVisitor(ASM, cw) {
      @Override public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
        MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
        if (name.equals("defaultHotBar") && desc.equals("()Lnet/spell_engine/client/gui/HudElement;")) {
          return new MethodVisitor(ASM, mv) {
            @Override public void visitLdcInsn(Object value) {
              if (value instanceof Float f && Float.compare(f, -170.0f) == 0) {
                super.visitLdcInsn(-185.0f);
              } else if (value instanceof Float f && Float.compare(f, -34.0f) == 0) {
                super.visitLdcInsn(-11.0f);
              } else {
                super.visitLdcInsn(value);
              }
            }
          };
        }
        return mv;
      }
    };
    cr.accept(cv, 0);
    Files.write(outHud, cw.toByteArray());

    ClassWriter m = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    m.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
        "net/spell_engine/config/HcHudMigration", null, "java/lang/Object", null);

    MethodVisitor init = m.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null);
    init.visitCode();
    init.visitVarInsn(Opcodes.ALOAD, 0);
    init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    init.visitInsn(Opcodes.RETURN);
    init.visitMaxs(0,0);
    init.visitEnd();

    MethodVisitor mv = m.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "migrate",
        "(Lnet/spell_engine/config/HudConfig;)V", null, null);
    mv.visitCode();
    Label ret = new Label();
    Label checkOldOriginal = new Label();
    Label migrate = new Label();

    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitJumpInsn(Opcodes.IFNULL, ret);
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/spell_engine/config/HudConfig", "hotbar", "Lnet/spell_engine/client/gui/HudElement;");
    mv.visitVarInsn(Opcodes.ASTORE, 1);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitJumpInsn(Opcodes.IFNULL, ret);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/spell_engine/client/gui/HudElement", "origin", "Lnet/spell_engine/client/gui/HudElement$Origin;");
    mv.visitFieldInsn(Opcodes.GETSTATIC, "net/spell_engine/client/gui/HudElement$Origin", "BOTTOM", "Lnet/spell_engine/client/gui/HudElement$Origin;");
    mv.visitJumpInsn(Opcodes.IF_ACMPNE, ret);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/spell_engine/client/gui/HudElement", "offset", "Lnet/minecraft/class_241;");
    mv.visitVarInsn(Opcodes.ASTORE, 2);
    mv.visitVarInsn(Opcodes.ALOAD, 2);
    mv.visitJumpInsn(Opcodes.IFNULL, ret);

    mv.visitVarInsn(Opcodes.ALOAD, 2);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/class_241", "field_1343", "F");
    mv.visitLdcInsn(-170.0f);
    mv.visitInsn(Opcodes.FCMPL);
    mv.visitJumpInsn(Opcodes.IFNE, ret);

    mv.visitVarInsn(Opcodes.ALOAD, 2);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/class_241", "field_1342", "F");
    mv.visitLdcInsn(-34.0f);
    mv.visitInsn(Opcodes.FCMPL);
    mv.visitJumpInsn(Opcodes.IFNE, checkOldOriginal);
    mv.visitJumpInsn(Opcodes.GOTO, migrate);

    mv.visitLabel(checkOldOriginal);
    mv.visitVarInsn(Opcodes.ALOAD, 2);
    mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/class_241", "field_1342", "F");
    mv.visitLdcInsn(-11.0f);
    mv.visitInsn(Opcodes.FCMPL);
    mv.visitJumpInsn(Opcodes.IFNE, ret);

    mv.visitLabel(migrate);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitTypeInsn(Opcodes.NEW, "net/minecraft/class_241");
    mv.visitInsn(Opcodes.DUP);
    mv.visitLdcInsn(-185.0f);
    mv.visitLdcInsn(-11.0f);
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/class_241", "<init>", "(FF)V", false);
    mv.visitFieldInsn(Opcodes.PUTFIELD, "net/spell_engine/client/gui/HudElement", "offset", "Lnet/minecraft/class_241;");

    mv.visitLabel(ret);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(0,0);
    mv.visitEnd();
    m.visitEnd();
    Files.write(outMig, m.toByteArray());
  }
}
