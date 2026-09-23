import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

public class PatchRc12Migration implements Opcodes {
  static class SafeWriter extends ClassWriter {
    SafeWriter(ClassReader cr, int flags){ super(cr, flags); }
    @Override protected String getCommonSuperClass(String a,String b){ return "java/lang/Object"; }
  }
  static byte[] patch(byte[] in) {
    ClassReader cr = new ClassReader(in);
    SafeWriter cw = new SafeWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    ClassVisitor cv = new ClassVisitor(ASM9, cw) {
      @Override public MethodVisitor visitMethod(int access,String name,String desc,String sig,String[] ex) {
        MethodVisitor target = super.visitMethod(access,name,desc,sig,ex);
        if (!name.equals("migrate") || !desc.equals("(Lnet/spell_engine/config/HudConfig;)V")) return target;
        return new MethodVisitor(ASM9) {
          @Override public void visitEnd() {
            target.visitCode();
            Label ret=new Label(), x170=new Label(), x185=new Label(), migrate=new Label();
            target.visitVarInsn(ALOAD,0); target.visitJumpInsn(IFNULL,ret);
            target.visitVarInsn(ALOAD,0); target.visitFieldInsn(GETFIELD,"net/spell_engine/config/HudConfig","hotbar","Lnet/spell_engine/client/gui/HudElement;"); target.visitVarInsn(ASTORE,1);
            target.visitVarInsn(ALOAD,1); target.visitJumpInsn(IFNULL,ret);
            target.visitVarInsn(ALOAD,1); target.visitFieldInsn(GETFIELD,"net/spell_engine/client/gui/HudElement","origin","Lnet/spell_engine/client/gui/HudElement$Origin;");
            target.visitFieldInsn(GETSTATIC,"net/spell_engine/client/gui/HudElement$Origin","BOTTOM","Lnet/spell_engine/client/gui/HudElement$Origin;"); target.visitJumpInsn(IF_ACMPNE,ret);
            target.visitVarInsn(ALOAD,1); target.visitFieldInsn(GETFIELD,"net/spell_engine/client/gui/HudElement","offset","Lnet/minecraft/class_241;"); target.visitVarInsn(ASTORE,2);
            target.visitVarInsn(ALOAD,2); target.visitJumpInsn(IFNULL,ret);
            target.visitVarInsn(ALOAD,2); target.visitFieldInsn(GETFIELD,"net/minecraft/class_241","field_1343","F"); target.visitLdcInsn(-170.0F); target.visitInsn(FCMPL); target.visitJumpInsn(IFEQ,x170);
            target.visitVarInsn(ALOAD,2); target.visitFieldInsn(GETFIELD,"net/minecraft/class_241","field_1343","F"); target.visitLdcInsn(-185.0F); target.visitInsn(FCMPL); target.visitJumpInsn(IFEQ,x185);
            target.visitJumpInsn(GOTO,ret);

            target.visitLabel(x170);
            target.visitVarInsn(ALOAD,2); target.visitFieldInsn(GETFIELD,"net/minecraft/class_241","field_1342","F"); target.visitLdcInsn(-34.0F); target.visitInsn(FCMPL); target.visitJumpInsn(IFEQ,migrate);
            target.visitVarInsn(ALOAD,2); target.visitFieldInsn(GETFIELD,"net/minecraft/class_241","field_1342","F"); target.visitLdcInsn(-11.0F); target.visitInsn(FCMPL); target.visitJumpInsn(IFEQ,migrate);
            target.visitJumpInsn(GOTO,ret);

            target.visitLabel(x185);
            target.visitVarInsn(ALOAD,2); target.visitFieldInsn(GETFIELD,"net/minecraft/class_241","field_1342","F"); target.visitLdcInsn(-34.0F); target.visitInsn(FCMPL); target.visitJumpInsn(IFNE,ret);

            target.visitLabel(migrate);
            target.visitVarInsn(ALOAD,1); target.visitTypeInsn(NEW,"net/minecraft/class_241"); target.visitInsn(DUP); target.visitLdcInsn(-185.0F); target.visitLdcInsn(-11.0F);
            target.visitMethodInsn(INVOKESPECIAL,"net/minecraft/class_241","<init>","(FF)V",false);
            target.visitFieldInsn(PUTFIELD,"net/spell_engine/client/gui/HudElement","offset","Lnet/minecraft/class_241;");

            target.visitLabel(ret); target.visitInsn(RETURN); target.visitMaxs(0,0); target.visitEnd();
          }
        };
      }
    };
    cr.accept(cv, ClassReader.EXPAND_FRAMES);
    return cw.toByteArray();
  }
  public static void main(String[] args) throws Exception {
    Path p=Paths.get(args[0]); Files.write(p,patch(Files.readAllBytes(p)));
  }
}
