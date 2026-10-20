import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;

/** Produit seulement l'interface d'entrée Fabric, sans fournir de fausse API au compilateur. */
public final class BuildEntry {
  public static void main(String[] args)throws Exception {
    ClassWriter c=new ClassWriter(0);
    c.visit(Opcodes.V17,Opcodes.ACC_PUBLIC|Opcodes.ACC_FINAL,"net/indivis/admin/IndivisAdmin",null,"java/lang/Object",new String[]{"net/fabricmc/api/ModInitializer"});
    MethodVisitor init=c.visitMethod(Opcodes.ACC_PUBLIC,"<init>","()V",null,null);init.visitCode();init.visitVarInsn(Opcodes.ALOAD,0);
    init.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/Object","<init>","()V",false);init.visitInsn(Opcodes.RETURN);init.visitMaxs(1,1);init.visitEnd();
    MethodVisitor run=c.visitMethod(Opcodes.ACC_PUBLIC,"onInitialize","()V",null,null);run.visitCode();
    run.visitMethodInsn(Opcodes.INVOKESTATIC,"net/indivis/admin/FabricRuntime","init","()V",false);run.visitInsn(Opcodes.RETURN);run.visitMaxs(0,1);run.visitEnd();c.visitEnd();
    Path out=Path.of(args[0],"net/indivis/admin/IndivisAdmin.class");Files.createDirectories(out.getParent());Files.write(out,c.toByteArray());
  }
}
