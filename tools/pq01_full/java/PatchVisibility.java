import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
public final class PatchVisibility {
 public static void main(String[] args)throws Exception{
  byte[] bytes=Files.readAllBytes(Path.of(args[0]));int major=((bytes[6]&255)<<8)|(bytes[7]&255);bytes[6]=0;bytes[7]=61;
  ClassReader reader=new ClassReader(bytes);ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);int[] changed={0};
  reader.accept(new ClassVisitor(Opcodes.ASM8,writer){
   public void visit(int version,int access,String name,String signature,String parent,String[] interfaces){super.visit(major,access,name,signature,parent,interfaces);}
   public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions){
    MethodVisitor base=super.visitMethod(access,name,descriptor,signature,exceptions);
    if(!name.equals("isVisibleTo")||!descriptor.equals("(Lnet/minecraft/class_1297;Lnet/minecraft/class_3222;)Z"))return base;
    changed[0]++;return new MethodVisitor(Opcodes.ASM8,base){public void visitCode(){super.visitCode();Label proceed=new Label();visitVarInsn(Opcodes.ALOAD,0);visitVarInsn(Opcodes.ALOAD,1);visitMethodInsn(Opcodes.INVOKESTATIC,"net/hautecapitale/quetes/sequence/Pq01Actors","visible","(Lnet/minecraft/class_1297;Lnet/minecraft/class_3222;)Z",false);visitJumpInsn(Opcodes.IFNE,proceed);visitInsn(Opcodes.ICONST_0);visitInsn(Opcodes.IRETURN);visitLabel(proceed);visitFrame(Opcodes.F_SAME,0,null,0,null);}};
   }
  },0);
  if(changed[0]!=1)throw new AssertionError("Visibility patch count="+changed[0]);Files.write(Path.of(args[1]),writer.toByteArray());System.out.println("PASS one scoped visibility guard; existing SI1b instructions retained.");
 }
}
